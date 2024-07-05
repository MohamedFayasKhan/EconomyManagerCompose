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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mohamedkhan.economymanagercompose.route.Router
import com.mohamedkhan.economymanagercompose.signin.GoogleAuthClient
import com.mohamedkhan.economymanagercompose.viewModel.DataViewModel

@Composable
fun BankScreen(googleAuthClient: GoogleAuthClient, viewModel: DataViewModel) {
    Box(modifier = Modifier
        .fillMaxSize()) {
        Column {
            HeaderBankComponent(googleAuthClient)
            SearchBoxBank()
            BanksLazyList(viewModel)
        }
    }
}

@Composable
fun BanksLazyList(viewModel: DataViewModel) {
    val banks by viewModel.bankLiveData.observeAsState(emptyList())
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(banks) {bank->
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly) {
                Text(text = bank.name)
                Text(text = bank.number)
                Text(text = bank.balance)
            }
        }
    }
}

@Composable
fun SearchBoxBank() {
    Spacer(modifier = Modifier.height(32.dp))
}

@Composable
fun HeaderBankComponent(googleAuthClient: GoogleAuthClient) {
    val name = googleAuthClient.getSignedInUser()?.displayName
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = name + "'s")
            Text(text = "Bank Accounts")
        }
        Icon(imageVector = Icons.Filled.Add, contentDescription = "add", modifier = Modifier.size(50.dp).clickable {
        })
    }
}
