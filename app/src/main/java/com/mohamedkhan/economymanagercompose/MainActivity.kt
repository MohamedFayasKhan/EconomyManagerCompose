package com.mohamedkhan.economymanagercompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import com.mohamedkhan.economymanagercompose.route.Router
import com.mohamedkhan.economymanagercompose.screen.addTransaction.AddTransaction
import com.mohamedkhan.economymanagercompose.screen.MainScreen
import com.mohamedkhan.economymanagercompose.screen.SignInScreen
import com.mohamedkhan.economymanagercompose.screen.SplashScreen
import com.mohamedkhan.economymanagercompose.signin.GoogleAuthClient
import com.mohamedkhan.economymanagercompose.ui.theme.EconomyManagerComposeTheme
import com.mohamedkhan.economymanagercompose.viewModel.DataViewModel
import com.mohamedkhan.economymanagercompose.viewModel.SignInViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    val googleAuthClient by lazy {
        GoogleAuthClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    private lateinit var viewModel: DataViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel = ViewModelProvider(this)[DataViewModel::class.java]
        viewModel.initDatabase(googleAuthClient.getSignedInUser()?.userId)
        setContent {

            EconomyManagerComposeTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Router.Splash.route) {
                    composable(Router.Splash.route) {
                        SplashScreen(navController, googleAuthClient)
                    }
                    composable(Router.Login.route) {
                        val signViewModal = viewModel<SignInViewModel>()
                        val state by signViewModal.state.collectAsStateWithLifecycle()

                        LaunchedEffect(key1 = Unit) {
                            if (googleAuthClient.getSignedInUser() != null) {
                                navController.navigate(Router.Main.route)
                            }
                        }

                        LaunchedEffect(key1 = state.isSignInSuccessful) {
                            if (state.isSignInSuccessful) {
                                viewModel.performTasks {
                                    navController.navigate(Router.Main.route)
                                    signViewModal.resetState()
                                }
                            }
                        }

                        val launcher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.StartIntentSenderForResult(),
                            onResult = { result ->
                                if (result.resultCode == RESULT_OK) {
                                    lifecycleScope.launch {
                                        val signInResult =
                                            googleAuthClient.getSignInResultFromIntent(
                                                intent = result.data ?: return@launch
                                            )
                                        signViewModal.onSignInResult(signInResult)
                                    }
                                }
                            }
                        )
                        SignInScreen(state = state,
                            onClickSignIn = {
                                lifecycleScope.launch {
                                    val signInIntentSender = googleAuthClient.signIn()
                                    launcher.launch(
                                        IntentSenderRequest.Builder(
                                            signInIntentSender ?: return@launch
                                        ).build()
                                    )
                                }
                            })
                    }
                    composable(route = Router.Main.route) {
                        val isComplete = remember {
                            mutableStateOf(false)
                        }
                        LaunchedEffect(Unit) {
                            viewModel.performTasks {
                                isComplete.value = true
                            }
                        }
                        if (isComplete.value) {
                            MainScreen(
                                googleAuthClient = googleAuthClient,
                                lifecycleScope,
                                viewModel,
                                navController
                            )
                        }
                    }
                    composable(route = Router.AddTransaction.route) {
                        AddTransaction(viewModel, navController)
                    }
                }

            }
        }
    }
}