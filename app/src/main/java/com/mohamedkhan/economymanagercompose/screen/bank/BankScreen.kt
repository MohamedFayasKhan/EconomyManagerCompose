package com.mohamedkhan.economymanagercompose.screen.bank

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mohamedkhan.economymanagercompose.R
import com.mohamedkhan.economymanagercompose.signin.GoogleAuthClient
import com.mohamedkhan.economymanagercompose.viewModel.DataViewModel
import java.util.Locale

@Composable
fun BankScreen(googleAuthClient: GoogleAuthClient, dataViewModel: DataViewModel) {

    val bankViewModel = viewModel<BankViewModel>()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        HeaderBank(googleAuthClient, dataViewModel, bankViewModel, context)
        SearchBox(dataViewModel, bankViewModel)
        BankList(context, dataViewModel, bankViewModel)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BankList(context: Context, dataViewModel: DataViewModel, bankViewModel: BankViewModel) {
    bankViewModel.onEvent(Event.OnGetBank(dataViewModel.banks))
    val banks = bankViewModel.banks

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (banks != null) {
            items(banks) { bank ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .height(200.dp)
                        .combinedClickable(
                            onClick = {},
                            onLongClick = {
                                bankViewModel.onEvent(Event.OnShowBankOptionDialog(true, bank))
                            }
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Text(
                            text = bank.name,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                        )
                        Text(
                            text = "Rs." + String.format(
                                Locale.getDefault(),
                                "%.2f",
                                bank.balance.toDouble()
                            ),
                            fontSize = 24.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        Text(
                            text = bank.number,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
    if (bankViewModel.showBankOptionDialog) {
        BankOptionDialog(context, bankViewModel, dataViewModel) {
            bankViewModel.onEvent(
                Event.OnShowBankOptionDialog(
                    false,
                    bankViewModel.selectedBankOptionDialog
                )
            )
        }
    }
}

@Composable
fun BankOptionDialog(
    context: Context,
    bankViewModel: BankViewModel,
    dataViewModel: DataViewModel,
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
                Text(text = "Edit Bank Name", modifier = Modifier
                    .padding(10.dp)
                    .clickable {
                        bankViewModel.onEvent(Event.OnShowEditBankDialog(true, "Name"))
                    }
                )
                Text(text = "Edit Bank Number", modifier = Modifier
                    .padding(10.dp)
                    .clickable {
                        bankViewModel.onEvent(Event.OnShowEditBankDialog(true, "Number"))
                    }
                )
            }
        }
    }

    if (bankViewModel.showEditBankDialog) {
        ShowEditBankDialog(context, dataViewModel, bankViewModel) {
            bankViewModel.onEvent(Event.OnShowEditBankDialog(false, "Number"))
        }
    }
}

@Composable
fun ShowEditBankDialog(
    context: Context,
    dataViewModel: DataViewModel,
    bankViewModel: BankViewModel,
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
                Text(text = bankViewModel.editHead)
                Spacer(modifier = Modifier.size(10.dp))
                if (bankViewModel.typeEditBankDialog == "Name") {
                    bankViewModel.onEvent(Event.OnNameEditBankChange(bankViewModel.selectedBankOptionDialog.name))
                    OutlinedTextField(value = bankViewModel.nameEditBankDialog, onValueChange = {
                        bankViewModel.onEvent(Event.OnNameEditBankChange(it))
                    })
                    Button(onClick = {
                        bankViewModel.onEvent(Event.OnSaveEditBank(
                            context,
                            "Name",
                            dataViewModel,
                            onDismiss
                        ))
                    }) {
                        Text(text = "Save")
                    }
                } else {
                    bankViewModel.onEvent(Event.OnNumberEditBankChange(bankViewModel.selectedBankOptionDialog.number))
                    OutlinedTextField(value = bankViewModel.numberEditBankDialog, onValueChange = {
                        bankViewModel.onEvent(Event.OnNumberEditBankChange(it))
                    })
                    Button(onClick = {
                        bankViewModel.onEvent(Event.OnSaveEditBank(context, "Number", dataViewModel, onDismiss))
                    }) {
                        Text(text = "Save")
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBox(dataViewModel: DataViewModel, bankViewModel: BankViewModel) {
    OutlinedTextField(
        value = bankViewModel.searchText,
        onValueChange = { value ->
            bankViewModel.onEvent(Event.OnSearchBank(dataViewModel.banks, value))
        },
        label = {
            Text(text = stringResource(id = R.string.search))
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = stringResource(R.string.search)
            )
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun HeaderBank(
    googleAuthClient: GoogleAuthClient,
    dataViewModel: DataViewModel,
    bankViewModel: BankViewModel,
    context: Context
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = "${googleAuthClient.getSignedInUser()?.displayName}'s")
            Text(text = "Bank Accounts")
        }
        Icon(imageVector = Icons.Filled.Add, contentDescription = "add", modifier = Modifier
            .size(50.dp)
            .clickable {
                bankViewModel.onEvent(Event.OnShowAddBankDialog(value = true))
            })
    }

    if (bankViewModel.showAddBankDialog) {
        AddBankDialog(context, dataViewModel, bankViewModel) {
            bankViewModel.onEvent(Event.OnShowAddBankDialog(value = true))
        }
    }
}

@Composable
fun AddBankDialog(
    context: Context,
    dataViewModel: DataViewModel,
    bankViewModel: BankViewModel,
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
                OutlinedTextField(
                    value = bankViewModel.nameAddBankDialog,
                    onValueChange = {
                        bankViewModel.onEvent(Event.OnNameAddBankDialogChange(it))
                    },
                    label = {
                        Text(text = "Bank Name")
                    })
                Spacer(modifier = Modifier.size(20.dp))
                OutlinedTextField(
                    value = bankViewModel.numberAddBankDialog,
                    onValueChange = {
                        bankViewModel.onEvent(Event.OnNumberAddBankDialogChange(it))
                    },
                    label = {
                        Text(text = "Bank Number")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.size(20.dp))
                OutlinedTextField(
                    value = bankViewModel.openingBalanceAddBankDialog,
                    onValueChange = {
                        bankViewModel.onEvent(Event.OnOpeningBalanceAddBankDialogChange(it))
                    },
                    label = {
                        Text(text = "Opening Balance")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                Spacer(modifier = Modifier.size(20.dp))
                Button(onClick = {
                    bankViewModel.onEvent(Event.OnAddBank(context, dataViewModel, onDismiss))
                }) {
                    Text(text = "Add Bank")
                }

            }
        }
    }
}