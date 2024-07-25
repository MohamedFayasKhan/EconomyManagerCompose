package com.mohamedkhan.economymanagercompose.screen

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.mohamedkhan.economymanagercompose.navigation.BottomBar
import com.mohamedkhan.economymanagercompose.navigation.BottomNavigationGraph
import com.mohamedkhan.economymanagercompose.navigation.Screen
import com.mohamedkhan.economymanagercompose.signin.GoogleAuthClient
import com.mohamedkhan.economymanagercompose.viewModel.DataViewModel

@Composable
fun MainScreen(
    googleAuthClient: GoogleAuthClient,
    lifecycleScope: LifecycleCoroutineScope,
    viewModel: DataViewModel,
    navControllerMainActivity: NavHostController
) {
    val context = LocalContext.current
    val navHostController = rememberNavController()
    Scaffold(
        bottomBar = { BottomBar(navHostController = navHostController)}
    ) {innerPadding->
        Box(modifier = Modifier.padding(innerPadding)) {
            BottomNavigationGraph(navHostController = navHostController, googleAuthClient = googleAuthClient, lifecycleScope = lifecycleScope, viewModel= viewModel, navControllerMainActivity = navControllerMainActivity)
        }
        BackHandler {
            val currentDestination = navHostController.currentBackStackEntry?.destination?.route
            if (currentDestination != Screen.HomeScreen.route) {
                navHostController.navigate(Screen.HomeScreen.route) {
                    popUpTo(navHostController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            } else {
                (context as ComponentActivity).finish()
            }
        }
    }
}