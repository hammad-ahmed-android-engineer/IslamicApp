package com.example.noorapp.ui.ibadat

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppTopAppBar
import com.example.noorapp.core.theme.WarmParchment
import com.example.noorapp.core.utils.AppUtils
import com.example.noorapp.ui.ibadat.components.DuasTabContent

@Composable
fun IbadatScreen(
    viewModel: IbadatViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshTypographyPreferences()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = stringResource(R.string.nav_duas)
            )
        },
        containerColor = WarmParchment,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            DuasTabContent(
                duas = state.filteredDuas,
                categories = state.categories,
                categoryCounts = state.categoryCounts,
                selectedCategory = state.selectedCategory,
                isCategoryOpened = state.isCategoryOpened,
                searchQuery = state.searchQuery,
                isLoading = state.isLoading,
                errorMessage = state.errorMessage,
                isLandscape = isLandscape,
                selectedQuranFont = state.selectedQuranFont,
                arabicFontSize = state.arabicFontSize,
                translationFontSize = state.translationFontSize,
                onRetry = viewModel::loadDuas,
                onSearchChange = viewModel::onSearchQueryChanged,
                onSelectCategory = viewModel::selectCategory,
                onCloseCategory = viewModel::closeCategory,
                onCopy = { dua ->
                    val text = context.getString(
                        R.string.dua_copy_format,
                        dua.arabicText,
                        dua.transliteration,
                        dua.translation,
                        dua.reference
                    )
                    AppUtils.copyToClipboard(
                        context,
                        context.getString(R.string.home_dua_clipboard_label),
                        text,
                        context.getString(R.string.dua_copied)
                    )
                },
                onShare = { dua ->
                    val text = context.getString(
                        R.string.dua_share_format,
                        dua.title,
                        dua.arabicText,
                        dua.transliteration,
                        dua.translation,
                        context.getString(R.string.duas_reference_label, dua.reference)
                    )
                    AppUtils.shareText(
                        context,
                        text,
                        context.getString(R.string.share_dua_via)
                    )
                }
            )
        }
    }
}
