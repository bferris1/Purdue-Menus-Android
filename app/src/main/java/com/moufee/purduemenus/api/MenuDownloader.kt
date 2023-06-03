package com.moufee.purduemenus.api

import com.moufee.purduemenus.api.models.RemoteDiningCourtMenu
import com.moufee.purduemenus.repository.data.menus.Location
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

    suspend fun getMenus(date: LocalDate, locations: List<Location>): List<RemoteDiningCourtMenu> = supervisorScope {
        val locationNames = locations.map { it.Name }
        var lastException: Throwable? = null
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
                lastException = t
                null
            }
        }.also { menuList ->
            if (menuList.isEmpty()) {
                lastException?.let { throw it }
            }
        }
    }
}