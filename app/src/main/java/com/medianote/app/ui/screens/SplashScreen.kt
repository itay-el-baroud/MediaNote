package com.medianote.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medianote.app.ui.components.LoadingIcon
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000)
        onFinished()
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Card(shape = RoundedCornerShape(32.dp), elevation = CardDefaults.cardElevation(8.dp), modifier = Modifier.size(120.dp)) {
                Box(modifier = Modifier.fillMaxSize().background(Color(0xFF7C4DFF)), contentAlignment = Alignment.Center) {
                    Text(text = "M", color = Color.White, fontSize = 48.sp)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            LoadingIcon(message = "جاري تحميل التطبيق...", fullScreen = false)
        }
    }
}
