package com.medianote.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.medianote.app.data.preferences.SettingsManager
import com.medianote.app.ui.components.AppBottomBar
import com.medianote.app.ui.screens.AddNoteScreen
import com.medianote.app.ui.screens.HomeScreen
import com.medianote.app.ui.screens.RecordScreen
import com.medianote.app.ui.screens.SettingsScreen
import com.medianote.app.ui.screens.WebViewScreen
import com.medianote.app.ui.theme.MediaNoteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val settingsManager = remember { SettingsManager(context) }
            val darkMode by settingsManager.darkModeFlow.collectAsState(initial = true)
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route ?: "home"
            var webUrl by remember { mutableStateOf("") }

            MediaNoteTheme(darkTheme = darkMode) {
                if (currentRoute == "webview") {
                    WebViewScreen(url = webUrl, onBack = { navController.popBackStack() })
                } else {
                    Scaffold(
                        bottomBar = {
                            if (currentRoute in listOf("home", "record", "settings")) {
                                AppBottomBar(currentRoute = currentRoute, onNavigate = { route ->
                                    navController.navigate(route) {
                                        launchSingleTop = true
                                    }
                                })
                            }
                        }
                    ) { padding ->
                        NavHost(
                            navController = navController,
                            startDestination = "home",
                            modifier = Modifier.padding(padding)
                        ) {
                            composable("home") {
                                HomeScreen(onNavigateToAdd = { type ->
                                    navController.navigate("add/$type")
                                })
                            }
                            composable("add/{type}") { backStack ->
                                val type = backStack.arguments?.getString("type") ?: "text"
                                AddNoteScreen(noteType = type, onFinished = { navController.popBackStack() })
                            }
                            composable("record") { RecordScreen() }
                            composable("settings") {
                                SettingsScreen(onOpenWeb = { url ->
                                    webUrl = url
                                    navController.navigate("webview")
                                })
                            }
                            composable("webview") {
                                WebViewScreen(url = webUrl, onBack = { navController.popBackStack() })
                            }
                        }
                    }
                }
            }
        }
    }
}
