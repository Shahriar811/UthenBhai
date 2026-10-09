package com.mdshahriar.uthenbhai

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.MapsInitializer
import com.mdshahriar.uthenbhai.data.ProfileSessionStore
import com.mdshahriar.uthenbhai.ui.screens.LoginScreen
import com.mdshahriar.uthenbhai.ui.screens.MainRideScreen
import com.mdshahriar.uthenbhai.ui.theme.UthenBhaiTheme
import com.mdshahriar.uthenbhai.ui.locale.LanguageStore
import com.mdshahriar.uthenbhai.ui.locale.LocalAppLanguage
import com.mdshahriar.uthenbhai.ui.components.LocationAccessGate

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Google Maps renderer
        try {
            MapsInitializer.initialize(applicationContext, MapsInitializer.Renderer.LATEST) { renderer ->
                Log.d("MainActivity", "Google Maps initialized with renderer: $renderer")
            }
        } catch (e: Exception) {
            Log.w("MainActivity", "MapsInitializer error: ${e.message}")
        }

        setContent {
            val systemDark = isSystemInDarkTheme()
            var isDarkTheme by remember {
                val preferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
                mutableStateOf(if (preferences.contains("dark_theme"))
                    preferences.getBoolean("dark_theme", systemDark) else systemDark)
            }
            var appLanguage by remember { mutableStateOf(LanguageStore.load(this)) }

            var loggedInUser by remember { mutableStateOf(ProfileSessionStore.load(this)) }

            UthenBhaiTheme(darkTheme = isDarkTheme) {
                CompositionLocalProvider(LocalAppLanguage provides appLanguage) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LocationAccessGate {
                    if (loggedInUser == null) {
                        // Open with Login Page (Google login style)
                        LoginScreen(
                            onLoginSuccess = { profile ->
                                ProfileSessionStore.save(this, profile)
                                loggedInUser = profile
                            }
                        )
                    } else {
                        key(loggedInUser!!.uid) {
                        // Main Ride Screen with clean state (no default location selected)
                        MainRideScreen(
                            currentUser = loggedInUser!!,
                            hasLocationPermission = true,
                            onRequestLocationPermission = {},
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = {
                                isDarkTheme = it
                                getSharedPreferences("app_preferences", MODE_PRIVATE)
                                    .edit().putBoolean("dark_theme", it).apply()
                            },
                            appLanguage = appLanguage,
                            onLanguageChange = {
                                appLanguage = it
                                LanguageStore.save(this, it)
                            },
                            onSignOut = {
                                ProfileSessionStore.clear(this)
                                loggedInUser = null
                            }
                        )
                        }
                    }
                    }
                }
                }
            }
        }
    }

}
