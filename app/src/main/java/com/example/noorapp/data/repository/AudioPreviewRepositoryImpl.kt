package com.example.noorapp.data.repository

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.RawRes
import com.example.noorapp.domain.repository.AudioPreviewRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AudioPreviewRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AudioPreviewRepository {

    private var mediaPlayer: MediaPlayer? = null

    override fun playRawAudio(
        @RawRes audioResId: Int,
        onCompleted: () -> Unit,
        onError: () -> Unit
    ) {
        try {
            stop()
            val player = MediaPlayer.create(context, audioResId)
            if (player == null) {
                onError()
                return
            }
            mediaPlayer = player.apply {
                setOnCompletionListener {
                    onCompleted()
                }
                start()
            }
        } catch (_: Exception) {
            stop()
            onError()
        }
    }

    override fun stop() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) it.stop()
                it.release()
            }
        } catch (_: Exception) {
            // ignore
        } finally {
            mediaPlayer = null
        }
    }
}

