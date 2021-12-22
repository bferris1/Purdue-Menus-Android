package com.moufee.purduemenus.ui.menu

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.work.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.moufee.purduemenus.BuildConfig
import com.moufee.purduemenus.R
import com.moufee.purduemenus.databinding.ActivityMenuDatePickerTimeBinding
import com.moufee.purduemenus.preferences.KEY_PREF_SHOW_FAVORITE_COUNT
import com.moufee.purduemenus.repository.data.menus.DayMenu
import com.moufee.purduemenus.repository.data.menus.DiningCourtMeal
import com.moufee.purduemenus.ui.settings.SettingsActivity
import com.moufee.purduemenus.util.DateTimeHelper
import com.moufee.purduemenus.util.NetworkAvailabilityListener
import com.moufee.purduemenus.util.Resource
import com.moufee.purduemenus.util.UPDATE_MESSAGE_KEY
import com.moufee.purduemenus.workers.DownloadWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val IN_APP_UPDATE_REQUEST_CODE = 1

@AndroidEntryPoint
class MenuActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMenuDatePickerTimeBinding
    private val mMenuPagerAdapter: MenuPagerAdapter = MenuPagerAdapter(this)
    private lateinit var mViewModel: MenuViewModel
    private lateinit var networkListener: NetworkAvailabilityListener


    @Inject
    lateinit var appUpdateManager: AppUpdateManager

    @Inject
    lateinit var mSharedPreferences: SharedPreferences

    private fun setListeners() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.currentDate.collect { dateTime: LocalDate ->
                    mBinding.dateTextView.text = DateTimeHelper.getFriendlyDateFormat(dateTime, Locale.getDefault(), applicationContext)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.favoriteSet.collect { strings: Set<String> ->
                    mMenuPagerAdapter.setFavoritesSet(strings)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.appPreferences.collect { prefs ->
                    mMenuPagerAdapter.setShowFavoriteCount(prefs.showFavoriteCounts)
                }
            }

        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.sortedLocations.collect { sorted: List<DiningCourtMeal> ->
                    mMenuPagerAdapter.setMenus(sorted)
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.dayMenu.collect { result: Resource<DayMenu> ->
                    if (result is Resource.Error) {
                        Snackbar.make(mBinding.activityMenuCoordinatorLayout,
                            getString(R.string.network_error_message),
                            Snackbar.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.app_name)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_menu_date_picker_time)
        mBinding.lifecycleOwner = this
        mViewModel = ViewModelProvider(this).get(MenuViewModel::class.java)
        mBinding.viewModel = mViewModel
        val tabLayout = mBinding.menuTabLayout
        val toolbar = mBinding.mainToolbar
        setSupportActionBar(toolbar)
        mMenuPagerAdapter.setShowFavoriteCount(mSharedPreferences.getBoolean(KEY_PREF_SHOW_FAVORITE_COUNT, true))
        mBinding.menuViewPager.adapter = mMenuPagerAdapter
        TabLayoutMediator(tabLayout, mBinding.menuViewPager) { tab, position ->
            tab.text = mMenuPagerAdapter.getPageTitle(position)
        }.attach()
        setListeners()
        networkListener = NetworkAvailabilityListener(this, lifecycle) {
            mViewModel.reloadData()
        }
//        displayChangelog()
        val workManager = WorkManager.getInstance(this)
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).setRequiresBatteryNotLow(true).build()
        val request = PeriodicWorkRequest.Builder(DownloadWorker::class.java, 1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()
        workManager.enqueueUniquePeriodicWork("downloader", ExistingPeriodicWorkPolicy.KEEP, request)
        checkForUpdate()
    }

    private fun displayChangelog() {
        val versionCode = BuildConfig.VERSION_CODE
        val hasShownMessage = mSharedPreferences.getBoolean(UPDATE_MESSAGE_KEY + versionCode, false)
        if (!hasShownMessage) {
            val changelogFragment: DialogFragment = ChangelogDialogFragment()
            changelogFragment.show(supportFragmentManager, "changelog")
            mSharedPreferences.edit().putBoolean(UPDATE_MESSAGE_KEY + versionCode, true).apply()
        }
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                // If the update is downloaded but not installed,
                // notify the user to complete the update.
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackbarForCompleteUpdate()
                }
            }
    }

    private fun checkForUpdate() {
        // Create a listener to track request state updates.
        val listener = { state: InstallState ->
            // (Optional) Provide a download progress bar.
            if (state.installStatus() == InstallStatus.DOWNLOADING) {
//                val bytesDownloaded = state.bytesDownloaded()
//                val totalBytesToDownload = state.totalBytesToDownload()
                // Show update progress bar.
            } else if (state.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate()
            }
        }

        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                // Request the update.
                appUpdateManager.registerListener(listener)
                appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, this, IN_APP_UPDATE_REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IN_APP_UPDATE_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                // Log something
                Timber.d("Update failed")
            }
        }
    }

    private fun popupSnackbarForCompleteUpdate() {
        Snackbar.make(
            mBinding.activityMenuCoordinatorLayout,
            getString(R.string.update_downloaded_alert),
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction(getString(R.string.update_downloaded_install)) { appUpdateManager.completeUpdate() }
            setActionTextColor(ResourcesCompat.getColor(resources, R.color.snackbarButtonColor, context.theme))
            show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(SettingsActivity.getIntent(this))
                true
            }
            R.id.action_feedback -> {
                val emailIntent = Intent(Intent.ACTION_SENDTO)
                emailIntent.data = Uri.parse("mailto:") // only email apps should handle this
                emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("support@benferris.dev")) // recipients
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Purdue Menus Feedback (version ${BuildConfig.VERSION_NAME})")
                try {
                    startActivity(emailIntent)
                } catch (e: ActivityNotFoundException) {

                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}