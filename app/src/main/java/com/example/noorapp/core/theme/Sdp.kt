package com.example.noorapp.core.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Responsive Scalable DP (sdp) for Jetpack Compose.
 * Uses the minimum screen dimension capped at 600dp baseline to ensure
 * dimensions scale proportionally without overflowing in landscape mode or large displays.
 */
val Int.sdp: Dp
    @Composable
    get() {
        val config = LocalConfiguration.current
        val baseWidth = minOf(config.screenWidthDp, config.screenHeightDp, 600)
        return ((this.toFloat() * baseWidth) / 360f).dp
    }

val Float.sdp: Dp
    @Composable
    get() {
        val config = LocalConfiguration.current
        val baseWidth = minOf(config.screenWidthDp, config.screenHeightDp, 600)
        return ((this * baseWidth) / 360f).dp
    }

val Double.sdp: Dp
    @Composable
    get() {
        val config = LocalConfiguration.current
        val baseWidth = minOf(config.screenWidthDp, config.screenHeightDp, 600)
        return ((this.toFloat() * baseWidth) / 360f).dp
    }

/**
 * Responsive Scalable SP (ssp) for Jetpack Compose.
 */
val Int.ssp: TextUnit
    @Composable
    get() {
        val config = LocalConfiguration.current
        val baseWidth = minOf(config.screenWidthDp, config.screenHeightDp, 600)
        return ((this.toFloat() * baseWidth) / 360f).sp
    }

val Float.ssp: TextUnit
    @Composable
    get() {
        val config = LocalConfiguration.current
        val baseWidth = minOf(config.screenWidthDp, config.screenHeightDp, 600)
        return ((this * baseWidth) / 360f).sp
    }

val Double.ssp: TextUnit
    @Composable
    get() {
        val config = LocalConfiguration.current
        val baseWidth = minOf(config.screenWidthDp, config.screenHeightDp, 600)
        return ((this.toFloat() * baseWidth) / 360f).sp
    }
