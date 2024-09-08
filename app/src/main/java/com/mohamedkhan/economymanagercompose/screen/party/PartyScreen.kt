package com.mohamedkhan.economymanagercompose.screen.party

import android.content.Context
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mohamedkhan.economymanagercompose.R
import com.mohamedkhan.economymanagercompose.database.ToggleSwitch
import com.mohamedkhan.economymanagercompose.signin.GoogleAuthClient
import com.mohamedkhan.economymanagercompose.viewModel.DataViewModel
import java.util.Locale

@Composable
fun PartyScreen(googleAuthClient: GoogleAuthClient, dataViewModel: DataViewModel) {
    val partyViewModel = viewModel<PartyViewModel>()
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column {
            HeaderPartyComponent(googleAuthClient, dataViewModel, partyViewModel, context)
            SearchBoxParty(dataViewModel, partyViewModel)
            PartiesLazyList(context, dataViewModel, partyViewModel)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PartiesLazyList(context: Context, dataViewModel: DataViewModel, partyViewModel: PartyViewModel) {
    partyViewModel.onEvent(Event.OnGetParties(dataViewModel.parties))
    val parties = partyViewModel.parties

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (parties != null) {
            items(parties) { party ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .combinedClickable(
                            onClick = {},
                            onLongClick = {
                                partyViewModel.onEvent(Event.OnShowPartyOptionDialog(true, party))
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
                            text = String.format(Locale.getDefault(), "%.2f", party.balance.toDouble()),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (party.receivable) Color.Green else Color.Red,
                            modifier = Modifier.padding(end = 10.dp)
                        )
                    }
                }
            }
        }
    }
    if (partyViewModel.showPartyOptionDialog) {
        PartyOptionsDialog(context, dataViewModel, partyViewModel) {
            partyViewModel.onEvent(Event.OnShowPartyOptionDialog(false, partyViewModel.selectedParty))
        }
    }
}

@Composable
fun PartyOptionsDialog(
    context: Context,
    dataViewModel: DataViewModel,
    partyViewModel: PartyViewModel,
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
                Text(text = "Edit Party Name", modifier = Modifier
                    .padding(10.dp)
                    .clickable {
                        partyViewModel.onEvent(Event.OnShowEditPartyDialog(true, "Name"))
                    })
                Text(text = "Edit Party Number", modifier = Modifier
                    .padding(10.dp)
                    .clickable {
                        partyViewModel.onEvent(Event.OnShowEditPartyDialog(true, "Number"))
                    })
                Text(text = "Edit Borrower", modifier = Modifier
                    .padding(10.dp)
                    .clickable {
                        partyViewModel.onEvent(Event.OnShowEditPartyDialog(true, "Borrower"))
                    })
            }
        }
    }
    if (partyViewModel.showEditPartyDialog) {
        ShowEditPartyDialog(context, dataViewModel, partyViewModel) {
            partyViewModel.onEvent(Event.OnShowEditPartyDialog(false, "Borrower"))
        }
    }
}

@Composable
fun ShowEditPartyDialog(
    context: Context,
    dataViewModel: DataViewModel,
    partyViewModel: PartyViewModel,
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
                Text(text = partyViewModel.editHead)
                Spacer(modifier = Modifier.size(10.dp))
                if (partyViewModel.typeEditParty == "Name") {
                    partyViewModel.onEvent(Event.OnNameEditPartyChange(partyViewModel.selectedParty.name))
                    OutlinedTextField(value = partyViewModel.nameEditParty, onValueChange = {
                        partyViewModel.onEvent(Event.OnNameEditPartyChange(it))
                    })
                    Button(onClick = {
                        partyViewModel.onEvent(Event.OnSaveEditParty(context, "Name", dataViewModel, onDismiss))
                    }) {
                        Text(text = "Save")
                    }
                } else if (partyViewModel.typeEditParty == "Number") {
                    partyViewModel.onEvent(Event.OnNumberEditPartyChange(partyViewModel.selectedParty.number))
                    OutlinedTextField(value = partyViewModel.numberEditParty, onValueChange = {
                        partyViewModel.onEvent(Event.OnNumberEditPartyChange(it))
                    }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    Button(onClick = {
                        partyViewModel.onEvent(Event.OnSaveEditParty(context, "Number", dataViewModel, onDismiss))
                    }) {
                        Text(text = "Save")
                    }
                }
                if ("Borrower" == partyViewModel.typeEditParty) {
                    partyViewModel.onEvent(Event.OnLoanSwitchEditPartyChange(ToggleSwitch("Borrower", partyViewModel.selectedParty.receivable)))
                    PartyType(loanSwitch = partyViewModel.loanSwitchEditParty) {
                        partyViewModel.onEvent(Event.OnLoanSwitchEditPartyChange(it))
                    }
                    Button(onClick = {
                        partyViewModel.onEvent(Event.OnSaveEditParty(context, "Borrower", dataViewModel, onDismiss))
                    }) {
                        Text(text = "Save")
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBoxParty(dataViewModel: DataViewModel, partyViewModel: PartyViewModel) {
    OutlinedTextField(
        value = partyViewModel.searchText,
        onValueChange = { text ->
            partyViewModel.onEvent(Event.OnSearchTextChange(dataViewModel.parties, text))
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
fun HeaderPartyComponent(
    googleAuthClient: GoogleAuthClient,
    dataViewModel: DataViewModel,
    partyViewModel: PartyViewModel,
    context: Context
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text ="${googleAuthClient.getSignedInUser()?.displayName}'s")
            Text(text = "Parties")
        }
        Icon(imageVector = Icons.Filled.Add, contentDescription = "add", modifier = Modifier
            .size(50.dp)
            .clickable {
                partyViewModel.onEvent(Event.OnShowAddPartyDialog(true))
            })
    }
    if (partyViewModel.showAddPartyDialog) {
        AddPartyDialog(context, dataViewModel, partyViewModel) {
            partyViewModel.onEvent(Event.OnShowAddPartyDialog(false))
        }
    }
}

@Composable
fun AddPartyDialog(
    context: Context,
    dataViewModel: DataViewModel,
    partyViewModel: PartyViewModel,
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
                    value = partyViewModel.nameAddPartyDialog,
                    onValueChange = {
                        partyViewModel.onEvent(Event.OnNameAddPartyChange(it))
                    },
                    label = { Text(text = "Party Name") })
                Spacer(modifier = Modifier.size(20.dp))
                OutlinedTextField(
                    value = partyViewModel.numberAddPartyDialog,
                    onValueChange = {
                        partyViewModel.onEvent(Event.OnNumberAddPartyChange(it))
                    },
                    label = { Text(text = "Party Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.size(20.dp))
                OutlinedTextField(
                    value = partyViewModel.openingBalanceAddPartyDialog,
                    onValueChange = {
                        partyViewModel.onEvent(Event.OnOpeningBalanceAddPartyChange(it))
                    },
                    label = { Text(text = "Opening Balance") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                Spacer(modifier = Modifier.size(20.dp))
                PartyType(loanSwitch = partyViewModel.loanSwitch) {
                    partyViewModel.onEvent(Event.OnLoanSwitchAddPartyChange(it))
                }
                Spacer(modifier = Modifier.size(20.dp))
                Button(onClick = {
                    partyViewModel.onEvent(Event.OnAddParty(context, dataViewModel, onDismiss))
                }) {
                    Text(text = "Add Party")
                }
            }
        }
    }
}

@Composable
fun PartyType(loanSwitch: ToggleSwitch, onSwitchChanged: (ToggleSwitch) -> Unit) {
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


