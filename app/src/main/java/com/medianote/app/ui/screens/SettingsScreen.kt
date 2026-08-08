package com.medianote.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.medianote.app.data.preferences.SettingsManager
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(onOpenWeb: (String) -> Unit) {
    val context = LocalContext.current
    val settings = remember { SettingsManager(context) }
    val darkMode by settings.darkModeFlow.collectAsState(initial = true)
    val muted by settings.notificationsMutedFlow.collectAsState(initial = false)
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), shape = RoundedCornerShape(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("الوضع الليلي / النهاري", modifier = Modifier.weight(1f))
                Switch(checked = darkMode, onCheckedChange = { scope.launch { settings.setDarkMode(it) } })
            }
        }
        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), shape = RoundedCornerShape(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("كتم الاشعارات", modifier = Modifier.weight(1f))
                Switch(checked = muted, onCheckedChange = { scope.launch { settings.setNotificationsMuted(it) } })
            }
        }

        Text("روابط", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))

        TextButton(onClick = { onOpenWeb("https://media-note.ct.ws/app-evaluation") }) { Text("تقييم التطبيق") }
        TextButton(onClick = { onOpenWeb("https://media-note.ct.ws/Terms-and-Conditions") }) { Text("الشروط والاحكام") }
        TextButton(onClick = { onOpenWeb("https://media-note.ct.ws/sharing") }) { Text("مشاركة التطبيق") }
        TextButton(onClick = { onOpenWeb("https://media-note.ct.ws/Developer") }) { Text("المطور") }
        TextButton(onClick = { onOpenWeb("https://media-note.ct.ws/comments") }) { Text("ارسل تعليق") }
    }
}
