package com.moufee.purduemenus.api

import android.content.Context
import com.moufee.purduemenus.menus.DiningCourtMenu
import com.moufee.purduemenus.menus.FullDayMenu
import com.moufee.purduemenus.menus.Location
import io.reactivex.Single
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MenuDownloader @Inject constructor(val webservice: Webservice, private val context: Context) {

    private val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")

    fun getMenus(date: DateTime, locations: List<Location>): Single<FullDayMenu> {
        val locationNames = locations.map { it.Name }
        val requests: List<Single<DiningCourtMenu>> = locationNames.map { webservice.getMenu(it, formatter.print(date)) }
        return Single.zip(requests) { arrayOfAnys -> FullDayMenu((arrayOfAnys.map { it as DiningCourtMenu }).toList(), date) }
    }
}