package com.moufee.purduemenus.di

import com.moufee.purduemenus.api.DownloadWorker
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class WorkerModule {

    @Binds
    @IntoMap
    @WorkerKey(DownloadWorker::class)
    internal abstract fun bindDownloadWorkerFactory(worker: DownloadWorker.Factory): ChildWorkerFactory
}