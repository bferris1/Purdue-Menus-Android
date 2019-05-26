package com.moufee.purduemenus.api

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.moufee.purduemenus.db.LocationDao
import com.moufee.purduemenus.di.ChildWorkerFactory
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import javax.inject.Inject


class DownloadWorker(
        val webservice: Webservice,
        private val locationDao: LocationDao,
        private val downloader: MenuDownloader,
        private val appContext: Context,
        private val workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")
        //do the work
        //download menus day by day and save to disk
        // need a Webservice at least
        // need to a Downloader class that gets all menus and specifies what to do next
        val locations = locationDao.getAllList()
        val today = DateTime.now()
        val oneWeek = today.plusDays(7)
        var current = today
        while (current.isBefore(oneWeek.toInstant())) {
            val menu = downloader.getMenus(current, locations).blockingGet()
            val filesDir = appContext.getCacheDir()
            //todo: refactor save to be reused
            val outputFile = File(filesDir, formatter.print(current) + ".fdm")
            val fileOutputStream = FileOutputStream(outputFile)
            val objectOutputStream = ObjectOutputStream(fileOutputStream)
            objectOutputStream.writeObject(menu)
            objectOutputStream.close()
            fileOutputStream.close()
            current = current.plusDays(1)
        }
        return Result.success()
    }


    class Factory @Inject constructor(val webservice: Webservice, private val locationDao: LocationDao, private val downloader: MenuDownloader) : ChildWorkerFactory {
        override fun create(appContext: Context, params: WorkerParameters): Worker {
            return DownloadWorker(webservice, locationDao, downloader, appContext, params)
        }
    }
}