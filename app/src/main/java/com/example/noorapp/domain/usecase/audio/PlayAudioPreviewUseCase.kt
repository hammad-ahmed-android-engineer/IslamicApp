package com.example.noorapp.domain.usecase.audio

import androidx.annotation.RawRes
import com.example.noorapp.domain.repository.AudioPreviewRepository
import javax.inject.Inject

class PlayAudioPreviewUseCase @Inject constructor(
    private val audioPreviewRepository: AudioPreviewRepository
) {
    fun play(
        @RawRes audioResId: Int,
        onCompleted: () -> Unit,
        onError: () -> Unit
    ) {
        audioPreviewRepository.playRawAudio(
            audioResId = audioResId,
            onCompleted = onCompleted,
            onError = onError
        )
    }

    fun stop() {
        audioPreviewRepository.stop()
    }
}

