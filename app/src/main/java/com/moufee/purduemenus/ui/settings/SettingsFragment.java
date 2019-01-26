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

import com.moufee.purduemenus.R;
import com.moufee.purduemenus.repository.FavoritesRepository;
import com.moufee.purduemenus.ui.login.LoginActivity;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import dagger.android.AndroidInjection;

/**
 * Displays preferences for the app
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {


    public static final String KEY_PREF_SHOW_SERVING_TIMES = "show_serving_times";
    public static final String KEY_PREF_USE_NIGHT_MODE = "night_mode";
    public static final String KEY_PREF_LOGGED_IN = "logged_in";
    public static final String KEY_PREF_USERNAME = "username";
    public static final String KEY_PREF_PASSWORD = "password";
    public static final String KEY_PREF_DINING_COURT_ORDER = "dining_court_order";
    public static final String PREF_LOG_IN = "log_in";

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


        mLoginPref = findPreference(PREF_LOG_IN);
        Preference sortOrderPref = findPreference(KEY_PREF_DINING_COURT_ORDER);
        sortOrderPref.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(getActivity(), CustomOrderActivity.class));
            return true;
        });


        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        updateLoginPreference();

        mLoginPref.setOnPreferenceClickListener(preference -> {
            boolean isLoggedIn = mSharedPreferences.getBoolean(KEY_PREF_LOGGED_IN, false);
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
                .setTitle(R.string.title_prompt_clear_favorites)
                .setMessage(R.string.prompt_clear_local_favorites)
                .setPositiveButton(R.string.action_clear_favorites, (dialog, which) -> {
                    mFavoritesRepository.clearLocalFavorites();
                    logout();
                })
                .setCancelable(true)
                .setNegativeButton(R.string.action_only_logout, ((dialog, which) -> logout()))
                .create();
        logoutDialog.setOnShowListener(dialog -> logoutDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.RED));
        logoutDialog.show();
    }

    private void logout() {
        mSharedPreferences
                .edit()
                .putBoolean(KEY_PREF_LOGGED_IN, false)
                .putString(KEY_PREF_USERNAME, null)
                .putString(KEY_PREF_PASSWORD, null)
                .apply();
    }

    private void updateLoginPreference() {
        boolean isLoggedIn = mSharedPreferences.getBoolean(KEY_PREF_LOGGED_IN, false);
        if (isLoggedIn) {
            mLoginPref.setTitle(R.string.action_sign_out);
            mLoginPref.setSummary(getString(R.string.description_signed_in, mSharedPreferences.getString(KEY_PREF_USERNAME, "user")));
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
