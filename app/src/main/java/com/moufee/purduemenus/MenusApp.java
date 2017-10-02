package com.moufee.purduemenus;

import android.app.Application;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatDelegate;

import com.moufee.purduemenus.di.AppComponent;
import com.moufee.purduemenus.di.AppModule;
import com.moufee.purduemenus.di.DaggerAppComponent;
import com.moufee.purduemenus.ui.settings.SettingsFragment;

/**
 * Created by Ben on 25/08/2017.
 */

public class MenusApp extends Application {
    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        switch (PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsFragment.KEY_PREF_USE_NIGHT_MODE, "")){
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
        super.onCreate();
        mAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    }

    public AppComponent getAppComponent(){
        return mAppComponent;
    }
}
