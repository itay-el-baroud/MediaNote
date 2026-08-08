package com.medianote.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.medianote.app.ui.components.LoadingIcon
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(onNavigateToAdd: (String) -> Unit) {
    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(600)
        isLoading = false
    }
    if (isLoading) {
        LoadingIcon(message = "جاري تحميل الرئيسية...", fullScreen = true)
    } else {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("MediaNote", style = MaterialTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { onNavigateToAdd("text") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) { Text("اضافة نص") }
                Button(onClick = { onNavigateToAdd("voice") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), shape = RoundedCornerShape(12.dp)) { Text("تسجيل صوتي") }
                Button(onClick = { onNavigateToAdd("image") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), shape = RoundedCornerShape(12.dp)) { Text("اضافة صورة") }
                Button(onClick = { onNavigateToAdd("video") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), shape = RoundedCornerShape(12.dp)) { Text("اضافة فيديو") }
            }
        }
    }
}
