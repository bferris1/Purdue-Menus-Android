package com.moufee.purduemenus.api

import com.moufee.purduemenus.menus.FullDayMenu
import com.moufee.purduemenus.menus.Location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MenuDownloader @Inject constructor(val webservice: Webservice) {

    private val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")

    suspend fun getMenus(date: DateTime, locations: List<Location>): FullDayMenu {
        val locationNames = locations.map { it.Name }
        val jobs = locationNames.map { CoroutineScope(Dispatchers.IO).async { webservice.getMenu(it, formatter.print(date)) } }
        val locationResponses = jobs.awaitAll()
        return FullDayMenu(locationResponses, date)
    }
}