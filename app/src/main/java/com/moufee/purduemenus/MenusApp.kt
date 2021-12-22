package com.moufee.purduemenus

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.Configuration
import com.moufee.purduemenus.di.MenusWorkerFactory
import com.moufee.purduemenus.preferences.AppPreferenceManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Inject

/**
 * The Application for this App
 * Allows Dagger Android injection
 * Sets night mode according to preferences when the app starts
 */
@HiltAndroidApp
class MenusApp : Application(), Configuration.Provider {

    @Inject
    lateinit var mSharedPreferences: SharedPreferences

    @Inject
    lateinit var mWorkerFactory: MenusWorkerFactory

    @Inject lateinit var preferenceManager: AppPreferenceManager

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder().setWorkerFactory(mWorkerFactory).build()
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

        runBlocking {
            when (preferenceManager.preferencesFlow.first().nightMode) {
                AppPreferences.NightMode.OFF -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                AppPreferences.NightMode.ON -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                AppPreferences.NightMode.FOLLOW_SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                else -> { }
            }
        }
    }
}