package com.moufee.purduemenus.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.moufee.purduemenus.api.MenuCache
import com.moufee.purduemenus.api.MenuDownloader
import com.moufee.purduemenus.api.Webservice
import com.moufee.purduemenus.db.LocationDao
import com.moufee.purduemenus.di.ChildWorkerFactory
import com.moufee.purduemenus.repository.toDayMenu
import org.joda.time.LocalDate
import timber.log.Timber
import javax.inject.Inject


class DownloadWorker(
        val webservice: Webservice,
        private val locationDao: LocationDao,
        private val downloader: MenuDownloader,
        private val menuCache: MenuCache,
        private val appContext: Context,
        private val workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        //download menus day by day and save to disk
        val locations = locationDao.getAllList()
        val today = LocalDate.now()
        val oneWeek = today.plusDays(7)
        var current = today

        while (current.isBefore(oneWeek)) {
            // todo: don't block here?
            val menu = downloader.getMenus(current, locations).toDayMenu(current)
            try {
                Timber.d("Caching menu %s", menu.date.toString())
                menuCache.put(menu)
            } catch (e: Exception) {
                Timber.e(e, "error:")
            }
            current = current.plusDays(1)
        }
        return Result.success()
    }


    class Factory @Inject constructor(val webservice: Webservice, private val locationDao: LocationDao, private val downloader: MenuDownloader, private val menuCache: MenuCache) : ChildWorkerFactory {
        override fun create(appContext: Context, params: WorkerParameters): ListenableWorker {
            return DownloadWorker(webservice = webservice, locationDao = locationDao, downloader = downloader, menuCache = menuCache, appContext = appContext, workerParams = params)
        }
    }
}