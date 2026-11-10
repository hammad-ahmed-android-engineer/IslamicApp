package com.ne.muslimprayers.qiblafinder.ui.navigation

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ne.muslimprayers.qiblafinder.R
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.WarmParchment
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.alarm.PrayerAlarmScreen
import com.ne.muslimprayers.qiblafinder.ui.calendar.CalendarScreen
import com.ne.muslimprayers.qiblafinder.ui.compass.CompassScreen
import com.ne.muslimprayers.qiblafinder.ui.hadith.HadithScreen
import com.ne.muslimprayers.qiblafinder.ui.home.HomeScreen
import com.ne.muslimprayers.qiblafinder.ui.ibadat.IbadatScreen
import com.ne.muslimprayers.qiblafinder.ui.more.MoreScreen
import com.ne.muslimprayers.qiblafinder.ui.prayers.PrayersTimingScreen
import com.ne.muslimprayers.qiblafinder.ui.quran.QuranScreen
import com.ne.muslimprayers.qiblafinder.ui.salahtracker.SalahTrackerScreen
import com.ne.muslimprayers.qiblafinder.ui.splash.SplashScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val isMainTab = currentRoute in listOf(
        Screen.Home.route,
        Screen.Quran.route,
        Screen.Hadith.route,
        Screen.Ibadat.route,
        Screen.More.route
    )

    var isQuranFullScreen by remember { mutableStateOf(false) }
    val showNavBars = isMainTab && !(currentRoute == Screen.Quran.route && isQuranFullScreen)

    if (isLandscape && isMainTab) {
        // Landscape Navigation Rail
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(WarmParchment)
        ) {
            if (showNavBars) {
                NavigationRail(
                    modifier = Modifier
                        .fillMaxHeight()
                        .border(
                            width = AppDimensions.borderWidthThin,
                            color = DividerColor
                        ),
                    containerColor = PureAlabaster,
                    header = {
                        Image(
                            painter = painterResource(id = R.mipmap.icon),
                            contentDescription = "App Icon",
                            modifier = Modifier
                                .padding(vertical = AppDimensions.paddingMediumSmall)
                                .size(AppDimensions.iconExtraLarge)
                                .clip(CircleShape)
                                .border(AppDimensions.borderWidthThin, DesertOchre, CircleShape)
                        )
                    }
                ) {
                    Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationRailItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = stringResource(item.titleRes)
                                )
                            },
                            label = {
                                AppText.Caption(
                                    text = stringResource(item.titleRes),
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    maxLines = 1
                                )
                            },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = MedinaSage,
                                selectedTextColor = MedinaSage,
                                indicatorColor = MedinaSageLight,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary
                            )
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                AppNavHost(
                    navController = navController,
                    onQuranFullScreenChanged = { isQuranFullScreen = it }
                )
            }
        }
    } else {
        // Portrait Fixed Bottom Navigation Bar
        Scaffold(
            bottomBar = {
                AnimatedVisibility(
                    visible = showNavBars,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    NavigationBar(
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(
                                    topStart = AppDimensions.cornerExtraLarge,
                                    topEnd = AppDimensions.cornerExtraLarge
                                )
                            )
                            .border(
                                width = AppDimensions.borderWidthThin,
                                color = DividerColor,
                                shape = RoundedCornerShape(
                                    topStart = AppDimensions.cornerExtraLarge,
                                    topEnd = AppDimensions.cornerExtraLarge
                                )
                            ),
                        containerColor = PureAlabaster,
                        tonalElevation = AppDimensions.elevationMedium,
                        windowInsets = WindowInsets(0, 0, 0, 0)
                    ) {
                        bottomNavItems.forEach { item ->
                            val selected = currentRoute == item.route
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = stringResource(item.titleRes)
                                    )
                                },
                                label = {
                                    AppText.Caption(
                                        text = stringResource(item.titleRes),
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        maxLines = 1
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MedinaSage,
                                    selectedTextColor = MedinaSage,
                                    indicatorColor = MedinaSageLight,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary
                                )
                            )
                        }
                    }
                }
            },
            containerColor = WarmParchment,
            contentWindowInsets = WindowInsets.navigationBars
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = if (showNavBars) innerPadding.calculateBottomPadding() else AppDimensions.paddingZero)
            ) {
                AppNavHost(
                    navController = navController,
                    onQuranFullScreenChanged = { isQuranFullScreen = it }
                )
            }
        }
    }
}

@Composable
private fun AppNavHost(
    navController: NavHostController,
    onQuranFullScreenChanged: (Boolean) -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Main Tabs
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToCompass = { navController.navigate(Screen.Compass.route) },
                onNavigateToCalendar = { navController.navigate(Screen.Calendar.route) },
                onNavigateToPrayersTiming = { navController.navigate(Screen.PrayersTiming.route) },
                onNavigateToPrayerAlarm = { navController.navigate(Screen.PrayerAlarm.route) },
                onNavigateToSalahTracker = { navController.navigate(Screen.SalahTracker.route) }
            )
        }

        composable(Screen.Quran.route) {
            QuranScreen(onFullScreenChanged = onQuranFullScreenChanged)
        }

        composable(Screen.Hadith.route) {
            HadithScreen()
        }

        composable(Screen.Ibadat.route) {
            IbadatScreen()
        }

        composable(Screen.More.route) {
            MoreScreen(
                onNavigateToSalahTracker = { navController.navigate(Screen.SalahTracker.route) },
                onNavigateToPrayerAlarm = { navController.navigate(Screen.PrayerAlarm.route) }
            )
        }

        // Sub Screens
        composable(Screen.Compass.route) {
            CompassScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.Calendar.route) {
            CalendarScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.PrayersTiming.route) {
            PrayersTimingScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.PrayerAlarm.route) {
            PrayerAlarmScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.SalahTracker.route) {
            SalahTrackerScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
