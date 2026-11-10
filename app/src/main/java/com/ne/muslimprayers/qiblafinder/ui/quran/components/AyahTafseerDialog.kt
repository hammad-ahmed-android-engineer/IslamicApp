package com.ne.muslimprayers.qiblafinder.ui.quran.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.DividerColor
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.core.theme.WarmParchment
import com.ne.muslimprayers.qiblafinder.data.remote.api.QuranApiService
import com.ne.muslimprayers.qiblafinder.domain.model.Ayah
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class TafseerOption(
    val id: Int,
    val name: String,
    val author: String,
    val language: String, // "ur", "en", "ar", "bn"
    val languageLabel: String
)

val AVAILABLE_TAFSEERS = listOf(
    TafseerOption(159, "Bayan ul Quran", "Dr. Israr Ahmad", "ur", "اردو"),
    TafseerOption(160, "Tafsir Ibn Kathir (Urdu)", "Hafiz Ibn Kathir", "ur", "اردو"),
    TafseerOption(169, "Ibn Kathir (English)", "Hafiz Ibn Kathir", "en", "English"),
    TafseerOption(16, "Tafsir Muyassar (Arabic)", "King Fahd Complex", "ar", "العربية"),
    TafseerOption(165, "Tafsir Ahsanul Bayaan (Bengali)", "Salahuddin Yusuf", "bn", "বাংলা")
)

