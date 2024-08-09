/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.mohamedfayaskhan.economywear.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.mohamedfayaskhan.economywear.R
import com.mohamedfayaskhan.economywear.presentation.route.Router
import com.mohamedfayaskhan.economywear.presentation.screen.BankScreen
import com.mohamedfayaskhan.economywear.presentation.screen.LoginScreen
import com.mohamedfayaskhan.economywear.presentation.screen.MainScreen
import com.mohamedfayaskhan.economywear.presentation.screen.PartyScreen
import com.mohamedfayaskhan.economywear.presentation.screen.TransactionScreen
import com.mohamedfayaskhan.economywear.presentation.signin.GoogleAuthClient
import com.mohamedfayaskhan.economywear.presentation.theme.EconomyManagerComposeTheme
import com.mohamedfayaskhan.economywear.presentation.viewmodel.DataViewModel
import com.mohamedfayaskhan.economywear.presentation.viewmodel.SignInViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val googleAuthClient by lazy {
        GoogleAuthClient(
            context = applicationContext,
            this@MainActivity
        )
    }
    private lateinit var viewModel: DataViewModel
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>



    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)

        viewModel = ViewModelProvider(this)[DataViewModel::class.java]
        viewModel.initDatabase(googleAuthClient.getSignedInUser()?.userId)

        googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            googleAuthClient.handleSignInResult(task) {

            }
        }

        setContent {
            val navController = rememberNavController()
            val startDestination = if (googleAuthClient.getLoggedInUser() != null) {
                Router.Main.route
            } else {
                Router.Login.route
            }

            NavHost(navController = navController, startDestination = startDestination) {
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
                composable(route = Router.Login.route) {
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

                    LoginScreen(
                        state = state,
                        onClickSignIn = {
                            lifecycleScope.launch {
                                val signInIntent = googleAuthClient.getSignInIntent().signInIntent
                                googleSignInLauncher.launch(signInIntent)
                            }
                        })
                }
                composable(route = Router.Bank.route) {
                    BankScreen(viewModel)
                }
                composable(route = Router.Party.route) {
                    PartyScreen(viewModel)
                }
                composable(route = Router.Transaction.route) {
                    TransactionScreen(viewModel)
                }
            }
////            WearApp("Android")
//            Wearable.getDataClient(this).addListener { dataEventBuffer ->
//                for (event in dataEventBuffer) {
//                    if (event.type == DataEvent.TYPE_CHANGED && event.dataItem.uri.path == "/auth-token") {
//                        val idToken =
//                            DataMapItem.fromDataItem(event.dataItem).dataMap.getString("idToken")
//                        // Use idToken to authenticate with Firebase
//                        Toast.makeText(this, idToken, Toast.LENGTH_LONG).show()
//                    }
//                }
//            }
        }
    }
}


@Composable
fun WearApp(greetingName: String) {
    EconomyManagerComposeTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            TimeText()
            Greeting(greetingName = greetingName)
        }
    }
}

@Composable
fun Greeting(greetingName: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = stringResource(R.string.hello_world, greetingName)
    )
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp("Preview Android")
}