package com.medianote.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigateToAdd: (String) -> Unit) {
    var showSheet by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "مرحبا بك في MediaNote",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = 32.dp)
            )
            Text(
                text = "اضغط على زر + لاضافة ملاحظة جديدة",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
        FloatingActionButton(
            onClick = { showSheet = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "اضافة")
        }
        if (showSheet) {
            ModalBottomSheet(onDismissRequest = { showSheet = false }) {
                Column(modifier = Modifier.padding(24.dp)) {
                    TextButton(onClick = { showSheet = false; onNavigateToAdd("text") }) {
                        Text("نص - كتابة ملاحظة")
                    }
                    TextButton(onClick = { showSheet = false; onNavigateToAdd("voice") }) {
                        Text("تسجيل صوتي")
                    }
                    TextButton(onClick = { showSheet = false; onNavigateToAdd("image") }) {
                        Text("صورة من الاستوديو")
                    }
                    TextButton(onClick = { showSheet = false; onNavigateToAdd("video") }) {
                        Text("فيديو من الاستوديو")
                    }
                }
            }
        }
    }
}
