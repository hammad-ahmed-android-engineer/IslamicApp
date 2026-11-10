package com.ne.muslimprayers.qiblafinder.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.ui.graphics.vector.ImageVector
import com.ne.muslimprayers.qiblafinder.R

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Quran : Screen("quran")
    object Hadith : Screen("hadith")
    object Ibadat : Screen("ibadat")
    object More : Screen("more")
    object Compass : Screen("compass")
    object Calendar : Screen("calendar")
    object PrayersTiming : Screen("prayers_timing")
    object PrayerAlarm : Screen("prayer_alarm")
    object SalahTracker : Screen("salah_tracker")
}

data class BottomNavItem(
    @StringRes val titleRes: Int,
    val route: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(R.string.nav_home, Screen.Home.route, Icons.Default.Home),
    BottomNavItem(R.string.nav_quran, Screen.Quran.route, Icons.Default.MenuBook),
    BottomNavItem(R.string.nav_hadith, Screen.Hadith.route, Icons.Default.AutoStories),
    BottomNavItem(R.string.nav_ibadat, Screen.Ibadat.route, Icons.Default.SelfImprovement),
    BottomNavItem(R.string.nav_more, Screen.More.route, Icons.Default.MoreHoriz)
)
