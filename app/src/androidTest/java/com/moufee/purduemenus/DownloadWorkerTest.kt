package com.moufee.purduemenus

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.moufee.purduemenus.api.DownloadWorker
import org.junit.Before
import org.junit.Test


class DownloadWorkerTest {

    @Before
    fun setup() {

    }

    @Test
    fun testSimpleDownloadWorker() {
        val request = OneTimeWorkRequestBuilder<DownloadWorker>().build()

        val workManager = WorkManager.getInstance()

        workManager.enqueue(request)
    }
}