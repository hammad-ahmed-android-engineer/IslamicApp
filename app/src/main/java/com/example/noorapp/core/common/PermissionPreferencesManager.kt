package com.example.noorapp.core.common

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.example.noorapp.core.utils.LocationUtils

/**
 * Tracks runtime permission denial counts in SharedPreferences so the app can prompt
 * on the 1st and 2nd denials and direct the user to App Settings on the 3rd denial.
 */
object PermissionPreferencesManager {

    private const val PREFS_NAME = "app_permission_prefs"
    private const val KEY_PREFIX = "denial_count_"

    const val KEY_STARTUP = "startup"
    const val KEY_LOCATION = "location"
    const val KEY_NOTIFICATION = "notification"

    fun getDenialCount(context: Context, key: String): Int {
        return context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getInt("$KEY_PREFIX$key", 0)
    }

    fun incrementDenialCount(context: Context, key: String): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val updated = prefs.getInt("$KEY_PREFIX$key", 0) + 1
        prefs.edit().putInt("$KEY_PREFIX$key", updated).apply()
        return updated
    }

    fun resetDenialCount(context: Context, key: String) {
        context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt("$KEY_PREFIX$key", 0)
            .apply()
    }

    fun hasLocationPermission(context: Context): Boolean {
        return LocationUtils.hasLocationPermission(context)
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun hasAllStartupPermissions(context: Context): Boolean {
        return hasLocationPermission(context) && hasNotificationPermission(context)
    }

    fun locationPermissions(): Array<String> = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    fun notificationPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            emptyArray()
        }
    }

    fun startupPermissions(): Array<String> {
        val list = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            list.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        return list.toTypedArray()
    }

    fun openAppSettings(context: Context) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}

