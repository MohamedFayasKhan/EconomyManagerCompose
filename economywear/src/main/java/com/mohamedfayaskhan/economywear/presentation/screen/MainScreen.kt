package com.mohamedfayaskhan.economywear.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.mohamedfayaskhan.economywear.presentation.route.Router
import com.mohamedfayaskhan.economywear.presentation.signin.GoogleAuthClient
import com.mohamedfayaskhan.economywear.presentation.viewmodel.DataViewModel

@Composable
fun MainScreen(
    googleAuthClient: GoogleAuthClient,
    lifecycleScope: LifecycleCoroutineScope,
    viewModel: DataViewModel,
    navController: NavHostController
) {

    val context = LocalContext.current
    val totalBalance = viewModel.totalBankBalance
    val items = listOf(
        "bank",
        "parties",
        "transaction"
    )
    TimeText()
    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp)
    ) {
        item {
            Text(text = "Total Balance")
        }
        item {
            Text(text = totalBalance.value.toString(), fontSize = 20.sp, modifier = Modifier.padding(vertical = 10.dp))
        }
        items(items) {item ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(33.dp))
                    .background(Color.Gray)
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clickable {
                        onItemClick(item, navController)
                    }
            ) {
                Text(text = item, modifier = Modifier.padding(10.dp))
            }
        }
    }
}

private fun onItemClick(item: String, navController: NavHostController) {
    when (item) {
        "bank" -> {
            navController.navigate(Router.Bank.route)
        }
        "parties" -> {
            navController.navigate(Router.Party.route)
        }
        "transaction" -> {
            navController.navigate(Router.Transaction.route)
        }
    }
}
