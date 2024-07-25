package com.mohamedkhan.economymanagercompose.navigation

import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mohamedkhan.economymanagercompose.screen.BankScreen
import com.mohamedkhan.economymanagercompose.screen.HomeScreen
import com.mohamedkhan.economymanagercompose.screen.PartyScreen
import com.mohamedkhan.economymanagercompose.screen.ProfileScreen
import com.mohamedkhan.economymanagercompose.screen.TransactionScreen
import com.mohamedkhan.economymanagercompose.signin.GoogleAuthClient
import com.mohamedkhan.economymanagercompose.viewModel.DataViewModel

@Composable
fun BottomNavigationGraph(
    navHostController: NavHostController,
    googleAuthClient: GoogleAuthClient,
    lifecycleScope: LifecycleCoroutineScope,
    viewModel: DataViewModel,
    navControllerMainActivity: NavHostController
) {
    NavHost(navController = navHostController, startDestination = Screen.HomeScreen.route){
        composable(route= Screen.HomeScreen.route){
            HomeScreen(googleAuthClient, viewModel)
        }
        composable(route= Screen.TransactionScreen.route){
            TransactionScreen(googleAuthClient, viewModel, navControllerMainActivity)
        }
        composable(route= Screen.BankScreen.route){
            BankScreen(googleAuthClient, viewModel)
        }
        composable(route= Screen.PartyScreen.route){
            PartyScreen(googleAuthClient, viewModel)
        }
        composable(route= Screen.ProfileScreen.route){
            ProfileScreen(googleAuthClient= googleAuthClient, lifecycleScope)
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomBar(navHostController: NavHostController) {
    val screens = listOf(
        Screen.HomeScreen,
        Screen.TransactionScreen,
        Screen.BankScreen,
        Screen.PartyScreen,
        Screen.ProfileScreen
    )
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        screens.forEach{screen ->
            val selected = currentDestination?.hierarchy?.any {
                it.route == screen.route
            }
            if (selected != null) {
                NavigationBarItem(
                    selected = selected,
                    onClick ={
                        navHostController.navigate(screen.route) {
                            popUpTo(navHostController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        BadgedBox(badge = {null}) {
                            Icon(
                                imageVector = if (selected) screen.selectedIcon else screen.unSelectedIcon,
                                contentDescription = screen.title)

                        }
                    },
                    alwaysShowLabel = true,
                    label = {
                        Text(text = screen.title)
                    }
                )
            }
        }
    }
}