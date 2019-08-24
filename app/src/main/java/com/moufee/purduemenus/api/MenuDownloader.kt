package com.moufee.purduemenus.api

import com.moufee.purduemenus.menus.FullDayMenu
import com.moufee.purduemenus.menus.Location
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MenuDownloader @Inject constructor(val webservice: Webservice) {

    private val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")

    suspend fun getMenus(date: DateTime, locations: List<Location>): FullDayMenu = supervisorScope {
        val locationNames = locations.map { it.Name }
        val deferred = locationNames.map {
            async {
                webservice.getMenu(it, formatter.print(date))
            }
        }
        val mapped = deferred.mapNotNull {
            try {
                it.await()
            } catch (t: Throwable) {
                Timber.e(t, "Network error.")
                null
            }
        }
        FullDayMenu(mapped, date)
    }
}