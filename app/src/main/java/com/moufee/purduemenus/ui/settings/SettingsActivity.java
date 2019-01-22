package com.moufee.purduemenus.ui.settings;


import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;

import com.moufee.purduemenus.repository.FavoritesRepository;
import com.moufee.purduemenus.util.SingleFragmentActivity;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasFragmentInjector;

/**
 * Settings Activity
 */

public class SettingsActivity extends SingleFragmentActivity implements SharedPreferences.OnSharedPreferenceChangeListener, HasFragmentInjector {
    public static final String KEY_PREF_SHOW_SERVING_TIMES = "show_serving_times";
    public static final String KEY_PREF_USE_NIGHT_MODE = "night_mode";
    public static final String KEY_PREF_SHOW_FAVORITE_COUNT = "show_favorite_count";

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    FavoritesRepository mFavoritesRepository;

    @Inject
    DispatchingAndroidInjector<Fragment> mDispatchingAndroidInjector;


    @Override
    public void recreate() {
        finish();
        overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
        startActivity(getIntent());
        overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
    }

    public static Intent getIntent(Context packageContext) {
        return new Intent(packageContext, SettingsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPause() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    protected Fragment createFragment() {
        return new SettingsFragment();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_PREF_USE_NIGHT_MODE)) {
            String value = sharedPreferences.getString(key, "");
            switch (value) {
                case "mode_off":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    break;

                case "mode_on":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                    break;
                case "mode_auto":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
                    //permissions are needed
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                            //do something?
                        } else {
                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                    0);
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    recreate();
                } else {
//                    recreate();
                }
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public AndroidInjector<Fragment> fragmentInjector() {
        return mDispatchingAndroidInjector;
    }
}
