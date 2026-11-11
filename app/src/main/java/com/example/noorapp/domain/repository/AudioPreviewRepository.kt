package com.example.noorapp.domain.repository

import androidx.annotation.RawRes

interface AudioPreviewRepository {
    fun playRawAudio(
        @RawRes audioResId: Int,
        onCompleted: () -> Unit,
        onError: () -> Unit
    )
    fun stop()
}

