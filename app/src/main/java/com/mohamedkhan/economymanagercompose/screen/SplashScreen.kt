package com.mohamedkhan.economymanagercompose.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mohamedkhan.economymanagercompose.R
import com.mohamedkhan.economymanagercompose.route.Router
import com.mohamedkhan.economymanagercompose.signin.GoogleAuthClient
import com.mohamedkhan.economymanagercompose.ui.theme.darkGray
import com.mohamedkhan.economymanagercompose.ui.theme.white
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController, googleAuthClient: GoogleAuthClient) {
    val startDestination =
        if (googleAuthClient.getSignedInUser() != null) Router.Main.route else Router.Login.route
    LaunchedEffect(Unit) {
        delay(1000)
        navController.navigate(startDestination) {
            popUpTo(Router.Splash.route) {
                inclusive = true
            }
        }
    }
    val background = if (isSystemInDarkTheme()) {
        darkGray
    } else {
        white
    }
    val iconColor = if (isSystemInDarkTheme()) {
        Color.White
    } else {
        Color.Black}
    Box(modifier = Modifier
        .fillMaxSize()
        .background(background), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.dollar_logo),
            contentDescription = "logo",
            alignment = Alignment.Center,
            colorFilter = ColorFilter.tint(iconColor),
            modifier = Modifier.size(192.dp)
        )
    }
}
