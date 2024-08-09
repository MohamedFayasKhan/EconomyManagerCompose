package com.mohamedfayaskhan.economywear.presentation.route

sealed class Router(val route: String) {
    data object Login: Router("login_screen")
    data object Main: Router("main_screen")
    data object Bank: Router("bank_screen")
    data object Party: Router("party_screen")
    data object Transaction: Router("transaction_screen")
}