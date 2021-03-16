package com.moufee.purduemenus.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.moufee.purduemenus.BuildConfig
import com.moufee.purduemenus.api.DateTimeTypeAdapter
import com.moufee.purduemenus.api.LocalDateAdapter
import com.moufee.purduemenus.api.LocalTimeTypeAdapter
import com.moufee.purduemenus.api.Webservice
import com.moufee.purduemenus.db.AppDatabase
import com.moufee.purduemenus.db.FavoriteDao
import com.moufee.purduemenus.db.LocationDao
import com.moufee.purduemenus.util.AppExecutors
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

/**
 * Created by Ben on 28/07/2017.
 * The Dagger App Module, Provides dependencies for injection
 */
@Module(includes = [ViewModelModule::class])
internal class AppModule {
    @Singleton
    @Provides
    fun provideWebService(executors: AppExecutors, moshi: Moshi, client: OkHttpClient): Webservice {
        return Retrofit.Builder().baseUrl("https://api.hfs.purdue.edu")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .callbackExecutor(executors.diskIO())
                .build()
                .create(Webservice::class.java)
    }

    @Singleton
    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder().add(LocalTimeTypeAdapter())
                .add(DateTimeTypeAdapter())
                .add(LocalDateAdapter())
                .add(KotlinJsonAdapterFactory())
                .build()
    }

    @Singleton
    @Provides
    fun provideHttpClient(context: Context): OkHttpClient {
        //todo: maybe don't use shared prefs, possibly use custom implementation
        val builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }
        builder.cookieJar(PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(context)))
        return builder.build()
    }

    @Singleton
    @Provides
    fun sharedPreferences(application: Application): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(application)
    }

    @Singleton
    @Provides
    fun provideContext(application: Application): Context = application.applicationContext

    @Provides
    fun provideAnalytics(context: Context): FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    @Provides
    fun provideCrashlytics(): FirebaseCrashlytics = FirebaseCrashlytics.getInstance()

    @Singleton
    @Provides
    fun provideAppDatabase(applicationContext: Context): AppDatabase {
        return Room.databaseBuilder(applicationContext, AppDatabase::class.java, "menus-database")
                .addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3)
                .fallbackToDestructiveMigration()
                .build()
    }

    @Provides
    fun provideFavoriteDao(appDatabase: AppDatabase): FavoriteDao = appDatabase.favoriteDao()

    @Provides
    fun provideLocationDao(appDatabase: AppDatabase): LocationDao = appDatabase.locationDao()

    @Singleton
    @Provides
    fun provideUpdateManager(context: Context): AppUpdateManager = AppUpdateManagerFactory.create(context)
}