package com.example.noorapp.ui.more.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.noorapp.R
import com.example.noorapp.core.designsystem.AppButton
import com.example.noorapp.core.designsystem.AppCard
import com.example.noorapp.core.designsystem.AppText
import com.example.noorapp.core.designsystem.AppTextField
import com.example.noorapp.core.theme.AppDimensions
import com.example.noorapp.core.theme.DesertOchre
import com.example.noorapp.core.theme.MedinaSage
import com.example.noorapp.core.theme.MedinaSageLight
import com.example.noorapp.core.theme.PureAlabaster
import com.example.noorapp.domain.usecase.zakat.ZakatResult
import java.util.Locale

/**
 * Pure content body for the Zakat calculator inside common [com.example.noorapp.core.designsystem.AppDialog].
 */
@Composable
fun ZakatCalculatorContent(
    cashSavings: String,
    goldValue: String,
    silverValue: String,
    investments: String,
    liabilities: String,
    zakatResult: ZakatResult?,
    isLandscape: Boolean = false,
    onInputChange: (cash: String, gold: String, silver: String, investments: String, liabilities: String) -> Unit,
    onCalculate: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingRegular)
    ) {
        if (isLandscape) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingRegular)
            ) {
                AppTextField(
                    value = cashSavings,
                    onValueChange = { onInputChange(it, goldValue, silverValue, investments, liabilities) },
                    label = stringResource(R.string.zakat_cash_input_label),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                AppTextField(
                    value = goldValue,
                    onValueChange = { onInputChange(cashSavings, it, silverValue, investments, liabilities) },
                    label = stringResource(R.string.zakat_gold_input_label),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppDimensions.paddingRegular)
            ) {
                AppTextField(
                    value = silverValue,
                    onValueChange = { onInputChange(cashSavings, goldValue, it, investments, liabilities) },
                    label = stringResource(R.string.zakat_silver_input_label),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                AppTextField(
                    value = investments,
                    onValueChange = { onInputChange(cashSavings, goldValue, silverValue, it, liabilities) },
                    label = stringResource(R.string.zakat_investments_input_label),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
            AppTextField(
                value = liabilities,
                onValueChange = { onInputChange(cashSavings, goldValue, silverValue, investments, it) },
                label = stringResource(R.string.zakat_liabilities_input_label),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        } else {
            AppTextField(
                value = cashSavings,
                onValueChange = { onInputChange(it, goldValue, silverValue, investments, liabilities) },
                label = stringResource(R.string.zakat_cash_input_label),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            AppTextField(
                value = goldValue,
                onValueChange = { onInputChange(cashSavings, it, silverValue, investments, liabilities) },
                label = stringResource(R.string.zakat_gold_input_label),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            AppTextField(
                value = silverValue,
                onValueChange = { onInputChange(cashSavings, goldValue, it, investments, liabilities) },
                label = stringResource(R.string.zakat_silver_input_label),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            AppTextField(
                value = investments,
                onValueChange = { onInputChange(cashSavings, goldValue, silverValue, it, liabilities) },
                label = stringResource(R.string.zakat_investments_input_label),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            AppTextField(
                value = liabilities,
                onValueChange = { onInputChange(cashSavings, goldValue, silverValue, investments, it) },
                label = stringResource(R.string.zakat_liabilities_input_label),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(AppDimensions.paddingTiny))

        AppButton(
            onClick = onCalculate,
            modifier = Modifier.fillMaxWidth()
        ) {
            AppText.ButtonText(
                text = stringResource(R.string.zakat_calculate_btn),
                fontWeight = FontWeight.Bold,
                color = PureAlabaster
            )
        }

        if (zakatResult != null) {
            Spacer(modifier = Modifier.height(AppDimensions.paddingTiny))
            AppCard(
                backgroundColor = MedinaSage,
                borderColor = DesertOchre.copy(alpha = 0.5f)
            ) {
                Column {
                    AppText.TitleMedium(
                        text = stringResource(R.string.zakat_summary_title),
                        fontWeight = FontWeight.Bold,
                        color = PureAlabaster
                    )
                    Spacer(modifier = Modifier.height(AppDimensions.paddingExtraSmall))
                    AppText.BodyMedium(
                        text = String.format(
                            Locale.US,
                            stringResource(R.string.zakat_net_wealth_format),
                            zakatResult.netWealth
                        ),
                        color = MedinaSageLight
                    )
                    Spacer(modifier = Modifier.height(AppDimensions.paddingTiny))
                    AppText.HeadingLarge(
                        text = String.format(
                            Locale.US,
                            stringResource(R.string.zakat_payable_format),
                            zakatResult.zakatPayable
                        ),
                        color = DesertOchre
                    )
                }
            }
        }
    }
}

