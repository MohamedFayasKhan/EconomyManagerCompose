package com.mohamedkhan.economymanagercompose.screen

import android.widget.Toast
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.mohamedkhan.economymanagercompose.R
import com.mohamedkhan.economymanagercompose.database.Bank
import com.mohamedkhan.economymanagercompose.signin.GoogleAuthClient
import com.mohamedkhan.economymanagercompose.viewModel.DataViewModel

@Composable
fun BankScreen(googleAuthClient: GoogleAuthClient, viewModel: DataViewModel) {
    val filterList = remember {
        mutableStateOf<List<Bank>?>(emptyList())
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column {
            HeaderBankComponent(googleAuthClient, viewModel)
            SearchBoxBank(filterList, viewModel)
            BanksLazyList(filterList, viewModel)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BanksLazyList(filterList: MutableState<List<Bank>?>, viewModel: DataViewModel) {
    val banks = viewModel.bankLiveData
    val list = if (filterList.value != null && filterList.value!!.size > 0) {
        filterList.value as List<Bank>
    } else {
        banks
    }

    var showDialog by remember { mutableStateOf(false) }
    var selectedBank = remember {
        mutableStateOf(Bank())
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(list.distinct().sortedBy { it.name }) {bank ->
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(200.dp)
                    .combinedClickable(
                        onClick = {},
                        onLongClick = {
                            showDialog = true
                            selectedBank.value = bank
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
                        text = "Rs." + String.format("%.2f", bank.balance.toDouble()),
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
    if (showDialog) {
        BankOptionDialog(viewModel, selectedBank) {
            showDialog = false
        }
    }
}

@Composable
fun BankOptionDialog(viewModel: DataViewModel, selectedBank: MutableState<Bank>, onDismiss: () -> Unit) {
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
                Text(text = "Edit Bank Name", modifier = Modifier
                    .padding(10.dp)
                    .clickable {
                        showDialog = true
                        type = "Name"
                    })
                Text(text = "Edit Bank Number", modifier = Modifier
                    .padding(10.dp)
                    .clickable {
                        showDialog = true
                        type = "Number"
                    })
            }
        }
    }
    if (showDialog) {
        ShowEditBankDialog(viewModel, type, selectedBank) {
            showDialog = false
            onDismiss()
        }
    }
}

@Composable
fun ShowEditBankDialog(
    viewModel: DataViewModel,
    type: String,
    selectedBank: MutableState<Bank>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var name by remember {
        mutableStateOf(selectedBank.value.name)
    }
    var number by remember {
        mutableStateOf(selectedBank.value.number)
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
                val head = if (type.equals("Name")) "Edit Name" else "Edit Number"
                Text(text = head)
                Spacer(modifier = Modifier.size(10.dp))
                if (type.equals("Name")) {
                    OutlinedTextField(value = name, onValueChange = {name = it})
                    Button(onClick = {
                        if (name != "") {
                            selectedBank.value.name = name
                            viewModel.addBank(selectedBank.value)
                            onDismiss()
                        } else {
                            Toast.makeText(context, "Fill all fields", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text(text = "Save")
                    }
                } else {
                    OutlinedTextField(value = number, onValueChange = {number = it}, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    Button(onClick = {
                        selectedBank.value.number = number
                        viewModel.addBank(selectedBank.value)
                        onDismiss()
                    }) {
                        Text(text = "Save")
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBoxBank(filterList: MutableState<List<Bank>?>, viewModel: DataViewModel) {
    var searchText by remember {
        mutableStateOf("")
    }
    OutlinedTextField(
        value = searchText,
        onValueChange = { text ->
            searchText = text
            filterList.value = viewModel.bankLiveData.filter {bank ->
                bank.name.lowercase().contains(searchText) ||
                        bank.number.lowercase().contains(searchText) ||
                        bank.balance.lowercase().contains(searchText)
            }

        },
        label = {
                Text(text = stringResource(id = R.string.search))
        },
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = stringResource(R.string.search))
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun HeaderBankComponent(googleAuthClient: GoogleAuthClient, viewModel: DataViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    val name = googleAuthClient.getSignedInUser()?.displayName
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = "$name's")
            Text(text = "Bank Accounts")
        }
        Icon(imageVector = Icons.Filled.Add, contentDescription = "add", modifier = Modifier
            .size(50.dp)
            .clickable {
                showDialog = true
            })
    }
    if (showDialog) {
        AddBankDialog(viewModel) {
            showDialog = false
        }
    }
}

@Composable
private fun AddBankDialog(viewModel: DataViewModel, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var name by remember {
        mutableStateOf("")
    }
    var number by remember {
        mutableStateOf("")
    }
    var openingBalance by remember {
        mutableStateOf("0")
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
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = {
                        Text(text = "Bank Name")
                    })
                Spacer(modifier = Modifier.size(20.dp))
                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it },
                    label = {
                        Text(text = "Bank Number")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.size(20.dp))
                OutlinedTextField(
                    value = openingBalance,
                    onValueChange = { openingBalance = it },
                    label = {
                        Text(text = "Opening Balance")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                Spacer(modifier = Modifier.size(20.dp))
                Button(onClick = {
                    if (name != "" && number != "" && openingBalance != "") {
                        val id = viewModel.getUniqueDatabaseId()
                        viewModel.addBank(Bank(id.toString(), name, number, openingBalance, true))
                        onDismiss()
                    } else {
                        Toast.makeText(context, "Fill all fields", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text(text = "Add Bank")
                }
            }
        }
    }
}
