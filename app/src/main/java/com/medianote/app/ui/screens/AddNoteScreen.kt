package com.medianote.app.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.medianote.app.data.local.AppDatabase
import com.medianote.app.data.local.NoteEntity
import com.medianote.app.ui.components.LoadingIcon
import com.medianote.app.util.AudioRecorder
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

fun copyUriToInternalStorage(context: Context, uri: Uri, fileName: String): String {
    return try {
        val input = context.contentResolver.openInputStream(uri) ?: return ""
        val file = File(context.filesDir, fileName)
        val output = FileOutputStream(file)
        input.copyTo(output)
        input.close()
        output.close()
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

@Composable
fun AddNoteScreen(noteType: String, onFinished: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = remember { AppDatabase.getInstance(context) }
    val recorder = remember { AudioRecorder(context) }

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var filePath by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            isLoading = true
            val path = copyUriToInternalStorage(context, it, "img_${System.currentTimeMillis()}.jpg")
            filePath = path
            isLoading = false
        }
    }
    val videoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            isLoading = true
            val path = copyUriToInternalStorage(context, it, "vid_${System.currentTimeMillis()}.mp4")
            filePath = path
            isLoading = false
        }
    }

    Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("اضافة ملاحظة: $noteType", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("عنوان الملاحظة") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        Spacer(modifier = Modifier.height(12.dp))

        if (noteType == "text") {
            OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("اكتب ملاحظتك") }, modifier = Modifier.fillMaxWidth().height(150.dp), shape = RoundedCornerShape(12.dp))
        }

        if (noteType == "voice") {
            Button(onClick = {
                val path = recorder.startRecording()
                if (path.isNotEmpty()) { filePath = path; isRecording = true } else { error = "فشل بدء التسجيل تحقق من الصلاحيات" }
            }, enabled = !isRecording) { Text("بدء التسجيل") }
            if (isRecording) {
                Button(onClick = { filePath = recorder.stopRecording(); isRecording = false }, modifier = Modifier.padding(top = 8.dp)) { Text("انهاء التسجيل") }
            }
            if (filePath.isNotEmpty()) {
                Text("تم حفظ الصوت: ${File(filePath).name}", modifier = Modifier.padding(top = 8.dp))
            }
        }

        if (noteType == "image") {
            Button(onClick = { imagePicker.launch("image/*") }) { Text("اختيار صورة") }
            if (filePath.isNotEmpty()) {
                val file = File(filePath)
                if (file.exists()) {
                    AsyncImage(model = file, contentDescription = null, modifier = Modifier.fillMaxWidth().height(200.dp).padding(top = 8.dp))
                }
            }
        }

        if (noteType == "video") {
            Button(onClick = { videoPicker.launch("video/*") }) { Text("اختيار فيديو") }
            if (filePath.isNotEmpty()) { Text("تم حفظ الفيديو: ${File(filePath).name}", modifier = Modifier.padding(top = 8.dp)) }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            LoadingIcon(message = "جاري التحميل...", fullScreen = false)
        }

        if (error.isNotEmpty()) {
            Text(error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }

        Button(
            onClick = {
                if (title.isEmpty()) { error = "اكتب عنوان"; return@Button }
                isLoading = true
                scope.launch {
                    try {
                        val note = NoteEntity(title = title, content = if (noteType == "text") content else filePath, type = noteType, filePath = filePath, shareId = UUID.randomUUID().toString().take(8))
                        db.noteDao().insert(note)
                        isLoading = false
                        onFinished()
                    } catch (e: Exception) { isLoading = false; error = "فشل الحفظ: ${e.message}" }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading
        ) { Text("حفظ الملاحظة") }
    }
}
