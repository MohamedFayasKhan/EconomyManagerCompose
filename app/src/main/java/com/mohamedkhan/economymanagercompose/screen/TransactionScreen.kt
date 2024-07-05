package com.mohamedkhan.economymanagercompose.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mohamedkhan.economymanagercompose.route.Router
import com.mohamedkhan.economymanagercompose.signin.GoogleAuthClient
import com.mohamedkhan.economymanagercompose.viewModel.DataViewModel

@Composable
fun TransactionScreen(
    googleAuthClient: GoogleAuthClient,
    viewModel: DataViewModel,
    navHostController: NavHostController
) {
    Box(modifier = Modifier
        .fillMaxSize()) {
        Column {
            HeaderTransactionComponent(googleAuthClient, navHostController)
            SearchBoxTransaction()
            TransactionsLazyList(viewModel)
        }
    }
}

@Composable
fun TransactionsLazyList(viewModel: DataViewModel) {
    val transactions by viewModel.transactionLiveData.observeAsState(emptyList())
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(transactions) {transition->
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column() {
                    Text(text = transition.subject)
                    Text(text = transition.timeStamp)
                }
                Column {
                    Text(text = transition.amount)
                    Text(text = if (transition.income) "Receive" else "Sent")
                }
            }
        }
    }
}

@Composable
fun SearchBoxTransaction() {
    Spacer(modifier = Modifier.height(32.dp))
}

@Composable
fun HeaderTransactionComponent(
    googleAuthClient: GoogleAuthClient,
    navHostController: NavHostController
) {
    val name = googleAuthClient.getSignedInUser()?.displayName
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = name + "'s")
            Text(text = "Cash Book")
        }
        Icon(imageVector = Icons.Filled.Add, contentDescription = "add", modifier = Modifier.size(50.dp).clickable {
            navHostController.navigate(Router.AddTransaction.route)
        })
    }
}