@Composable
fun AyahTafseerDialog(
    surahNumber: Int,
    surahName: String,
    ayah: Ayah,
    quranApiService: QuranApiService,
    onDismiss: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var selectedTafseerId by remember { mutableIntStateOf(159) } // Default: Bayan ul Quran
    var isLoading by remember { mutableStateOf(true) }
    var tafseerContent by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val selectedOption = AVAILABLE_TAFSEERS.firstOrNull { it.id == selectedTafseerId } ?: AVAILABLE_TAFSEERS.first()

    fun loadTafseer(tafsirId: Int) {
        isLoading = true
        errorMessage = null
        scope.launch {
            try {
                val verseKey = "$surahNumber:${ayah.numberInSurah}"
                val response = withContext(Dispatchers.IO) {
                    quranApiService.getAyahTafseer(tafsirId, verseKey)
                }
                val rawHtml = response.tafsir?.text ?: ""
                val cleanText = rawHtml
                    .replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n")
                    .replace(Regex("</p>", RegexOption.IGNORE_CASE), "\n\n")
                    .replace(Regex("<.*?>"), "")
                    .replace("&nbsp;", " ")
                    .replace("&quot;", "\"")
                    .replace("&amp;", "&")
                    .trim()

                tafseerContent = if (cleanText.isNotBlank()) cleanText else "No explanation available for this Ayah."
                isLoading = false
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Could not load Tafseer"
                isLoading = false
            }
        }
    }

    LaunchedEffect(selectedTafseerId) {
        loadTafseer(selectedTafseerId)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(if (isLandscape) 0.82f else 0.94f)
                .clip(RoundedCornerShape(AppDimensions.cornerDialog))
                .border(
                    AppDimensions.borderWidthThin,
                    DividerColor,
                    RoundedCornerShape(AppDimensions.cornerDialog)
                ),
            color = WarmParchment,
            shape = RoundedCornerShape(AppDimensions.cornerDialog),
            tonalElevation = AppDimensions.elevationHigh
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(if (isLandscape) AppDimensions.paddingMediumSmall else AppDimensions.paddingMedium)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingRegular)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(AppDimensions.buttonSmallHeight)
                                .clip(CircleShape)
                                .background(MedinaSageLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                tint = MedinaSage,
                                modifier = Modifier.size(AppDimensions.iconMedium)
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingExtraSmall)
                            ) {
                                AppText.TitleMedium(
                                    text = "Ayah Tafseer",
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Surface(
                                    color = MedinaSageLight,
                                    shape = RoundedCornerShape(AppDimensions.cornerExtraSmall)
                                ) {
                                    AppText.Caption(
                                        text = "‹ $surahNumber.${ayah.numberInSurah} ›",
                                        fontWeight = FontWeight.Bold,
                                        color = MedinaSage,
                                        modifier = Modifier.padding(
                                            horizontal = AppDimensions.paddingExtraSmall,
                                            vertical = AppDimensions.paddingMicro
                                        )
                                    )
                                }
                            }
                            AppText.Caption(
                                text = "Surah $surahName",
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(AppDimensions.iconButtonCompact)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary,
                            modifier = Modifier.size(AppDimensions.iconMedium)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(AppDimensions.paddingRegular))

                AppText.Caption(
                    text = "Commentary & Scholar:",
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(AppDimensions.paddingExtraSmall))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingSmall)
                ) {
                    AVAILABLE_TAFSEERS.forEach { option ->
                        val isSelected = option.id == selectedTafseerId
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedTafseerId = option.id },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MedinaSage,
                                selectedLabelColor = PureAlabaster,
                                containerColor = PureAlabaster,
                                labelColor = TextPrimary
                            ),
                            label = {
                                AppText.Caption(
                                    text = "${option.name} (${option.languageLabel})",
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    maxLines = 1
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

                IslamicCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(
                            min = AppDimensions.dialogMinHeight,
                            max = if (isLandscape) AppDimensions.dialogContentMaxHeightCompact else AppDimensions.dialogContentMaxHeightExpanded
                        ),
                    backgroundColor = PureAlabaster,
                    borderColor = DividerColor,
                    borderWidth = AppDimensions.borderWidthHairline,
                    cornerRadius = AppDimensions.cornerLarge,
                    contentPadding = AppDimensions.paddingZero
                ) {
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(AppDimensions.paddingLarge),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(
                                    color = MedinaSage,
                                    modifier = Modifier.size(AppDimensions.paddingExtraLarge)
                                )
                                Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))
                                AppText.Caption(
                                    text = "Loading ${selectedOption.name}...",
                                    color = TextSecondary
                                )
                            }
                        }
                    } else if (errorMessage != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(AppDimensions.paddingMedium),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                AppText.BodyMedium(
                                    text = "Failed to load explanation",
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(AppDimensions.paddingExtraSmall))
                                Button(
                                    onClick = { loadTafseer(selectedTafseerId) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MedinaSage)
                                ) {
                                    AppText.ButtonText(text = "Retry", color = PureAlabaster)
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(AppDimensions.paddingMediumSmall),
                            contentPadding = PaddingValues(bottom = AppDimensions.paddingMediumSmall)
                        ) {
                            item {
                                AppText.Caption(
                                    text = "${selectedOption.name} • ${selectedOption.author}",
                                    color = DesertOchre,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))
                                when (selectedOption.language) {
                                    "ur" -> AppText.UrduText(
                                        text = tafseerContent,
                                        fontSize = AppDimensions.textArabicPreview,
                                        color = TextPrimary,
                                        modifier = Modifier.padding(vertical = AppDimensions.paddingExtraSmall)
                                    )
                                    "ar" -> AppText.ArabicText(
                                        text = tafseerContent,
                                        fontSize = AppDimensions.textArabicPreview,
                                        color = TextPrimary,
                                        applySafePadding = false
                                    )
                                    else -> AppText.BodyMedium(
                                        text = tafseerContent,
                                        fontSizeOverride = AppDimensions.textBodyMedium,
                                        color = TextPrimary,
                                        textAlign = TextAlign.Start
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppDimensions.paddingRegular))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MedinaSage),
                    shape = RoundedCornerShape(AppDimensions.cornerMediumLarge)
                ) {
                    AppText.ButtonText(
                        text = "Close Tafseer",
                        fontWeight = FontWeight.Bold,
                        color = PureAlabaster
                    )
                }
            }
        }
    }
}
