package com.mohamedkhan.economymanagercompose.screen

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
    navHostControllerMainActivity: NavHostController
) {
    val filterList = remember {
        mutableStateOf<List<Transaction>?>(emptyList())
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column {
            HeaderTransactionComponent(googleAuthClient, navHostControllerMainActivity)
            SearchBoxTransaction(filterList, viewModel)
            TransactionsLazyList(filterList, viewModel)
        }
    }
}

@Composable
private fun TransactionDetail(
    viewModel: DataViewModel,
    transaction: Transaction,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var transferData: String? = null
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
                Spacer(modifier = Modifier.size(5.dp))
                Text(
                    text = transaction.subject,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    text =
                    when (transaction.type) {
                        Constant.SPENT -> {

                            val from =
                                viewModel.bankLiveData.filter { transaction.from == it.id }
                            transferData = "Paid from " + from[0].name
                            "Paid from " + from[0].name
                        }

                        Constant.BANK_TO_BANK -> {
                            val from =
                                viewModel.bankLiveData.filter { transaction.from == it.id }
                            val to =
                                viewModel.bankLiveData.filter { transaction.to == it.id }
                            transferData =
                                "Transfer from " + from[0].name + " to " + to[0].name
                            "Transfer from " + from[0].name + " to " + to[0].name
                        }

                        Constant.BANK_TO_PARTY -> {
                            val from =
                                viewModel.bankLiveData.filter { transaction.from == it.id }
                            val to =
                                viewModel.partiesLiveData.filter { transaction.to == it.id }
                            transferData =
                                "Sent from " + from[0].name + " to " + to[0].name
                            "Sent from " + from[0].name + " to " + to[0].name
                        }

                        Constant.PARTY_TO_BANK -> {
                            val to =
                                viewModel.bankLiveData.filter { transaction.to == it.id }
                            transferData = "Credited to " + to[0].name
                            "Credited to " + to[0].name
                        }

                        Constant.ADD_BALANCE_TO_BANK -> {
                            val to =
                                viewModel.bankLiveData.filter { transaction.to == it.id }
                            transferData = "Credited to " + to[0].name
                            "Credited to " + to[0].name
                        }

                        Constant.REDUCE_BALANCE_FROM_BANK -> {
                            val from =
                                viewModel.bankLiveData.filter { transaction.from == it.id }
                            transferData = "Debited from " + from[0].name
                            "Debited from " + from[0].name
                        }

                        Constant.ADD_BALANCE_TO_PARTY -> {
                            val to =
                                viewModel.partiesLiveData.filter { transaction.to == it.id }
                            transferData = "Added to " + to[0].name
                            "Added to " + to[0].name
                        }

                        Constant.REDUCE_BALANCE_FROM_PARTY -> {
                            val from =
                                viewModel.partiesLiveData.filter { transaction.from == it.id }
                            transferData = "Reduced to " + from[0].name
                            "Reduced to " + from[0].name
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
                val cat = viewModel.categoryLiveData.filter { transaction.category == it.id }
                Text(
                    text = cat[0].name
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
                Spacer(modifier = Modifier.size(5.dp))

                Button(onClick = {
                    shareTransaction(
                        context,
                        transaction,
                        transferData,
                        cat[0].name
                    )
                }) {
                    Text(text = "Share")
                }
                Spacer(modifier = Modifier.size(5.dp))
            }
        }
    }
}

private fun shareTransaction(
    context: Context,
    transaction: Transaction,
    transferData: String?,
    category: String
) {
    val text =
        "Transaction Detail\n\nSubject: ${transaction.subject}\nAmount: ${transaction.amount}\nCategory: ${category}\nDate: ${transaction.date}\n${transferData ?: ""}"
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(intent, null)
    context.startActivity(shareIntent)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionsLazyList(filterList: MutableState<List<Transaction>?>, viewModel: DataViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var showOptions by remember { mutableStateOf(false) }
    val transactions = viewModel.transactionLiveData
    var selectedItem by remember { mutableStateOf(Transaction()) }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        val list = if (filterList.value != null && filterList.value!!.isNotEmpty()) {
            filterList.value as List<Transaction>
        } else {
            transactions
        }

        items(list.distinct()) { transaction ->
            Box(
                modifier = Modifier.combinedClickable(
                    onClick = {
                        showDialog = true
                        selectedItem = transaction
                    },
                    onLongClick = {
                        showOptions = true
                        selectedItem = transaction
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
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
    if (showOptions) {
        TransactionOptionDialog(viewModel, selectedItem) {
            showOptions = false
        }
    }
}

@Composable
private fun TransactionOptionDialog(
    viewModel: DataViewModel,
    selectedItem: Transaction,
    onDismiss: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var type by remember { mutableStateOf("")}
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Edit Date", modifier = Modifier
                    .padding(10.dp)
                    .clickable {
                        showDialog = true
                        type = "Date"
                    })
                Text(text = "Edit Subject", modifier = Modifier
                    .padding(10.dp)
                    .clickable {
                        showDialog = true
                        type = "Subject"
                    })
                Text(text = "Delete Transaction", modifier = Modifier
                    .padding(10.dp)
                    .clickable {
                        showDialog = true
                        type = "delete"
                    })
            }
        }
    }
    if (showDialog) {
        ShowEditTransactionDialog(viewModel, type, selectedItem) {
            showDialog = false
            onDismiss()
        }
    }
}

@Composable
fun ShowEditTransactionDialog(
    viewModel: DataViewModel,
    type: String,
    selectedItem: Transaction,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val subject = remember {
        mutableStateOf(selectedItem.subject)
    }
    val date = remember {
        mutableStateOf(selectedItem.date)
    }
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val head = if (type == "Date") "Edit Transaction Date" else if (type == "delete") "Delete Transaction" else "Edit Transaction Subject"
                Text(text = head)
                Spacer(modifier = Modifier.size(10.dp))
                when (type) {
                    "Date" -> {
                        TextFieldDate(date = date)
                        Button(onClick = {
                            if (date.value != "") {
                                selectedItem.date = date.value
                                viewModel.upsertTransaction(selectedItem, context)
                                onDismiss()
                            } else {
                                Toast.makeText(context, "Fill all fields", Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Text(text = "Save")
                        }
                    }
                    "delete" -> {
                        Text(text = "Are you sure you want to delete ${selectedItem.subject} from transactions?")
                        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                            Button(onClick = {
                                onDismiss()
                            }) {
                                Text(text = "Cancel")
                            }
                            Button(onClick = {
                                viewModel.deleteTransaction(transaction = selectedItem) {status ->
                                    if (status) {
                                        Toast.makeText(context, "Transaction deleted", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Transaction not deleted\nInsufficient amount", Toast.LENGTH_SHORT).show()
                                    }
                                    onDismiss()
                                }
                            }) {
                                Text(text = "Delete")
                            }
                        }
                    }
                    else -> {
                        TextFieldSubject(subject = subject)
                        Button(onClick = {
                            if (subject.value != "") {
                                selectedItem.subject = subject.value
                                viewModel.upsertTransaction(selectedItem, context)
                                onDismiss()
                            } else {
                                Toast.makeText(context, "Fill all fields", Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Text(text = "Save")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBoxTransaction(filterList: MutableState<List<Transaction>?>, viewModel: DataViewModel) {
    var searchText by remember {
        mutableStateOf("")
    }
    OutlinedTextField(
        value = searchText,
        onValueChange = { text ->
            searchText = text
            filterList.value = viewModel.transactionLiveData.filter {transaction ->
                transaction.subject.lowercase().contains(searchText.lowercase()) ||
                        transaction.amount.lowercase().contains(searchText.lowercase()) ||
                        transaction.category.lowercase().contains(searchText.lowercase())
            }

        },
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = stringResource(R.string.search))
        },
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(text = stringResource(id = R.string.search))
        },
    )
}

@Composable
fun HeaderTransactionComponent(
    googleAuthClient: GoogleAuthClient,
    navHostControllerMainActivity: NavHostController
) {
    val name = googleAuthClient.getSignedInUser()?.displayName
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = "$name's")
            Text(text = "Cash Book")
        }
        Icon(imageVector = Icons.Filled.Add, contentDescription = "add", modifier = Modifier
            .size(50.dp)
            .clickable {
                navHostControllerMainActivity.navigate(Router.AddTransaction.route)
            })
    }
}
