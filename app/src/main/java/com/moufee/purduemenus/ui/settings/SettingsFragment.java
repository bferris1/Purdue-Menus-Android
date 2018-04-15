package com.moufee.purduemenus.ui.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.moufee.purduemenus.R;
import com.moufee.purduemenus.repository.FavoritesRepository;
import com.moufee.purduemenus.ui.login.LoginActivity;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

/**
 * Displays preferences for the app
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {


    public static final String KEY_PREF_SHOW_SERVING_TIMES = "show_serving_times";
    public static final String KEY_PREF_USE_NIGHT_MODE = "night_mode";
    public static final String KEY_PREF_LOGGED_IN = "logged_in";
    private static final String TAG = "SettingsFragment";
    @Inject
    SharedPreferences mSharedPreferences;
    @Inject
    FavoritesRepository mFavoritesRepository;
    private Preference mLoginPref;

    @Override
    public void onAttach(Context context) {
        AndroidInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);


        mLoginPref = findPreference("log_in");
        Preference sortOrderPref = findPreference("dining_court_order");
        sortOrderPref.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(getActivity(), CustomOrderActivity.class));
            return true;
        });


        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        updateLoginPreference();

        mLoginPref.setOnPreferenceClickListener(preference -> {
            boolean isLoggedIn = mSharedPreferences.getBoolean("logged_in", false);
            if (isLoggedIn) {
                showLogoutPrompt();
                return true;
            } else {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                return true;
            }
        });

        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);

    }

    private void showLogoutPrompt() {
        AlertDialog logoutDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Clear Favorites?")
                .setMessage("Would you also like to clear your local favorites, or just stop synchronizing them?")
                .setPositiveButton("Clear Favorites", (dialog, which) -> {
                    mFavoritesRepository.clearLocalFavorites();
                    logout();
                })
                .setCancelable(true)
                .setNegativeButton("Just Log Out", ((dialog, which) -> logout()))
                .create();
        logoutDialog.setOnShowListener(dialog -> logoutDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.RED));
        logoutDialog.show();
    }

    private void logout() {
        mSharedPreferences
                .edit()
                .putBoolean(KEY_PREF_LOGGED_IN, false)
                .putString("username", null)
                .putString("password", null)
                .apply();
    }

    private void updateLoginPreference() {
        boolean isLoggedIn = mSharedPreferences.getBoolean("logged_in", false);
        if (isLoggedIn) {
            mLoginPref.setTitle("Sign Out");
            mLoginPref.setSummary("You are signed in as " + mSharedPreferences.getString("username", "user"));
        } else {
            mLoginPref.setTitle(R.string.action_login);
            mLoginPref.setSummary(R.string.pref_summary_not_logged_in);
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_PREF_LOGGED_IN))
            updateLoginPreference();
    }
}
