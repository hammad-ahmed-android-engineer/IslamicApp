package com.ne.muslimprayers.qiblafinder.domain.usecase.zakat

import javax.inject.Inject

/**
 * Data holder representing total wealth assets and liabilities for Zakat calculation.
 */
data class ZakatAssets(
    val cashSavings: Double = 0.0,
    val goldValue: Double = 0.0,
    val silverValue: Double = 0.0,
    val investments: Double = 0.0,
    val liabilities: Double = 0.0
)

/**
 * Data holder for Zakat calculation result.
 */
data class ZakatResult(
    val totalAssets: Double,
    val netWealth: Double,
    val zakatPayable: Double,
    val isNisabReached: Boolean
)

/**
 * Domain Use Case to perform accurate 2.5% Zakat calculations based on net wealth assets.
 */
class CalculateZakatUseCase @Inject constructor() {

    operator fun invoke(assets: ZakatAssets, nisabThreshold: Double = 500.0): ZakatResult {
        val totalAssets = assets.cashSavings + assets.goldValue + assets.silverValue + assets.investments
        val netWealth = (totalAssets - assets.liabilities).coerceAtLeast(0.0)
        val isNisabReached = netWealth >= nisabThreshold
        val zakatPayable = if (isNisabReached) netWealth * 0.025 else 0.0

        return ZakatResult(
            totalAssets = totalAssets,
            netWealth = netWealth,
            zakatPayable = zakatPayable,
            isNisabReached = isNisabReached
        )
    }
}
