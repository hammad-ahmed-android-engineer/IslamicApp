package com.example.noorapp.core.utils

import android.content.Context
import android.content.Intent
import com.example.noorapp.R

/**
 * Utility functions for sharing content via Android Intents.
 */
object ShareUtils {

    /**
     * Shares plain text content via standard Android share chooser dialog.
     */
    fun shareText(
        context: Context,
        text: String,
        chooserTitle: String = context.getString(R.string.share_via)
    ) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            context.startActivity(Intent.createChooser(intent, chooserTitle))
        } catch (e: Exception) {
            // ignore
        }
    }
}
