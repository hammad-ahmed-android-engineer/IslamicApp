package com.ne.muslimprayers.qiblafinder.ui.more.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ne.muslimprayers.qiblafinder.core.theme.AppDimensions
import com.ne.muslimprayers.qiblafinder.core.theme.DesertOchre
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSage
import com.ne.muslimprayers.qiblafinder.core.theme.MedinaSageLight
import com.ne.muslimprayers.qiblafinder.core.theme.PureAlabaster
import com.ne.muslimprayers.qiblafinder.core.theme.TextPrimary
import com.ne.muslimprayers.qiblafinder.core.theme.TextSecondary
import com.ne.muslimprayers.qiblafinder.domain.usecase.zakat.ZakatResult
import com.ne.muslimprayers.qiblafinder.ui.components.AppText
import com.ne.muslimprayers.qiblafinder.ui.components.IslamicCard
import java.util.Locale

@Composable
fun ZakatCalculatorDialog(
    cashSavings: String,
    goldValue: String,
    silverValue: String,
    investments: String,
    liabilities: String,
    zakatResult: ZakatResult?,
    onInputChange: (cash: String, gold: String, silver: String, investments: String, liabilities: String) -> Unit,
    onCalculate: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(AppDimensions.cornerDialog)),
            color = PureAlabaster
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(AppDimensions.paddingMediumLarge)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppText.HeadingLarge(
                        text = "Zakat Calculator (2.5%)",
                        color = TextPrimary,
                        maxLines = 1
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(AppDimensions.paddingMediumSmall))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(AppDimensions.paddingRegular)
                ) {
                    OutlinedTextField(
                        value = cashSavings,
                        onValueChange = { onInputChange(it, goldValue, silverValue, investments, liabilities) },
                        label = { AppText.BodySmall(text = "Cash Savings & Bank Balance (\$)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = goldValue,
                        onValueChange = { onInputChange(cashSavings, it, silverValue, investments, liabilities) },
                        label = { AppText.BodySmall(text = "Gold Holdings Value (\$)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = silverValue,
                        onValueChange = { onInputChange(cashSavings, goldValue, it, investments, liabilities) },
                        label = { AppText.BodySmall(text = "Silver Holdings Value (\$)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = investments,
                        onValueChange = { onInputChange(cashSavings, goldValue, silverValue, it, liabilities) },
                        label = { AppText.BodySmall(text = "Stocks & Investments (\$)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = liabilities,
                        onValueChange = { onInputChange(cashSavings, goldValue, silverValue, investments, it) },
                        label = { AppText.BodySmall(text = "Debts & Short-term Liabilities (\$)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(AppDimensions.paddingSmall))

                    Button(
                        onClick = onCalculate,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(AppDimensions.cornerMedium),
                        colors = ButtonDefaults.buttonColors(containerColor = MedinaSage)
                    ) {
                        AppText.ButtonText(
                            text = "Calculate Zakat Due",
                            fontWeight = FontWeight.Bold,
                            color = PureAlabaster
                        )
                    }

                    if (zakatResult != null) {
                        Spacer(modifier = Modifier.height(AppDimensions.paddingMediumSmall))
                        IslamicCard(
                            backgroundColor = MedinaSage,
                            borderColor = DesertOchre.copy(alpha = 0.5f)
                        ) {
                            Column {
                                AppText.TitleMedium(
                                    text = "Calculation Summary",
                                    fontWeight = FontWeight.Bold,
                                    color = PureAlabaster
                                )
                                Spacer(modifier = Modifier.height(AppDimensions.paddingExtraSmall))
                                AppText.BodyMedium(
                                    text = "Net Wealth Subject to Zakat: $%.2f".format(Locale.US, zakatResult.netWealth),
                                    color = MedinaSageLight
                                )
                                Spacer(modifier = Modifier.height(AppDimensions.paddingTiny))
                                AppText.HeadingLarge(
                                    text = "Zakat Payable (2.5%): $%.2f".format(Locale.US, zakatResult.zakatPayable),
                                    color = DesertOchre
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
