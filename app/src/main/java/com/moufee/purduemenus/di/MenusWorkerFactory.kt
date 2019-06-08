package com.moufee.purduemenus.di

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject
import javax.inject.Provider

class MenusWorkerFactory @Inject constructor(
        val workers: Map<Class<out ListenableWorker>, @JvmSuppressWildcards Provider<ChildWorkerFactory>>
) : WorkerFactory() {
    override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker? {
        for (worker in workers) {
            if (worker.key.name == workerClassName) {
                return worker.value.get().create(appContext, workerParameters)
            }
        }
        return null
    }
}