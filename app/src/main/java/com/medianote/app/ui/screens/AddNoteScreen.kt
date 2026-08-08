package com.medianote.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import com.medianote.app.data.local.AppDatabase
import com.medianote.app.data.local.NoteEntity
import com.medianote.app.util.AudioRecorder
import kotlinx.coroutines.launch
import java.util.UUID

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
    var isPaused by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    var success by remember { mutableStateOf("") }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { filePath = it.toString() }
    }
    val videoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { filePath = it.toString() }
    }

    Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("اضافة ملاحظة: $noteType", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("عنوان الملاحظة") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (noteType == "text") {
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("اكتب ملاحظتك") },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                shape = RoundedCornerShape(12.dp)
            )
        }

        if (noteType == "voice") {
            Row {
                Button(onClick = {
                    try {
                        val path = recorder.startRecording()
                        if (path.isNotEmpty()) {
                            filePath = path
                            isRecording = true
                            isPaused = false
                            error = ""
                        } else {
                            error = "فشل بدء التسجيل تحقق من الصلاحيات"
                        }
                    } catch (e: Exception) {
                        error = "خطأ في التسجيل: ${e.message}"
                    }
                }, enabled = !isRecording) { Text("بدء") }

                Button(onClick = {
                    recorder.pauseRecording()
                    isPaused = true
                }, enabled = isRecording && !isPaused, modifier = Modifier.padding(start = 8.dp)) {
                    Text("قطع")
                }

                Button(onClick = {
                    recorder.resumeRecording()
                    isPaused = false
                }, enabled = isRecording && isPaused, modifier = Modifier.padding(start = 8.dp)) {
                    Text("استئناف")
                }

                Button(onClick = {
                    val path = recorder.stopRecording()
                    filePath = path
                    isRecording = false
                    isPaused = false
                    success = "تم انتهاء التسجيل"
                }, enabled = isRecording, modifier = Modifier.padding(start = 8.dp)) {
                    Text("انتهاء")
                }
            }
            if (filePath.isNotEmpty()) {
                Text("ملف الصوت: $filePath", modifier = Modifier.padding(top = 8.dp))
            }
        }

        if (noteType == "image") {
            Button(onClick = { imagePicker.launch("image/*") }) {
                Text("اختيار صورة")
            }
        }

        if (noteType == "video") {
            Button(onClick = { videoPicker.launch("video/*") }) {
                Text("اختيار فيديو")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (error.isNotEmpty()) {
            Text(error, color = MaterialTheme.colorScheme.error)
        }
        if (success.isNotEmpty()) {
            Text(success, color = MaterialTheme.colorScheme.primary)
        }

        if (isLoading) {
            CircularProgressIndicator()
        }

        Button(
            onClick = {
                if (title.isEmpty()) {
                    error = "من فضلك اكتب عنوان"
                    return@Button
                }
                isLoading = true
                scope.launch {
                    try {
                        val note = NoteEntity(
                            title = title,
                            content = if (noteType == "text") content else filePath,
                            type = noteType,
                            filePath = filePath,
                            shareId = UUID.randomUUID().toString().take(8)
                        )
                        db.noteDao().insert(note)
                        isLoading = false
                        onFinished()
                    } catch (e: Exception) {
                        isLoading = false
                        error = "فشل الحفظ: ${e.message}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("حفظ")
        }
    }
}
