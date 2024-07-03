package com.mohamedkhan.economymanagercompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.mohamedkhan.economymanagercompose.navigation.BottomNavigationGraph
import com.mohamedkhan.economymanagercompose.screen.MainScreen
import com.mohamedkhan.economymanagercompose.ui.theme.EconomyManagerComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen()
        setContent {
            EconomyManagerComposeTheme {
                MainScreen()
            }
        }
    }
}