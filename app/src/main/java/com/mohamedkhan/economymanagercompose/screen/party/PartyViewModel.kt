package com.mohamedkhan.economymanagercompose.screen.party

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.mohamedkhan.economymanagercompose.database.Party
import com.mohamedkhan.economymanagercompose.database.ToggleSwitch

class PartyViewModel : ViewModel() {
    private var filterList by mutableStateOf<List<Party>?>(emptyList())
    var parties by mutableStateOf<List<Party>?>(emptyList())
        private set
    var showAddPartyDialog by mutableStateOf(false)
        private set
    var nameAddPartyDialog by mutableStateOf("")
        private set
    var numberAddPartyDialog by mutableStateOf("")
        private set
    var openingBalanceAddPartyDialog by mutableStateOf("")
        private set
    var loanSwitch by mutableStateOf(value = ToggleSwitch("Borrower", false))
        private set
    var searchText by mutableStateOf("")
        private set
    var selectedParty by mutableStateOf(Party())
        private set
    var showPartyOptionDialog by mutableStateOf(false)
        private set
    var typeEditParty by mutableStateOf("")
        private set
    var showEditPartyDialog by mutableStateOf(false)
        private set
    var nameEditParty by mutableStateOf("")
        private set
    var numberEditParty by mutableStateOf("")
        private set
    var loanSwitchEditParty by mutableStateOf(value = ToggleSwitch("Borrower", false))
        private set
    var editHead by mutableStateOf("")
        private set


    fun onEvent(event: Event) {
        when (event) {
            is Event.OnShowAddPartyDialog -> showAddPartyDialog = event.value

            is Event.OnLoanSwitchAddPartyChange -> loanSwitch = event.value

            is Event.OnNameAddPartyChange -> nameAddPartyDialog = event.value

            is Event.OnNumberAddPartyChange -> numberAddPartyDialog = event.value

            is Event.OnOpeningBalanceAddPartyChange -> openingBalanceAddPartyDialog = event.value

            is Event.OnAddParty -> {
                if (nameAddPartyDialog != "" && numberAddPartyDialog != "" && numberAddPartyDialog != "") {
                    val id = event.dataViewModel.getUniqueDatabaseId()
                    event.dataViewModel.addParty(
                        Party(
                            id.toString(),
                            nameAddPartyDialog,
                            numberAddPartyDialog,
                            numberAddPartyDialog,
                            true,
                            loanSwitch.isChecked
                        )
                    )
                    event.onDismiss()
                } else {
                    Toast.makeText(event.context, "Fill all fields", Toast.LENGTH_SHORT).show()
                }
            }

            is Event.OnSearchTextChange -> {
                searchText = event.value
                filterList = event.parties.filter { party ->
                    party.name.lowercase().contains(searchText) ||
                            party.number.lowercase().contains(searchText) ||
                            party.balance.lowercase().contains(searchText)
                }
            }

            is Event.OnGetParties -> {
                parties = if (filterList != null && filterList!!.isNotEmpty()) {
                    filterList!!.distinct().sortedBy { it.name }
                } else {
                    event.parties
                }
            }

            is Event.OnShowPartyOptionDialog -> {
                showPartyOptionDialog = event.value
                selectedParty = event.selectedParty
            }

            is Event.OnShowEditPartyDialog -> {
                typeEditParty = event.type
                showEditPartyDialog = event.value
                editHead = if (typeEditParty == "Name") "Edit Name" else if (typeEditParty == "Number") "Edit Number" else "Edit Borrower"
            }

            is Event.OnLoanSwitchEditPartyChange -> loanSwitchEditParty = event.value

            is Event.OnNameEditPartyChange -> nameEditParty = event.value

            is Event.OnNumberEditPartyChange -> numberEditParty = event.value

            is Event.OnSaveEditParty -> {
                if (nameEditParty != "" || numberEditParty != "") {
                    when (event.type) {
                        "Name" -> {
                            selectedParty.name = nameEditParty
                            nameEditParty = ""
                        }
                        "Number" -> {
                            selectedParty.number = numberEditParty
                            numberEditParty = ""
                        }
                        "Borrower" -> {
                            selectedParty.receivable = loanSwitchEditParty.isChecked
                        }
                    }
                    event.dataViewModel.addParty(selectedParty)
                    event.onDismiss()
                } else {
                    Toast.makeText(event.context, "Fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}