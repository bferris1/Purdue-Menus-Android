package com.moufee.purduemenus.preferences

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import timber.log.Timber
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
    val hideClosedDiningCourts: Boolean,
)


@Singleton
class AppPreferenceManager @Inject constructor(
    private val dataStore: DataStore<com.moufee.purduemenus.AppPreferences>,
) {
    val preferencesFlow = dataStore.data

    suspend fun setShowServingTimes(showServingTimes: Boolean) {
        Timber.d("Setting showServingTimes to $showServingTimes")
        dataStore.updateData { preferences ->
            preferences.toBuilder().setShowServingTimes(showServingTimes).build()
        }.also { Timber.d(it.toString()) }
    }

    suspend fun setShowFavoriteCounts(showFavoriteCounts: Boolean) {
        dataStore.updateData { preferences ->
            preferences.toBuilder().setShowFavoriteCounts(showFavoriteCounts).build()
        }
    }

    suspend fun setHideClosedDiningCourts(hideClosedDiningCourts: Boolean) {
        dataStore.updateData { preferences ->
            preferences.toBuilder().setHideClosedDiningCourts(hideClosedDiningCourts).build()
        }
    }

    suspend fun setNightMode(nightMode: com.moufee.purduemenus.AppPreferences.NightMode) {
        dataStore.updateData {
            it.toBuilder().setNightMode(nightMode).build()
        }
    }

    fun getCurrentPreferences() = runBlocking { dataStore.data.first() }
}