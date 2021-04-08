package com.moufee.purduemenus

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.Configuration
import com.moufee.purduemenus.di.MenusWorkerFactory
import com.moufee.purduemenus.preferences.KEY_PREF_USE_NIGHT_MODE
import dagger.hilt.android.HiltAndroidApp
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

    @JvmField @Inject
    var mWorkerFactory: MenusWorkerFactory? = null
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder().setWorkerFactory(mWorkerFactory!!).build()
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

        when (mSharedPreferences.getString(KEY_PREF_USE_NIGHT_MODE, "")) {
            "mode_off" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "mode_on" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}