package com.mohamedfayaskhan.economywear.presentation.signin

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mohamedfayaskhan.economywear.R
import com.mohamedfayaskhan.economywear.presentation.MainActivity

class GoogleAuthClient(
    private val context: Context,
    private val mainActivity: MainActivity
) {
   private val auth = Firebase.auth

    fun getLoggedInUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun getSignedInUser(): UserData? = auth.currentUser?.run {
        UserData(
            userId = uid,
            displayName = displayName,
            photoUrl = photoUrl?.toString()
        )
    }

    suspend fun getSignInIntent(): GoogleSignInClient {
        //         Setup Google Sign-In options and client
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    public fun handleSignInResult(completedTask: Task<GoogleSignInAccount>, onComplete: (SignInResult) -> Unit) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                firebaseAuthWithGoogle(account.idToken!!) {
                    onComplete(it)
                }
            }
        } catch (e: ApiException) {
            Log.w("Google Auth", "Google sign in failed", e)
        }
    }

    // Perform Firebase Authentication with the Google ID token
    private fun firebaseAuthWithGoogle(idToken: String, onComplete: (SignInResult) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(mainActivity as Activity) { task ->
                if (task.isSuccessful) {
                    Log.d("Google Auth", "signInWithCredential:success")
                    val user = FirebaseAuth.getInstance().currentUser
                    onComplete(
                        SignInResult(
                        user = user?.run {
                        UserData(
                            userId = uid,
                            displayName = displayName,
                            photoUrl = photoUrl.toString()
                        )
                    },
                        errorMessage = null
                    ))
                    // Update UI with user info
                } else {
                    Log.w("Google Auth", "signInWithCredential:failure", task.exception)
                    onComplete(SignInResult(null, task.exception?.message))
                }
            }
    }
}