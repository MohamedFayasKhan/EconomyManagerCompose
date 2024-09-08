package com.mohamedkhan.economymanagercompose.screen.transaction

import android.content.Context
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.mohamedkhan.economymanagercompose.R
import com.mohamedkhan.economymanagercompose.constant.Constant
import com.mohamedkhan.economymanagercompose.database.Transaction
import com.mohamedkhan.economymanagercompose.signin.GoogleAuthClient
import com.mohamedkhan.economymanagercompose.viewModel.DataViewModel

@Composable
fun TransactionScreen(
    googleAuthClient: GoogleAuthClient,
    dataViewModel: DataViewModel,
    navHostControllerMainActivity: NavHostController
) {
    val transactionViewModel = viewModel<TransactionViewModel>()
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        HeaderTransactionComponent(
            googleAuthClient,
            transactionViewModel,
            navHostControllerMainActivity
        )
        SearchBoxTransaction(dataViewModel, transactionViewModel)
        TransactionList(context, dataViewModel, transactionViewModel)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionList(
    context: Context,
    dataViewModel: DataViewModel,
    transactionViewModel: TransactionViewModel
) {
    transactionViewModel.onEvent(Event.OnGetTransaction(dataViewModel.transactions))
    val transactions = transactionViewModel.transactions
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (transactions != null) {
            items(transactions) { transaction ->
                Box(
                    modifier = Modifier.combinedClickable(
                        onClick = {
                            transactionViewModel.onEvent(
                                Event.OnShowTransactionDialog(
                                    true,
                                    transaction
                                )
                            )
                        },
                        onLongClick = {
                            transactionViewModel.onEvent(
                                Event.OnShowOptionsDialog(
                                    true,
                                    transaction
                                )
                            )
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
                            Text(
                                text = dataViewModel.epochToTimeStamp(transaction.timeStamp),
                                fontSize = 10.sp
                            )
                        }
                        Column(verticalArrangement = Arrangement.Center) {
                            Text(
                                text = transaction.amount.toString(),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
    if (transactionViewModel.showTransactionDialog) {
        TransactionDetailDialog(
            context,
            transactionViewModel,
            dataViewModel,
            transactionViewModel.selectedItem
        ) {
            transactionViewModel.onEvent(
                Event.OnShowTransactionDialog(
                    false,
                    transactionViewModel.selectedItem
                )
            )
        }
    }
    if (transactionViewModel.showOptionsDialog) {
        TransactionOptionsDialog(
            context,
            dataViewModel,
            transactionViewModel,
        ) {
            transactionViewModel.onEvent(
                Event.OnShowOptionsDialog(
                    false,
                    transactionViewModel.selectedItem
                )
            )
        }
    }
}

@Composable
fun TransactionOptionsDialog(
    context: Context,
    dataViewModel: DataViewModel,
    transactionViewModel: TransactionViewModel,
    onDismiss: () -> Unit
) {
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
                        transactionViewModel.onEvent(Event.OnShowEditDialog(true, "Date"))
                    })
                Text(text = "Edit Subject", modifier = Modifier
                    .padding(10.dp)
                    .clickable {
                        transactionViewModel.onEvent(Event.OnShowEditDialog(true, "Subject"))
                    })
                Text(text = "Delete Transaction", modifier = Modifier
                    .padding(10.dp)
                    .clickable {
                        transactionViewModel.onEvent(Event.OnShowEditDialog(true, "delete"))
                    })
            }
        }
    }
    if (transactionViewModel.showEditDialog) {
        TransactionEditDialog(context, dataViewModel, transactionViewModel) {
            transactionViewModel.onEvent(Event.OnShowEditDialog(false, "delete"))
        }
    }
}

@Composable
fun TransactionEditDialog(
    context: Context,
    dataViewModel: DataViewModel,
    transactionViewModel: TransactionViewModel,
    onDismiss: () -> Unit
) {
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
                Text(text = transactionViewModel.editHead)
                Spacer(modifier = Modifier.size(10.dp))
                when (transactionViewModel.typeEditTransaction) {
                    "Date" -> {
                        TextFieldDate(transactionViewModel)
                        Button(onClick = {
                            transactionViewModel.onEvent(Event.OnEditTransaction("Date", dataViewModel, context, onDismiss))
                        }) {
                            Text(text = "Save")
                        }
                    }

                    "delete" -> {
                        Text(text = "Are you sure you want to delete ${transactionViewModel.selectedItem.subject} from transactions?")
                        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                            Button(onClick = {
                                transactionViewModel.onEvent(Event.OnDismiss(onDismiss))
                            }) {
                                Text(text = "Cancel")
                            }
                            Button(onClick = {
                                transactionViewModel.onEvent(Event.OnDeleteTransaction(dataViewModel, context, onDismiss))
                            }) {
                                Text(text = "Delete")
                            }
                        }
                    }

                    else -> {
                        TextFieldSubject(transactionViewModel = transactionViewModel)
                        Button(onClick = {
                            transactionViewModel.onEvent(Event.OnEditTransaction("Subject", dataViewModel, context, onDismiss))
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
fun TextFieldDate(transactionViewModel: TransactionViewModel) {
    OutlinedTextField(
        value = transactionViewModel.dateEditTransaction,
        onValueChange = {
            transactionViewModel.onEvent(Event.OnDateEditChange(it))
        },
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(text = "Date")
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.Clear,
                contentDescription = "Clear",
                modifier = Modifier.clickable {
                    transactionViewModel.onEvent(Event.OnDateEditChange(""))
                }
            )
        }
    )
}

@Composable
fun TextFieldSubject(transactionViewModel: TransactionViewModel) {
    OutlinedTextField(
        value = transactionViewModel.subjectEditTransaction,
        onValueChange = {
            transactionViewModel.onEvent(Event.OnSubjectEditChange(it))
        },
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(text = "Subject")
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.Clear,
                contentDescription = "Clear",
                modifier = Modifier.clickable {
                    transactionViewModel.onEvent(Event.OnSubjectEditChange(""))
                }
            )
        }
    )
}

@Composable
fun TransactionDetailDialog(
    context: Context,
    transactionViewModel: TransactionViewModel,
    dataViewModel: DataViewModel,
    transaction: Transaction,
    onDismiss: () -> Unit
) {
    transactionViewModel.onEvent(
        Event.OnViewTransaction(
            dataViewModel.transactions,
            dataViewModel.banks,
            dataViewModel.parties,
            dataViewModel.categories,
            transaction
        )
    )
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
                    text = transactionViewModel.typeViewTransaction
                )
                Spacer(modifier = Modifier.size(5.dp))
                Text(text = "on " + dataViewModel.epochToDate(transaction.date))
                Spacer(modifier = Modifier.size(30.dp))
                Text(
                    text = "Rs." + transaction.amount.toString(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(30.dp))
                Text(
                    text = "Posted on " + dataViewModel.epochToTimeStamp(transaction.timeStamp)
                )
                Spacer(modifier = Modifier.size(30.dp))
                Text(
                    text = "Category",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = transactionViewModel.categoryViewTransaction
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
                    transactionViewModel.onEvent(
                        Event.OnShareTransaction(
                            context,
                            transaction,
                            dataViewModel.epochToDate(transaction.date)
                        )
                    )
                }) {
                    Text(text = "Share")
                }
                Spacer(modifier = Modifier.size(5.dp))
            }
        }
    }
}

@Composable
fun SearchBoxTransaction(dataViewModel: DataViewModel, transactionViewModel: TransactionViewModel) {
    OutlinedTextField(
        value = transactionViewModel.searchText,
        onValueChange = { text ->
            transactionViewModel.onEvent(
                Event.OnSearchTransaction(
                    dataViewModel.transactions,
                    text
                )
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = stringResource(R.string.search)
            )
        },
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(text = stringResource(id = R.string.search))
        })
}

@Composable
fun HeaderTransactionComponent(
    googleAuthClient: GoogleAuthClient,
    transactionViewModel: TransactionViewModel,
    navHostControllerMainActivity: NavHostController
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = "${googleAuthClient.getSignedInUser()?.displayName}'s")
            Text(text = "Cash Book")
        }
        Icon(imageVector = Icons.Filled.Add, contentDescription = "add", modifier = Modifier
            .size(50.dp)
            .clickable {
                transactionViewModel.onEvent(
                    Event.OnNavigateToAddTransaction(
                        navHostControllerMainActivity
                    )
                )
            })
    }
}
