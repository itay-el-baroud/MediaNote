package com.medianote.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.medianote.app.data.local.AppDatabase
import com.medianote.app.util.ShareUtil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RecordScreen() {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val notes by db.noteDao().getAllNotes().collectAsState(initial = emptyList())

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(notes) { note ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(note.title, style = MaterialTheme.typography.titleMedium)
                    Text("النوع: ${note.type}", style = MaterialTheme.typography.bodySmall)
                    val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(note.createdAt))
                    Text("التاريخ: $date", style = MaterialTheme.typography.bodySmall)
                    if (note.content.isNotEmpty()) {
                        Text(note.content.take(100), modifier = Modifier.padding(top = 8.dp))
                    }
                    Row {
                        TextButton(onClick = {
                            val link = ShareUtil.generateShareLink(note.type, note.shareId)
                            ShareUtil.copyToClipboard(context, link)
                        }) {
                            Text("مشاركة - نسخ الرابط")
                        }
                    }
                }
            }
        }
    }
}
