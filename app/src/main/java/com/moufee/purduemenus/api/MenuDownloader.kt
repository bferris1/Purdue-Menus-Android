package com.moufee.purduemenus.api

import com.moufee.purduemenus.menus.Location
import com.moufee.purduemenus.repository.data.ApiDiningCourtMenu
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MenuDownloader @Inject constructor(val webservice: Webservice) {

    private val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")

    suspend fun getMenus(date: LocalDate, locations: List<Location>): List<ApiDiningCourtMenu> = supervisorScope {
        val locationNames = locations.map { it.Name }
        val deferredResponses = locationNames.map {
            async {
                webservice.getMenu(it, formatter.print(date))
            }
        }
        deferredResponses.mapNotNull {
            try {
                it.await()
            } catch (t: Throwable) {
                Timber.e(t, "Network error.")
                null
            }
        }
    }
}