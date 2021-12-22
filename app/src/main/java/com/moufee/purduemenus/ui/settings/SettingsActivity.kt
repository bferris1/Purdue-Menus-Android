package com.moufee.purduemenus.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.moufee.purduemenus.AppPreferences
import com.moufee.purduemenus.preferences.AppPreferenceManager
import com.moufee.purduemenus.util.SingleFragmentActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Settings Activity
 */
@AndroidEntryPoint
class SettingsActivity : SingleFragmentActivity() {
    @Inject
    lateinit var appPreferenceManager: AppPreferenceManager


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
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun createFragment(): Fragment {
        return SettingsFragment()
    }

    companion object {
        fun getIntent(packageContext: Context?): Intent {
            return Intent(packageContext, SettingsActivity::class.java)
        }
    }
}