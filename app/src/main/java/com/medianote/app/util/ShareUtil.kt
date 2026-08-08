package com.medianote.app.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import java.util.UUID

object ShareUtil {
    fun generateShareLink(type: String, shareId: String): String {
        val folder = when (type) {
            "voice" -> "voice"
            "image" -> "image"
            "video" -> "Videos"
            else -> "text"
        }
        val id = if (shareId.isEmpty()) UUID.randomUUID().toString().take(8) else shareId
        return "https://media-note.ct.ws/notebook/$folder/$id"
    }

    fun copyToClipboard(context: Context, link: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("MediaNote Link", link)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "تم نسخ رابط المشاركة", Toast.LENGTH_SHORT).show()
    }
}
