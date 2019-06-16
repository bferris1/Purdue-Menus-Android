package com.moufee.purduemenus.api

import com.moufee.purduemenus.menus.FullDayMenu
import com.moufee.purduemenus.menus.Location
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MenuDownloader @Inject constructor(val webservice: Webservice) {

    private val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")

    suspend fun getMenus(date: DateTime, locations: List<Location>): FullDayMenu = coroutineScope {
        val locationNames = locations.map { it.Name }
        val deferred = locationNames.map {
            async {
                Timber.d("Getting menu - ${Thread.currentThread().name}")
                webservice.getMenu(it, formatter.print(date))
            }
        }
        val locationResponses = deferred.awaitAll()
        Timber.d("Got all menus - ${Thread.currentThread().name}")
        FullDayMenu(locationResponses, date)
    }
}