package com.example.noorapp.ui.tasbih

import com.example.noorapp.domain.model.TasbihState

data class TasbihUiState(
    val tasbih: TasbihState = TasbihState(),
    val selectedQuranFont: String = "muhammadi"
)

