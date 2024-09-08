package com.mohamedkhan.economymanagercompose.screen.bank

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.mohamedkhan.economymanagercompose.database.Bank

class BankViewModel: ViewModel() {
    private var filterList by mutableStateOf<List<Bank>?>(emptyList())
    var banks by mutableStateOf<List<Bank>?>(emptyList())
        private set
    var showBankOptionDialog by mutableStateOf(false)
        private set
    var selectedBankOptionDialog by mutableStateOf(Bank())
        private set
    var showEditBankDialog by mutableStateOf(false)
        private set
    var typeEditBankDialog by mutableStateOf("")
        private set
    var nameEditBankDialog by mutableStateOf("")
        private set
    var numberEditBankDialog by mutableStateOf("")
        private set
    var searchText by mutableStateOf("")
        private set
    var showAddBankDialog by mutableStateOf(false)
        private set
    var nameAddBankDialog by mutableStateOf("")
        private set
    var numberAddBankDialog by mutableStateOf("")
        private set
    var openingBalanceAddBankDialog by mutableStateOf("")
        private set
    var editHead by mutableStateOf("")
        private set

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnShowAddBankDialog -> showAddBankDialog = event.value

            is Event.OnNameAddBankDialogChange -> nameAddBankDialog = event.value

            is Event.OnNumberAddBankDialogChange -> numberAddBankDialog = event.value

            is Event.OnOpeningBalanceAddBankDialogChange -> openingBalanceAddBankDialog = event.value

            is Event.OnAddBank -> {
                if (nameAddBankDialog.isNotEmpty() && numberAddBankDialog.isNotEmpty() && openingBalanceAddBankDialog.isNotEmpty()) {
                    val id = event.dataViewModel.getUniqueDatabaseId()
                    event.dataViewModel.addBank(Bank(id.toString(), nameAddBankDialog, numberAddBankDialog, openingBalanceAddBankDialog, true))
                    event.onDismiss()
                } else {
                    Toast.makeText(event.context, "Fill all fields", Toast.LENGTH_SHORT).show()
                }
            }

            is Event.OnSearchBank -> {
                searchText = event.value
                filterList = event.banks.filter {bank ->
                    bank.name.lowercase().contains(searchText) ||
                            bank.number.lowercase().contains(searchText) ||
                            bank.balance.lowercase().contains(searchText)
                }
            }

            is Event.OnGetBank -> {
                banks = if (filterList != null && filterList!!.isNotEmpty()) {
                    filterList!!.distinct().sortedBy { it.name }
                } else {
                    event.banks
                }
            }

            is Event.OnShowBankOptionDialog -> {
                showBankOptionDialog = event.value
                selectedBankOptionDialog = event.selectedBank
            }

            is Event.OnShowEditBankDialog -> {
                showEditBankDialog = event.value
                typeEditBankDialog = event.type
                editHead = if (typeEditBankDialog == "Name") "Edit Name" else "Edit Number"
            }

            is Event.OnNameEditBankChange -> {
                nameEditBankDialog = event.value
            }

            is Event.OnNumberEditBankChange -> {
                numberEditBankDialog = event.value
            }

            is Event.OnSaveEditBank -> {
                if (nameEditBankDialog != "" || numberEditBankDialog != "") {
                    if (event.type == typeEditBankDialog) {
                        selectedBankOptionDialog.name = nameEditBankDialog
                        nameEditBankDialog = ""
                    } else {
                        selectedBankOptionDialog.number = numberEditBankDialog
                        numberEditBankDialog = ""
                    }
                    event.dataViewModel.addBank(selectedBankOptionDialog)
                    event.onDismiss()
                } else {
                    Toast.makeText(event.context, "Fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}