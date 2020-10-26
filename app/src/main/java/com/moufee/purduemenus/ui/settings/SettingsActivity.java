package com.moufee.purduemenus.ui.settings;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.moufee.purduemenus.preferences.AppPreferencesKt;
import com.moufee.purduemenus.repository.FavoritesRepository;
import com.moufee.purduemenus.util.SingleFragmentActivity;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

/**
 * Settings Activity
 */

public class SettingsActivity extends SingleFragmentActivity implements SharedPreferences.OnSharedPreferenceChangeListener, HasAndroidInjector {

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    FavoritesRepository mFavoritesRepository;

    @Inject
    DispatchingAndroidInjector<Object> mDispatchingAndroidInjector;


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
        if (key.equals(AppPreferencesKt.KEY_PREF_USE_NIGHT_MODE)) {
            String value = sharedPreferences.getString(key, "");
            switch (value) {
                case "mode_off":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    break;

                case "mode_on":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                    break;
                case "mode_auto":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    break;
            }
        }
    }

    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return mDispatchingAndroidInjector;
    }
}
