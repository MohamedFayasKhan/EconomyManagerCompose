package com.mohamedkhan.economymanagercompose.screen

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.mohamedkhan.economymanagercompose.R
import com.mohamedkhan.economymanagercompose.database.Party
import com.mohamedkhan.economymanagercompose.database.ToggleSwitch
import com.mohamedkhan.economymanagercompose.signin.GoogleAuthClient
import com.mohamedkhan.economymanagercompose.viewModel.DataViewModel

@Composable
fun PartyScreen(googleAuthClient: GoogleAuthClient, viewModel: DataViewModel) {
    val filterList = remember {
        mutableStateOf<List<Party>?>(emptyList())
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column {
            HeaderPartyComponent(googleAuthClient, viewModel)
            SearchBoxParty(filterList, viewModel)
            PartiesLazyList(filterList, viewModel)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PartiesLazyList(filterList: MutableState<List<Party>?>, viewModel: DataViewModel) {
    val parties by viewModel.partiesLiveData.observeAsState(emptyList())
    val list = if (filterList.value != null && filterList.value!!.isNotEmpty()) {
        filterList.value as List<Party>
    } else {
        parties
    }
    var showDialog by remember { mutableStateOf(false) }
    val selectedParty = remember {
        mutableStateOf(Party())
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(list.sortedBy { it.name }) { party ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .combinedClickable(
                        onClick = {},
                        onLongClick = {
                            showDialog = true
                            selectedParty.value = party
                        }
                    ),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .size(50.dp)
                ) {
                    Text(text = party.name[0].toString())
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = party.name, fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = party.number, fontSize = 12.sp)
                }
                Column(verticalArrangement = Arrangement.Center) {
                    Text(
                        text = String.format("%.2f", party.balance.toDouble()),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (party.receivable) Color.Green else Color.Red,
                        modifier = Modifier.padding(end = 10.dp)
                    )
                }
            }
        }
    }
    if (showDialog) {
        PartyOptionsDialog(viewModel = viewModel, selectedParty) {
            showDialog = false
        }
    }
}

@Composable
fun PartyOptionsDialog(
    viewModel: DataViewModel,
    selectedParty: MutableState<Party>,
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
                Text(text = "Edit Party Name", modifier = Modifier
                    .padding(10.dp)
                    .clickable {
                        showDialog = true
                        type = "Name"
//                        onDismiss()
                    })
//                Text(text = "Edit Party Balance", modifier = Modifier
//                    .padding(10.dp)
//                    .clickable {
//                        showDialog = true
//                        type = "Balance"
////                        onDismiss()
//                    })
//                Text(text = "Add Party Balance", modifier = Modifier
//                    .padding(10.dp)
//                    .clickable {
//                        showDialog = true
//                        type = "Add"
////                        onDismiss()
//                    })
//                Text(text = "Reduce Party Balance", modifier = Modifier
//                    .padding(10.dp)
//                    .clickable {
//                        showDialog = true
//                        type = "Reduce"
////                        onDismiss()
//                    })
                Text(text = "Edit Borrower", modifier = Modifier
                    .padding(10.dp)
                    .clickable {
                        showDialog = true
                        type = "Borrower"
//                        onDismiss()
                    })
            }
        }
    }
    if (showDialog) {
        ShowEditPartyDialog(viewModel, type, selectedParty) {
            showDialog = false
        }
    }
}

@Composable
fun ShowEditPartyDialog(
    viewModel: DataViewModel,
    type: String,
    selectedParty: MutableState<Party>,
    onDismiss1: () -> Unit
) {
    var name = selectedParty.value.name
    var balance = selectedParty.value.balance
    Dialog(onDismissRequest = onDismiss1) {
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
                val head = if (type.equals("Name")) "Edit Name" else if (type.equals("Balance")) "Edit Balance" else if (type.equals("Add")) "Add balance" else if(type.equals("Reduce balance")) "Reduce balance" else "Edit Borrower"
                Text(text = head)
                Spacer(modifier = Modifier.size(10.dp))
                if (type.equals("Name")) {
                    OutlinedTextField(value = name, onValueChange = {name = it})
                    Button(onClick = {
                        selectedParty.value.name = name
                        viewModel.addParty(selectedParty.value)
                        onDismiss1()
                    }) {
                        Text(text = "Save")
                    }
                }
                if ("Edit Borrower".equals(head)) {
                    var loanSwitch by remember {
                        mutableStateOf(
                            value = ToggleSwitch("Borrower", selectedParty.value.receivable)
                        )
                    }
                    PartyType(loanSwitch = loanSwitch) {
                        loanSwitch = it
                    }
                    Button(onClick = {
                        selectedParty.value.receivable = loanSwitch.isChecked
                        viewModel.addParty(selectedParty.value)
                        onDismiss1()
                    }) {
                        Text(text = "Save")
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBoxParty(filterList: MutableState<List<Party>?>, viewModel: DataViewModel) {
    var searchText by remember {
        mutableStateOf("")
    }
    OutlinedTextField(
        value = searchText,
        onValueChange = { text ->
            searchText = text
            filterList.value = viewModel.partiesLiveData.value?.filter {party ->
                party.name.lowercase().contains(searchText) ||
                        party.number.lowercase().contains(searchText) ||
                        party.balance.lowercase().contains(searchText)
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
fun HeaderPartyComponent(googleAuthClient: GoogleAuthClient, viewModel: DataViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    val name = googleAuthClient.getSignedInUser()?.displayName
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text ="$name's")
            Text(text = "Parties")
        }
        Icon(imageVector = Icons.Filled.Add, contentDescription = "add", modifier = Modifier
            .size(50.dp)
            .clickable {
                showDialog = true
            })
    }
    if (showDialog) {
        AddPartyDialog(viewModel) {
            showDialog = false
        }
    }
}

@Composable
private fun AddPartyDialog(viewModel: DataViewModel, onDismiss: () -> Unit) {
    var name = ""
    var number = ""
    var openingBalance = ""
    var loanSwitch by remember {
        mutableStateOf(
            value = ToggleSwitch("Borrower", false)
        )
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
                    label = { Text(text = "Party Name") })
                Spacer(modifier = Modifier.size(20.dp))
                OutlinedTextField(
                    value = number,
                    onValueChange = { number = it },
                    label = { Text(text = "Party Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.size(20.dp))
                OutlinedTextField(
                    value = openingBalance,
                    onValueChange = { openingBalance = it },
                    label = { Text(text = "Party Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                Spacer(modifier = Modifier.size(20.dp))
                PartyType(loanSwitch = loanSwitch) {
                    loanSwitch = it
                }
                Spacer(modifier = Modifier.size(20.dp))
                Button(onClick = {
                    val id = viewModel.getUniqueDatabaseId()
                    viewModel.addParty(
                        Party(
                            id.toString(),
                            name,
                            number,
                            openingBalance,
                            true,
                            loanSwitch.isChecked
                        )
                    )
                }) {
                    Text(text = "Add Party")
                }
            }
        }
    }
}

@Composable
private fun PartyType(loanSwitch: ToggleSwitch, onSwitchChanged: (ToggleSwitch) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = loanSwitch.text)
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = loanSwitch.isChecked,
            onCheckedChange = { isChecked ->
                onSwitchChanged(loanSwitch.copy(isChecked = isChecked))
            },
            thumbContent = {
                Icon(
                    imageVector = if (loanSwitch.isChecked) {
                        Icons.Filled.Check
                    } else {
                        Icons.Filled.Close
                    },
                    contentDescription = "Switch"
                )
            }
        )
    }
}

