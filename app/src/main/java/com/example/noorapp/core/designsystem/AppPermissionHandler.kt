package com.example.noorapp.core.designsystem

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.noorapp.R
import com.example.noorapp.core.common.PermissionPreferencesManager
import com.example.noorapp.core.theme.DesertOchre

@Stable
class AppPermissionHandler internal constructor(
    private val onRequest: () -> Unit,
    private val onHasThreeOrMoreDenials: () -> Boolean
) {
    /**
     * Triggers the runtime permission prompt (or App Settings once denied 3+ times).
     */
    fun request() = onRequest()

    fun hasReachedSettingsThreshold(): Boolean = onHasThreeOrMoreDenials()
}

/**
 * Reusable Compose permission handler backed by SharedPreferences:
 * - 1st denial: displays an [AppDialog] explaining permissions are required and lets user retry.
 * - 2nd denial: displays the [AppDialog] warning again and lets user retry.
 * - 3rd denial (and beyond): automatically opens the Android App Settings screen.
 */
@Composable
fun rememberAppPermissionHandler(
    permissionKey: String,
    permissions: Array<String>,
    isGranted: () -> Boolean,
    dialogTitle: String,
    dialogMessage: String,
    onGranted: () -> Unit,
    onDismissWarning: () -> Unit = {},
    onReturnedFromSettingsDenied: () -> Unit = {}
): AppPermissionHandler {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val currentIsGranted by rememberUpdatedState(isGranted)
    val currentOnGranted by rememberUpdatedState(onGranted)
    val currentOnDismissWarning by rememberUpdatedState(onDismissWarning)
    val currentOnReturnedDenied by rememberUpdatedState(onReturnedFromSettingsDenied)

    var showWarningDialog by rememberSaveable { mutableStateOf(false) }
    var pendingSettingsReturn by rememberSaveable { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (currentIsGranted()) {
                    PermissionPreferencesManager.resetDenialCount(context, permissionKey)
                    showWarningDialog = false
                    if (pendingSettingsReturn) {
                        pendingSettingsReturn = false
                        currentOnGranted()
                    }
                } else if (pendingSettingsReturn) {
                    pendingSettingsReturn = false
                    currentOnReturnedDenied()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (currentIsGranted()) {
            PermissionPreferencesManager.resetDenialCount(context, permissionKey)
            showWarningDialog = false
            currentOnGranted()
        } else {
            val denialCount = PermissionPreferencesManager.incrementDenialCount(context, permissionKey)
            if (denialCount >= 3) {
                showWarningDialog = false
                pendingSettingsReturn = true
                PermissionPreferencesManager.openAppSettings(context)
            } else {
                showWarningDialog = true
            }
        }
    }

    if (showWarningDialog) {
        AppDialog(
            onDismissRequest = {
                showWarningDialog = false
                currentOnDismissWarning()
            },
            title = dialogTitle,
            titleMaxLines = 2,
            description = dialogMessage,
            headerIcon = Icons.Default.Security,
            headerIconTint = DesertOchre,
            confirmButtonText = stringResource(R.string.perm_grant_button),
            onConfirmClick = {
                showWarningDialog = false
                launcher.launch(permissions)
            },
            dismissButtonText = stringResource(R.string.perm_not_now_button),
            onDismissClick = {
                showWarningDialog = false
                currentOnDismissWarning()
            },
            dismissOnClickOutside = false
        ) {}
    }

    return remember(context, permissionKey, permissions) {
        AppPermissionHandler(
            onRequest = {
                if (permissions.isEmpty() || currentIsGranted()) {
                    PermissionPreferencesManager.resetDenialCount(context, permissionKey)
                    currentOnGranted()
                } else if (PermissionPreferencesManager.getDenialCount(context, permissionKey) >= 3) {
                    pendingSettingsReturn = true
                    PermissionPreferencesManager.openAppSettings(context)
                } else {
                    launcher.launch(permissions)
                }
            },
            onHasThreeOrMoreDenials = {
                PermissionPreferencesManager.getDenialCount(context, permissionKey) >= 3
            }
        )
    }
}

