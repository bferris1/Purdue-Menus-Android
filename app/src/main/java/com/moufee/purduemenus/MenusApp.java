package com.moufee.purduemenus;

import android.app.Activity;
import android.app.Application;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;

import com.moufee.purduemenus.di.DaggerAppComponent;
import com.moufee.purduemenus.ui.settings.SettingsFragment;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

/**
 * Created by Ben on 25/08/2017.
 */

public class MenusApp extends Application implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<Activity> mDispatchingAndroidInjector;

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return mDispatchingAndroidInjector;
    }

    @Override
    public void onCreate() {
        switch (PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsFragment.KEY_PREF_USE_NIGHT_MODE, "")) {
            case "mode_off":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case "mode_on":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case "mode_auto":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
        DaggerAppComponent.builder().application(this).build().inject(this);
        super.onCreate();

    }
}
