package com.moufee.purduemenus;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.moufee.purduemenus.di.AppComponent;
import com.moufee.purduemenus.di.DaggerAppComponent;

/**
 * Created by Ben on 25/08/2017.
 */

public class MenusApp extends Application {

    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
    }

    private AppComponent mAppComponent;
    @Override
    public void onCreate() {
        super.onCreate();
        mAppComponent = DaggerAppComponent.create();
    }

    public AppComponent getAppComponent(){
        return mAppComponent;
    }
}
