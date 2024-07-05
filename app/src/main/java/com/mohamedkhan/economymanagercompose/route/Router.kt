package com.mohamedkhan.economymanagercompose.route

sealed class Router(val route: String) {
    object Login: Router("login_screen")
    object Main: Router("main_screen")
    object Loading: Router("loading_screen")
    object AddTransaction: Router("add_transaction")
}