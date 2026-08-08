package com.medianote.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.medianote.app.data.local.AppDatabase
import com.medianote.app.data.local.NoteEntity
import com.medianote.app.ui.components.LoadingIcon
import com.medianote.app.util.ShareUtil
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(noteId: Int, onBack: () -> Unit, onOpenWeb: (String) -> Unit) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    var note by remember { mutableStateOf<NoteEntity?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(noteId) {
        isLoading = true
        note = db.noteDao().getById(noteId)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تفاصيل الملاحظة") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                LoadingIcon(message = "جاري تحميل الملاحظة...", fullScreen = false)
            }
        } else {
            note?.let { n ->
                Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
                    Text(n.title, style = MaterialTheme.typography.headlineSmall)
                    val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(n.createdAt))
                    Text("التاريخ: $date", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                    Text("النوع: ${n.type}", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(16.dp))
                    if (n.type == "text") {
                        Text(n.content, style = MaterialTheme.typography.bodyLarge)
                    }
                    if (n.type == "image" && n.filePath.isNotEmpty()) {
                        val file = File(n.filePath)
                        if (file.exists()) {
                            AsyncImage(model = file, contentDescription = null, modifier = Modifier.fillMaxWidth().height(300.dp))
                        }
                    }
                    if (n.type == "video" && n.filePath.isNotEmpty()) {
                        Text("فيديو محفوظ: ${File(n.filePath).name}", modifier = Modifier.padding(top = 8.dp))
                    }
                    if (n.type == "voice" && n.filePath.isNotEmpty()) {
                        Text("ملف صوتي: ${File(n.filePath).name}")
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = {
                        val link = ShareUtil.generateShareLink(n.type, n.shareId)
                        ShareUtil.copyToClipboard(context, link)
                    }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                        Text("نسخ رابط المشاركة")
                    }
                    Button(onClick = {
                        val link = ShareUtil.generateShareLink(n.type, n.shareId)
                        onOpenWeb(link)
                    }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), shape = RoundedCornerShape(12.dp)) {
                        Text("فتح الرابط")
                    }
                }
            } ?: run {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("الملاحظة غير موجودة")
                }
            }
        }
    }
}
