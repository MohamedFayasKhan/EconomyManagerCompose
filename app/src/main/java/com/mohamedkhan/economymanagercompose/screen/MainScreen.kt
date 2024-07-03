package com.mohamedkhan.economymanagercompose.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.mohamedkhan.economymanagercompose.navigation.BottomBar
import com.mohamedkhan.economymanagercompose.navigation.BottomNavigationGraph

@Composable
fun MainScreen() {
    val navHostController = rememberNavController()
    Scaffold(
        bottomBar = { BottomBar(navHostController = navHostController)}
    ) {innerpadding->
        Box(modifier = Modifier.padding(innerpadding)) {
            BottomNavigationGraph(navHostController = navHostController)
        }
    }
}