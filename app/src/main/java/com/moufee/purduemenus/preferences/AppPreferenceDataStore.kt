package com.moufee.purduemenus.preferences

import androidx.preference.PreferenceDataStore
import com.moufee.purduemenus.AppPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AppPreferenceDataStore(private val coroutineScope: CoroutineScope, private val preferenceManager: AppPreferenceManager) :
    PreferenceDataStore() {
    override fun putBoolean(key: String?, value: Boolean) {
        when (key) {
            KEY_PREF_SHOW_SERVING_TIMES -> coroutineScope.launch { preferenceManager.setShowServingTimes(value) }
            KEY_PREF_HIDE_CLOSED_LOCATIONS -> coroutineScope.launch { preferenceManager.setHideClosedDiningCourts(value) }
            KEY_PREF_SHOW_FAVORITE_COUNT -> coroutineScope.launch { preferenceManager.setShowFavoriteCounts(value) }
        }
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        val prefs = preferenceManager.getCurrentPreferences()
        return when (key) {
            KEY_PREF_SHOW_SERVING_TIMES -> prefs.showServingTimes
            KEY_PREF_HIDE_CLOSED_LOCATIONS -> prefs.hideClosedDiningCourts
            KEY_PREF_SHOW_FAVORITE_COUNT -> prefs.showFavoriteCounts
            else -> false
        }
    }

    override fun putString(key: String?, value: String?) {
        when (key) {
            KEY_PREF_USE_NIGHT_MODE -> {
                coroutineScope.launch { preferenceManager.setNightMode(nightModeFromPrefString(value)) }
            }
        }
    }



    override fun getString(key: String?, defValue: String?): String? {
        return when (key) {
            KEY_PREF_USE_NIGHT_MODE -> {
                when (preferenceManager.getCurrentPreferences().nightMode) {
                    AppPreferences.NightMode.OFF -> "mode_off"
                    AppPreferences.NightMode.ON -> "mode_on"
                    AppPreferences.NightMode.FOLLOW_SYSTEM -> "mode_auto"
                    else -> super.getString(key, defValue)
                }
            }
            else -> super.getString(key, defValue)
        }
    }
}

fun nightModeFromPrefString(pref: String?) = when (pref) {
    "mode_off" -> AppPreferences.NightMode.OFF
    "mode_on" -> AppPreferences.NightMode.ON
    else -> AppPreferences.NightMode.FOLLOW_SYSTEM
}