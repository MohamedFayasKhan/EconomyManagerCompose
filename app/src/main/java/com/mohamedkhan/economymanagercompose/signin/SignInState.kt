package com.mohamedkhan.economymanagercompose.signin

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)