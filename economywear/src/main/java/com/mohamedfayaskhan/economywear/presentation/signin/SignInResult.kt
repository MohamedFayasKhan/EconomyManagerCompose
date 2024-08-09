package com.mohamedfayaskhan.economywear.presentation.signin

data class SignInResult(
    val user: UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String,
    val displayName: String?,
    val photoUrl: String?
)
