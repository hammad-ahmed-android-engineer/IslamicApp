package com.ne.muslimprayers.qiblafinder.ui.more

import com.ne.muslimprayers.qiblafinder.domain.usecase.zakat.ZakatResult

/**
 * Data model for 99 Names of Allah items.
 */
data class NameOfAllah(
    val id: Int,
    val arabic: String,
    val transliteration: String,
    val translation: String,
    val audioRes: Int
)

/**
 * UI state for MoreScreen settings, options, and dialogs.
 */
data class MoreUiState(
    val notificationsEnabled: Boolean = true,
    val selectedMethodIndex: Int = 2,
    val selectedAsrJuristic: Int = 0, // 0: Standard, 1: Hanafi
    val selectedHijriOffset: Int = 0, // -2 to +2
    val isMethodManuallySelected: Boolean = false,
    val isAsrManuallySelected: Boolean = false,
    val showMethodDialog: Boolean = false,
    val showAsrDialog: Boolean = false,
    val showHijriOffsetDialog: Boolean = false,
    val showZakatDialog: Boolean = false,
    val showNamesDialog: Boolean = false,
    val cashSavings: String = "",
    val goldValue: String = "",
    val silverValue: String = "",
    val investments: String = "",
    val liabilities: String = "",
    val zakatResult: ZakatResult? = null,
    val playingNameId: Int? = null,
    val namesOfAllah: List<NameOfAllah> = emptyList()
)
