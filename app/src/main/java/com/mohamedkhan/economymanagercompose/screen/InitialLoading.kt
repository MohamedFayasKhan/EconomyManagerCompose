package com.mohamedkhan.economymanagercompose.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.mohamedkhan.economymanagercompose.route.Router
import com.mohamedkhan.economymanagercompose.signin.GoogleAuthClient
import com.mohamedkhan.economymanagercompose.viewModel.DataViewModel
import kotlinx.coroutines.coroutineScope

@Composable
fun InitialLoading(
    googleAuthClient: GoogleAuthClient,
    navHostController: NavHostController,
    viewModel: DataViewModel
) {

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }

    LaunchedEffect(Unit) {
        viewModel.performTasks {
            navHostController.navigate(Router.Main.route) {
                popUpTo(navHostController.graph.startDestinationId)
                launchSingleTop = true
            }
        }
    }
}