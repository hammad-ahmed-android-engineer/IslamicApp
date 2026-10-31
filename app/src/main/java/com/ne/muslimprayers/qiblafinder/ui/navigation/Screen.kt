package com.ne.muslimprayers.qiblafinder.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.ui.graphics.vector.ImageVector

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
    val title: String,
    val route: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem("Home", Screen.Home.route, Icons.Default.Home),
    BottomNavItem("Quran", Screen.Quran.route, Icons.Default.MenuBook),
    BottomNavItem("Hadith", Screen.Hadith.route, Icons.Default.AutoStories),
    BottomNavItem("Ibadat", Screen.Ibadat.route, Icons.Default.SelfImprovement),
    BottomNavItem("More", Screen.More.route, Icons.Default.MoreHoriz)
)
