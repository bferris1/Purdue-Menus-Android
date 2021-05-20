package com.moufee.purduemenus.di

import com.moufee.purduemenus.repository.FavoritesDataSource
import com.moufee.purduemenus.repository.LocalFavoritesDataSource
import com.moufee.purduemenus.repository.RemoteFavoritesDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Named(FavoritesDataSource.REMOTE)
    abstract fun provideRemoteDataSource(remoteFavoritesDataSource: RemoteFavoritesDataSource): FavoritesDataSource
    @Binds
    @Named(FavoritesDataSource.LOCAL)
    abstract fun provideLocalDataSource(localFavoritesDataSource: LocalFavoritesDataSource): FavoritesDataSource
}