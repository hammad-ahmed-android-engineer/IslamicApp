package com.example.noorapp.core.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import com.example.noorapp.R

/**
 * Helper utilities for copying content to system clipboard.
 */
object ClipboardUtils {

    /**
     * Copies plain text to device clipboard and displays a feedback toast.
     */
    fun copyToClipboard(
        context: Context,
        label: String,
        text: String,
        toastMessage: String = context.getString(R.string.copied_to_clipboard)
    ) {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            val clip = ClipData.newPlainText(label, text)
            clipboard?.setPrimaryClip(clip)
            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, context.getString(R.string.failed_to_copy_text), Toast.LENGTH_SHORT).show()
        }
    }
}
