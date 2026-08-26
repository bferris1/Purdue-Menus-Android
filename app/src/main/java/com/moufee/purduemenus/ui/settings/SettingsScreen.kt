package com.moufee.purduemenus.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.moufee.purduemenus.AppPreferences
import com.moufee.purduemenus.R

/**
 * Displays preferences for the app. Replaces the former PreferenceFragmentCompat-based settings.
 */
@Composable
fun SettingsScreen(
    preferences: AppPreferences,
    isLoggedIn: Boolean,
    username: String?,
    onShowServingTimesChanged: (Boolean) -> Unit,
    onShowFavoriteCountsChanged: (Boolean) -> Unit,
    onHideClosedLocationsChanged: (Boolean) -> Unit,
    onNightModeSelected: (AppPreferences.NightMode) -> Unit,
    onDiningCourtOrderClicked: () -> Unit,
    onLoginClicked: () -> Unit,
    onLogout: (clearFavorites: Boolean) -> Unit,
    onPrivacyPolicyClicked: () -> Unit,
) {
    var showNightModeDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.title_settings)) }) }) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            SwitchPreferenceRow(
                title = stringResource(R.string.pref_show_serving_times),
                checked = preferences.showServingTimes,
                onCheckedChange = onShowServingTimesChanged,
            )
            SwitchPreferenceRow(
                title = stringResource(R.string.pref_title_show_favorite_count),
                checked = preferences.showFavoriteCounts,
                onCheckedChange = onShowFavoriteCountsChanged,
            )
            SwitchPreferenceRow(
                title = stringResource(R.string.pref_title_hide_closed_locations),
                checked = preferences.hideClosedDiningCourts,
                onCheckedChange = onHideClosedLocationsChanged,
            )
            PreferenceRow(
                title = stringResource(R.string.title_pref_dining_court_order),
                summary = stringResource(R.string.pref_summary_dining_court_order),
                onClick = onDiningCourtOrderClicked,
            )
            PreferenceRow(
                title = stringResource(R.string.pref_title_dark_mode),
                summary = nightModeLabel(preferences.nightMode),
                onClick = { showNightModeDialog = true },
            )
            if (isLoggedIn) {
                PreferenceRow(
                    title = stringResource(R.string.action_sign_out),
                    summary = stringResource(R.string.description_signed_in, username ?: "user"),
                    onClick = { showLogoutDialog = true },
                )
            } else {
                PreferenceRow(
                    title = stringResource(R.string.action_login),
                    summary = stringResource(R.string.pref_summary_not_logged_in),
                    onClick = onLoginClicked,
                )
            }
            PreferenceRow(
                title = stringResource(R.string.title_privacy_policy),
                onClick = onPrivacyPolicyClicked,
            )
        }
    }

    if (showNightModeDialog) {
        NightModeDialog(
            currentMode = preferences.nightMode,
            onModeSelected = {
                showNightModeDialog = false
                onNightModeSelected(it)
            },
            onDismiss = { showNightModeDialog = false },
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(R.string.title_prompt_clear_favorites)) },
            text = { Text(stringResource(R.string.prompt_clear_local_favorites)) },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout(true)
                }) {
                    Text(stringResource(R.string.action_clear_favorites), color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout(false)
                }) {
                    Text(stringResource(R.string.action_only_logout))
                }
            },
        )
    }
}

@Composable
private fun nightModeLabel(mode: AppPreferences.NightMode): String = stringResource(
    when (mode) {
        AppPreferences.NightMode.OFF -> R.string.off
        AppPreferences.NightMode.ON -> R.string.on
        else -> R.string.auto
    }
)

@Composable
private fun NightModeDialog(
    currentMode: AppPreferences.NightMode,
    onModeSelected: (AppPreferences.NightMode) -> Unit,
    onDismiss: () -> Unit,
) {
    val options = listOf(
        AppPreferences.NightMode.OFF to stringResource(R.string.off),
        AppPreferences.NightMode.FOLLOW_SYSTEM to stringResource(R.string.auto),
        AppPreferences.NightMode.ON to stringResource(R.string.on),
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.pref_title_dark_mode)) },
        text = {
            Column {
                options.forEach { (mode, label) ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(selected = currentMode == mode, onClick = { onModeSelected(mode) })
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(selected = currentMode == mode, onClick = null)
                        Text(label, Modifier.padding(start = 16.dp))
                    }
                }
            }
        },
        buttons = {},
    )
}

@Composable
private fun SwitchPreferenceRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .defaultMinSize(minHeight = 56.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, Modifier.weight(1f), style = MaterialTheme.typography.subtitle1)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun PreferenceRow(title: String, summary: String? = null, onClick: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .defaultMinSize(minHeight = 56.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(title, style = MaterialTheme.typography.subtitle1)
        if (summary != null) {
            Text(
                text = summary,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}
