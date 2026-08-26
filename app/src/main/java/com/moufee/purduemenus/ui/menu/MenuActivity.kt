package com.moufee.purduemenus.ui.menu

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.lifecycle.lifecycleScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.moufee.purduemenus.BuildConfig
import com.moufee.purduemenus.R
import com.moufee.purduemenus.ui.settings.SettingsActivity
import com.moufee.purduemenus.ui.theme.MenusTheme
import com.moufee.purduemenus.util.NetworkAvailabilityListener
import com.moufee.purduemenus.workers.DownloadWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val IN_APP_UPDATE_REQUEST_CODE = 1

@AndroidEntryPoint
class MenuActivity : AppCompatActivity() {
    private val mViewModel: MenuViewModel by viewModels()
    private val snackbarHostState = SnackbarHostState()
    private lateinit var networkListener: NetworkAvailabilityListener

    @Inject
    lateinit var appUpdateManager: AppUpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MenusTheme {
                MenuScreen(
                    viewModel = mViewModel,
                    snackbarHostState = snackbarHostState,
                    onSettingsClicked = { startActivity(SettingsActivity.getIntent(this)) },
                    onFeedbackClicked = ::sendFeedback,
                )
            }
        }
        networkListener = NetworkAvailabilityListener(this, lifecycle) {
            mViewModel.reloadData()
        }
        val workManager = WorkManager.getInstance(this)
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).setRequiresBatteryNotLow(true).build()
        val request = PeriodicWorkRequest.Builder(DownloadWorker::class.java, 1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()
        workManager.enqueueUniquePeriodicWork("downloader", ExistingPeriodicWorkPolicy.KEEP, request)
        checkForUpdate()
    }

    private fun sendFeedback() {
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse("mailto:") // only email apps should handle this
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("support@benferris.dev")) // recipients
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Purdue Menus Feedback (version ${BuildConfig.VERSION_NAME})")
        try {
            startActivity(emailIntent)
        } catch (e: ActivityNotFoundException) {

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
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
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
                Timber.d("Update failed")
            }
        }
    }

    private fun popupSnackbarForCompleteUpdate() {
        lifecycleScope.launch {
            val result = snackbarHostState.showSnackbar(
                message = getString(R.string.update_downloaded_alert),
                actionLabel = getString(R.string.update_downloaded_install),
                duration = SnackbarDuration.Indefinite,
            )
            if (result == SnackbarResult.ActionPerformed) {
                appUpdateManager.completeUpdate()
            }
        }
    }
}
