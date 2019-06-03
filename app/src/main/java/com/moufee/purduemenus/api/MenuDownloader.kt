package com.moufee.purduemenus.api

import android.util.Log
import com.moufee.purduemenus.menus.DiningCourtMenu
import com.moufee.purduemenus.menus.FullDayMenu
import com.moufee.purduemenus.menus.Location
import io.reactivex.Single
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MenuDownloader @Inject constructor(val webservice: Webservice) {

    private val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")

    fun getMenus(date: DateTime, locations: List<Location>): Single<FullDayMenu> {
        val locationNames = locations.map { it.Name }
        val requests: List<Single<Any>> = locationNames.map { location ->
            (webservice.getMenu(location, formatter.print(date)) as Single<Any>).onErrorReturn {
                Log.e("MenuDownloader", "Error downloading menu", it)
                Any()
            }
        }
        return Single.zip(requests) { arrayOfAnys ->
            FullDayMenu((arrayOfAnys.mapNotNull {
                if (it is DiningCourtMenu) it else null
            }), date)
        }
    }
}