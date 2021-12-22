package com.moufee.purduemenus.preferences

import android.content.Context
import androidx.datastore.core.Serializer
import androidx.datastore.migrations.SharedPreferencesMigration
import androidx.datastore.migrations.SharedPreferencesView
import androidx.preference.PreferenceManager
import com.moufee.purduemenus.AppPreferences
import timber.log.Timber
import java.io.InputStream
import java.io.OutputStream

object AppPreferencesSerializer : Serializer<AppPreferences> {
    override val defaultValue: AppPreferences = AppPreferences.newBuilder()
        .setHideClosedDiningCourts(true)
        .setShowFavoriteCounts(true)
        .setShowVegetarianIcons(true)
        .setShowServingTimes(true)
        .build()

    override suspend fun readFrom(input: InputStream): AppPreferences {
        return AppPreferences.parseFrom(input)
    }

    override suspend fun writeTo(t: AppPreferences, output: OutputStream) {
        return t.writeTo(output)
    }
}

fun getAppPreferencesMigration(context: Context) =
    SharedPreferencesMigration<AppPreferences>(
        produceSharedPreferences = { PreferenceManager.getDefaultSharedPreferences(context) },
        keysToMigrate = setOf(KEY_PREF_HIDE_CLOSED_LOCATIONS, KEY_PREF_SHOW_FAVORITE_COUNT, KEY_PREF_SHOW_SERVING_TIMES, KEY_PREF_USE_NIGHT_MODE)
    )
    { prefs: SharedPreferencesView, appPrefs: AppPreferences ->
        appPrefs.toBuilder()
            .setHideClosedDiningCourts(prefs.getBoolean(KEY_PREF_HIDE_CLOSED_LOCATIONS, true))
            .setShowFavoriteCounts(prefs.getBoolean(KEY_PREF_SHOW_FAVORITE_COUNT, true))
            .setShowServingTimes(prefs.getBoolean(KEY_PREF_SHOW_SERVING_TIMES, true))
            .setNightMode(nightModeFromPrefString(prefs.getString(KEY_PREF_USE_NIGHT_MODE, "")))
            .build().also { Timber.d("Migration Result: $it") }
    }