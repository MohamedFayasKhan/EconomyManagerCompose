package com.mohamedkhan.economymanagercompose.route

sealed class Router(val route: String) {
    data object Login: Router("login_screen")
    data object Main: Router("main_screen")
    data object Splash: Router("splash_screen")
    data object AddTransaction: Router("add_transaction")
}