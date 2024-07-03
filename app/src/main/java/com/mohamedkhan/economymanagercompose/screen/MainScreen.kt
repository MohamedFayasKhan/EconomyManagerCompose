package com.mohamedkhan.economymanagercompose.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.compose.rememberNavController
import com.mohamedkhan.economymanagercompose.navigation.BottomBar
import com.mohamedkhan.economymanagercompose.navigation.BottomNavigationGraph
import com.mohamedkhan.economymanagercompose.signin.GoogleAuthClient

@Composable
fun MainScreen(googleAuthClient: GoogleAuthClient, lifecycleScope: LifecycleCoroutineScope) {
    val navHostController = rememberNavController()
    Scaffold(
        bottomBar = { BottomBar(navHostController = navHostController)}
    ) {innerpadding->
        Box(modifier = Modifier.padding(innerpadding)) {
            BottomNavigationGraph(navHostController = navHostController, googleAuthClient = googleAuthClient, lifecycleScope = lifecycleScope)
        }
    }
}