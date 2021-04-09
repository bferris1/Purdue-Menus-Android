package com.moufee.purduemenus.ui.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.moufee.purduemenus.preferences.KEY_PREF_USE_NIGHT_MODE
import com.moufee.purduemenus.repository.FavoritesRepository
import com.moufee.purduemenus.util.SingleFragmentActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Settings Activity
 */
@AndroidEntryPoint
class SettingsActivity : SingleFragmentActivity(), OnSharedPreferenceChangeListener {
    @Inject
    lateinit var sharedPreferences: SharedPreferences

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
    }

    override fun onPause() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    override fun onResume() {
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        super.onResume()
    }

    override fun createFragment(): Fragment {
        return SettingsFragment()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == KEY_PREF_USE_NIGHT_MODE) {
            when (sharedPreferences.getString(key, "")) {
                "mode_off" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                "mode_on" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                "mode_auto" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    companion object {
        fun getIntent(packageContext: Context?): Intent {
            return Intent(packageContext, SettingsActivity::class.java)
        }
    }
}