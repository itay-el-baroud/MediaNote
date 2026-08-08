package com.medianote.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.medianote.app.data.local.AppDatabase
import com.medianote.app.ui.components.LoadingIcon
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay

@Composable
fun RecordScreen(onNoteClick: (Int) -> Unit) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val notes by db.noteDao().getAllNotes().collectAsState(initial = emptyList())
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(notes) {
        delay(800)
        isLoading = false
    }

    if (isLoading) {
        LoadingIcon(message = "جاري تحميل الملاحظات...", fullScreen = true)
    } else {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(notes) { note ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).clickable { onNoteClick(note.id) },
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(note.title, style = MaterialTheme.typography.titleMedium)
                        val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(note.createdAt))
                        Text("التاريخ: $date", style = MaterialTheme.typography.bodySmall)
                        if (note.type == "image" && note.filePath.isNotEmpty()) {
                            val file = File(note.filePath)
                            if (file.exists()) {
                                AsyncImage(model = file, contentDescription = null, modifier = Modifier.fillMaxWidth().height(150.dp).padding(top = 8.dp))
                            }
                        }
                        if (note.type == "text") {
                            Text(note.content.take(80), modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                }
            }
        }
    }
}
