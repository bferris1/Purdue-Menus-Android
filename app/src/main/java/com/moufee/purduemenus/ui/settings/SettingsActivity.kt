package com.moufee.purduemenus.ui.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.content.edit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.moufee.purduemenus.AppPreferences
import com.moufee.purduemenus.preferences.AppPreferenceManager
import com.moufee.purduemenus.preferences.AppPreferencesSerializer
import com.moufee.purduemenus.preferences.KEY_PREF_LOGGED_IN
import com.moufee.purduemenus.preferences.KEY_PREF_PASSWORD
import com.moufee.purduemenus.preferences.KEY_PREF_USERNAME
import com.moufee.purduemenus.repository.FavoritesRepository
import com.moufee.purduemenus.ui.login.LoginActivity
import com.moufee.purduemenus.ui.theme.MenusTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Settings Activity
 */
@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    @Inject
    lateinit var appPreferenceManager: AppPreferenceManager

    @Inject
    lateinit var mSharedPreferences: SharedPreferences

    @Inject
    lateinit var mFavoritesRepository: FavoritesRepository

    override fun recreate() {
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                appPreferenceManager.preferencesFlow.collect {
                    when (it.nightMode) {
                        AppPreferences.NightMode.OFF -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        AppPreferences.NightMode.ON -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        AppPreferences.NightMode.FOLLOW_SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                        else -> { }
                    }
                }
            }
        }
        setContent {
            MenusTheme {
                val preferences by appPreferenceManager.preferencesFlow
                    .collectAsState(initial = AppPreferencesSerializer.defaultValue)
                val loginState by rememberLoginState(mSharedPreferences)
                SettingsScreen(
                    preferences = preferences,
                    isLoggedIn = loginState.isLoggedIn,
                    username = loginState.username,
                    onShowServingTimesChanged = { value ->
                        lifecycleScope.launch { appPreferenceManager.setShowServingTimes(value) }
                    },
                    onShowFavoriteCountsChanged = { value ->
                        lifecycleScope.launch { appPreferenceManager.setShowFavoriteCounts(value) }
                    },
                    onHideClosedLocationsChanged = { value ->
                        lifecycleScope.launch { appPreferenceManager.setHideClosedDiningCourts(value) }
                    },
                    onNightModeSelected = { mode ->
                        lifecycleScope.launch { appPreferenceManager.setNightMode(mode) }
                    },
                    onDiningCourtOrderClicked = { startActivity(Intent(this, CustomOrderActivity::class.java)) },
                    onLoginClicked = { startActivity(Intent(this, LoginActivity::class.java)) },
                    onLogout = ::logout,
                    onPrivacyPolicyClicked = {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://android.menus.benferris.dev/privacy")))
                    },
                )
            }
        }
    }

    private fun logout(clearFavorites: Boolean) {
        if (clearFavorites) {
            lifecycleScope.launch {
                mFavoritesRepository.clearLocalFavorites()
            }
        }
        mSharedPreferences.edit {
            putBoolean(KEY_PREF_LOGGED_IN, false)
            putString(KEY_PREF_USERNAME, null)
            putString(KEY_PREF_PASSWORD, null)
        }
    }

    companion object {
        fun getIntent(packageContext: Context?): Intent {
            return Intent(packageContext, SettingsActivity::class.java)
        }
    }
}

data class LoginPrefState(val isLoggedIn: Boolean, val username: String?)

/**
 * Observes the logged-in state stored in SharedPreferences so the login row updates
 * when the user signs in or out.
 */
@Composable
private fun rememberLoginState(sharedPreferences: SharedPreferences): State<LoginPrefState> {
    fun read() = LoginPrefState(
        isLoggedIn = sharedPreferences.getBoolean(KEY_PREF_LOGGED_IN, false),
        username = sharedPreferences.getString(KEY_PREF_USERNAME, null),
    )

    val state = remember { mutableStateOf(read()) }
    DisposableEffect(sharedPreferences) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_PREF_LOGGED_IN || key == KEY_PREF_USERNAME) state.value = read()
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
        onDispose { sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener) }
    }
    return state
}
