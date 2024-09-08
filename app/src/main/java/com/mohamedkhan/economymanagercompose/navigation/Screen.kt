package com.mohamedkhan.economymanagercompose.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    var title: String,
    var selectedIcon: ImageVector,
    var unSelectedIcon: ImageVector,
    var route: String
) {

    data object HomeScreen: Screen(
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unSelectedIcon = Icons.Outlined.Home,
        route = "home"
    )
    data object TransactionScreen: Screen(
        title = "Cashbook",
        selectedIcon = Icons.Filled.List,
        unSelectedIcon = Icons.Outlined.List,
        route = "cashbook"
    )
    data object BankScreen: Screen(
        title = "Bank",
        selectedIcon = Icons.Filled.ShoppingCart,
        unSelectedIcon = Icons.Outlined.ShoppingCart,
        route = "bank"
    )
    data object PartyScreen: Screen(
        title = "Party",
        selectedIcon = Icons.Filled.AccountCircle,
        unSelectedIcon = Icons.Outlined.AccountCircle,
        route = "party"
    )
    data object ProfileScreen: Screen(
        title = "Profile",
        selectedIcon = Icons.Filled.Face,
        unSelectedIcon = Icons.Outlined.Face,
        route = "profile"
    )

}