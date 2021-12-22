package com.moufee.purduemenus.di

import com.moufee.purduemenus.workers.DownloadWorker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap

@Module
@InstallIn(SingletonComponent::class)
abstract class WorkerModule {

    @Binds
    @IntoMap
    @WorkerKey(DownloadWorker::class)
    internal abstract fun bindDownloadWorkerFactory(worker: DownloadWorker.Factory): ChildWorkerFactory
}