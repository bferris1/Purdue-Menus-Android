package com.moufee.purduemenus;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.work.Configuration;

import com.moufee.purduemenus.di.DaggerAppComponent;
import com.moufee.purduemenus.di.MenusWorkerFactory;
import com.moufee.purduemenus.preferences.AppPreferencesKt;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;
import timber.log.Timber;

/**
 * The Application for this App
 * Allows Dagger Android injection
 * Sets night mode according to preferences when the app starts
 */

public class MenusApp extends Application implements HasAndroidInjector, Configuration.Provider {

    @Inject
    DispatchingAndroidInjector<Object> mDispatchingAndroidInjector;

    @Inject
    SharedPreferences mSharedPreferences;

    @Override
    public AndroidInjector<Object> androidInjector() {
        return mDispatchingAndroidInjector;
    }

    @Inject
    MenusWorkerFactory mWorkerFactory;

    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder().setWorkerFactory(mWorkerFactory).build();
    }

    @Override
    public void onCreate() {

        DaggerAppComponent.builder().application(this).build().inject(this);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        switch (mSharedPreferences.getString(AppPreferencesKt.KEY_PREF_USE_NIGHT_MODE, "")) {
            case "mode_off":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "mode_on":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
        super.onCreate();

    }
}
