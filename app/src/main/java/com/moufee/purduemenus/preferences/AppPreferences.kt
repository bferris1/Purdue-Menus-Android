package com.moufee.purduemenus.preferences

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import javax.inject.Inject
import javax.inject.Singleton

const val KEY_PREF_SHOW_SERVING_TIMES = "show_serving_times"
const val KEY_PREF_SHOW_FAVORITE_COUNT = "show_favorite_count"
const val KEY_PREF_HIDE_CLOSED_LOCATIONS = "hide_closed_locations"
const val KEY_PREF_USE_NIGHT_MODE = "night_mode"
const val KEY_PREF_LOGGED_IN = "logged_in"
const val KEY_PREF_USERNAME = "username"
const val KEY_PREF_PASSWORD = "password"
const val KEY_PREF_DINING_COURT_ORDER = "dining_court_order"
const val PREF_LOG_IN = "log_in"
const val KEY_PREF_PRIVACY_POLICY = "privacy_policy"

data class AppPreferences(
        val showServingTimes: Boolean,
        val showFavoriteCounts: Boolean,
        val showVegetarianIcons: Boolean,
        val hideClosedDiningCourts: Boolean
)


@Singleton
class AppPreferenceManager @Inject constructor(sharedPreferences: SharedPreferences) {
    val preferences: LiveData<AppPreferences> = AppPreferenceLiveData(sharedPreferences)
}


private class AppPreferenceLiveData(private val sharedPreferences: SharedPreferences) : LiveData<AppPreferences>() {

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, _ ->
        value = sharedPreferences.toAppPreferences()
    }

    override fun onActive() {
        value = sharedPreferences.toAppPreferences()
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onInactive() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }
}

private fun SharedPreferences.toAppPreferences() = AppPreferences(
        showServingTimes = getBoolean(KEY_PREF_SHOW_SERVING_TIMES, true),
        showFavoriteCounts = getBoolean(KEY_PREF_SHOW_FAVORITE_COUNT, true),
        showVegetarianIcons = true,
        hideClosedDiningCourts = getBoolean(KEY_PREF_HIDE_CLOSED_LOCATIONS, true)
)