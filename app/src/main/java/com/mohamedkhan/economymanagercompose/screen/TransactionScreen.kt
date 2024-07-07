package com.mohamedkhan.economymanagercompose.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.mohamedkhan.economymanagercompose.R
import com.mohamedkhan.economymanagercompose.constant.Constant
import com.mohamedkhan.economymanagercompose.database.Transaction
import com.mohamedkhan.economymanagercompose.route.Router
import com.mohamedkhan.economymanagercompose.signin.GoogleAuthClient
import com.mohamedkhan.economymanagercompose.viewModel.DataViewModel

@Composable
fun TransactionScreen(
    googleAuthClient: GoogleAuthClient,
    viewModel: DataViewModel,
    navHostController: NavHostController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column {
            HeaderTransactionComponent(googleAuthClient, navHostController)
            SearchBoxTransaction()
            TransactionsLazyList(viewModel)
        }
    }
}

@Composable
private fun TransactionDetail(
    viewModel: DataViewModel,
    transaction: Transaction,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    text =
                    when (transaction.type) {
                        Constant.SPENT -> {
                            val from =
                                viewModel.bankLiveData.value?.filter { transaction.from == it.id }
                            "Paid from " + from?.get(0)?.name
                        }

                        Constant.BANK_TO_BANK -> {
                            val from = viewModel.bankLiveData.value?.filter { transaction.from == it.id }
                            val to = viewModel.bankLiveData.value?.filter { transaction.to == it.id }
                            "Transfer from " + from?.get(0)?.name + " to " + to?.get(0)?.name
                        }

                        Constant.BANK_TO_PARTY -> {
                            val from =
                                viewModel.bankLiveData.value?.filter { transaction.from == it.id }
                            val to =
                                viewModel.partiesLiveData.value?.filter { transaction.to == it.id }
                            "Sent from " + from?.get(0)?.name + " to " + to?.get(0)?.name
                        }

                        Constant.PARTY_TO_BANK -> {
                            val to =
                                viewModel.bankLiveData.value?.filter { transaction.to == it.id }
                            "Credited to " + to?.get(0)?.name
                        }

                        Constant.ADD_BALANCE_TO_BANK -> {
                            val to =
                                viewModel.bankLiveData.value?.filter { transaction.to == it.id }
                            "Credited to " + to?.get(0)?.name
                        }

                        Constant.REDUCE_BALANCE_FROM_BANK -> {
                            val from =
                                viewModel.bankLiveData.value?.filter { transaction.from == it.id }
                            "Debited from " + from?.get(0)?.name
                        }

                        Constant.ADD_BALANCE_TO_PARTY -> {
                            val to =
                                viewModel.partiesLiveData.value?.filter { transaction.to == it.id }
                            "Added to " + to?.get(0)?.name
                        }

                        Constant.REDUCE_BALANCE_FROM_PARTY -> {
                            val from =
                                viewModel.partiesLiveData.value?.filter { transaction.from == it.id }
                            "Reduced to " + from?.get(0)?.name
                        }

                        else -> {
                            "Something went wrong"
                        }
                    })
                Spacer(modifier = Modifier.size(5.dp))
                Text(text = "on " + transaction.date)
                Spacer(modifier = Modifier.size(30.dp))
                Text(
                    text = "Rs." + transaction.amount,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(30.dp))
                Text(
                    text = "Posted on " + transaction.timeStamp
                )
                Spacer(modifier = Modifier.size(30.dp))
                Text(
                    text = "Category",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                val cat = viewModel.categoryLiveData.value?.filter { transaction.category == it.id }
                Text(
                    text = cat?.get(0)?.name.toString()
                )
                Spacer(modifier = Modifier.size(30.dp))
                Text(
                    text = "Transaction Id",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = transaction.id
                )
                Spacer(modifier = Modifier.size(30.dp))
            }
        }
    }
}

@Composable
fun TransactionsLazyList(viewModel: DataViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    val transactions by viewModel.transactionLiveData.observeAsState(emptyList())
    var selectedItem by remember { mutableStateOf(Transaction()) }
    var transactionDate = ""
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(transactions) { transaction ->
            Box(
                modifier = Modifier.clickable {
                    showDialog = true
                    selectedItem = transaction
                }
            ) {
                if (transactionDate == "" || transactionDate != transaction.timeStamp.split(" ")[0]) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 10.dp)
                    ) {
                        Spacer(
                            modifier = Modifier
                                .size(2.dp)
                                .background(Color.Gray)
                                .weight(0.5f)
                        )
                        Text(
                            text = transaction.timeStamp.split(" ")[0],
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 5.dp, end = 5.dp)
                        )
                        Spacer(
                            modifier = Modifier
                                .size(2.dp)
                                .background(Color.Gray)
                                .weight(0.5f)
                        )
                        transactionDate = transaction.timeStamp.split(" ")[0]
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Gray)
                            .size(50.dp)
                    ) {
                        Image(
                            painter = if (transaction.type == Constant.BANK_TO_BANK) {
                                painterResource(id = R.drawable.transfer)
                            } else if (transaction.income) {
                                painterResource(id = R.drawable.arrow_income)
                            } else {
                                painterResource(id = R.drawable.arrow_expense)
                            },
                            contentDescription = "Rounded Corner Image",
                            modifier = Modifier.size(25.dp)
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 10.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = transaction.subject, fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = transaction.timeStamp, fontSize = 10.sp)
                    }
                    Column(verticalArrangement = Arrangement.Center) {
                        Text(
                            text = transaction.amount, fontSize = 16.sp,
                            fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 10.dp)
                        )
                    }
                }
            }
        }
    }
    if (showDialog) {
        TransactionDetail(
            viewModel = viewModel,
            transaction = selectedItem,
            onDismiss = { showDialog = false })
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
        Icon(imageVector = Icons.Filled.Add, contentDescription = "add", modifier = Modifier
            .size(50.dp)
            .clickable {
                navHostController.navigate(Router.AddTransaction.route)
            })
    }
}
