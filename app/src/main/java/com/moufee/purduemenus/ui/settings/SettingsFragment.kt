package com.moufee.purduemenus.ui.settings

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.moufee.purduemenus.R
import com.moufee.purduemenus.preferences.*
import com.moufee.purduemenus.repository.FavoritesRepository
import com.moufee.purduemenus.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Displays preferences for the app
 */
@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat(), OnSharedPreferenceChangeListener {
    @Inject
    lateinit var mSharedPreferences: SharedPreferences

    @Inject
    lateinit var mFavoritesRepository: FavoritesRepository

    @Inject
    lateinit var appPreferenceManager: AppPreferenceManager

    private val dataStore: AppPreferenceDataStore by lazy { AppPreferenceDataStore(lifecycleScope, appPreferenceManager) }

    private var mLoginPref: Preference? = null
    private var mPrivacyPolicyPref: Preference? = null


    override fun onDestroy() {
        super.onDestroy()
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore = dataStore
        addPreferencesFromResource(R.xml.pref_general)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLoginPref = findPreference(PREF_LOG_IN)
        val sortOrderPref = findPreference<Preference>(KEY_PREF_DINING_COURT_ORDER)
        mPrivacyPolicyPref = findPreference(KEY_PREF_PRIVACY_POLICY)
        sortOrderPref!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            startActivity(Intent(activity, CustomOrderActivity::class.java))
            true
        }
        mSharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext())
        updateLoginPreference()
        mLoginPref?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val isLoggedIn = mSharedPreferences.getBoolean(KEY_PREF_LOGGED_IN, false)
            if (isLoggedIn) {
                showLogoutPrompt()
                true
            } else {
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
                true
            }
        }

        mPrivacyPolicyPref?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://android.menus.purdue.tools/privacy"))
            startActivity(browserIntent)
            true
        }
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    private fun showLogoutPrompt() {
        val logoutDialog = AlertDialog.Builder(requireActivity())
            .setTitle(R.string.title_prompt_clear_favorites)
            .setMessage(R.string.prompt_clear_local_favorites)
            .setPositiveButton(R.string.action_clear_favorites) { dialog: DialogInterface?, which: Int ->
                lifecycleScope.launch {
                    mFavoritesRepository.clearLocalFavorites()
                }
                logout()
            }
            .setCancelable(true)
            .setNegativeButton(R.string.action_only_logout) { dialog: DialogInterface?, which: Int -> logout() }
            .create()
        logoutDialog.setOnShowListener { dialog: DialogInterface? ->
            logoutDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.RED)
        }
        logoutDialog.show()
    }

    private fun logout() {
        mSharedPreferences.edit {
            putBoolean(KEY_PREF_LOGGED_IN, false)
            putString(KEY_PREF_USERNAME, null)
            putString(KEY_PREF_PASSWORD, null)
        }
    }

    private fun updateLoginPreference() {
        val isLoggedIn = mSharedPreferences.getBoolean(KEY_PREF_LOGGED_IN, false)
        if (isLoggedIn) {
            mLoginPref?.setTitle(R.string.action_sign_out)
            mLoginPref?.summary = getString(R.string.description_signed_in, mSharedPreferences.getString(KEY_PREF_USERNAME, "user"))
        } else {
            mLoginPref?.setTitle(R.string.action_login)
            mLoginPref?.setSummary(R.string.pref_summary_not_logged_in)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        if (key == KEY_PREF_LOGGED_IN) updateLoginPreference()
    }
}