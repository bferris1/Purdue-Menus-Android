package com.moufee.purduemenus.api

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.moufee.purduemenus.db.LocationDao
import com.moufee.purduemenus.di.ChildWorkerFactory
import org.joda.time.DateTime
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
        val today = DateTime.now()
        val oneWeek = today.plusDays(7)
        var current = today

        while (current.isBefore(oneWeek.toInstant())) {
            // todo: don't block here?
            val menu = downloader.getMenus(current, locations)
            try {
                Log.d(TAG, "Caching menu" + menu.date.toString())
                menuCache.put(menu)
            } catch (e: Exception) {
                Log.e("DownloadWorker", "error:", e)
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