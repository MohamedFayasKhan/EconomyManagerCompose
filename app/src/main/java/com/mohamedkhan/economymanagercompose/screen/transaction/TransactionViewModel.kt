package com.mohamedkhan.economymanagercompose.screen.transaction

import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.mohamedkhan.economymanagercompose.constant.Constant
import com.mohamedkhan.economymanagercompose.database.Transaction
import com.mohamedkhan.economymanagercompose.route.Router

class TransactionViewModel: ViewModel() {
    private var filterList by mutableStateOf<List<Transaction>?>(emptyList())
    var transactions by mutableStateOf<List<Transaction>?>(emptyList())
        private set
    var searchText by mutableStateOf("")
        private set
    var showTransactionDialog by mutableStateOf(false)
        private set
    var showOptionsDialog by mutableStateOf(false)
        private set
    var showEditDialog by mutableStateOf(false)
        private set
    var typeEditTransaction by mutableStateOf("")
        private set
    var selectedItem by mutableStateOf(Transaction())
        private set
    var typeViewTransaction by mutableStateOf("")
        private set
    var categoryViewTransaction by mutableStateOf("")
        private set
    var subjectEditTransaction by mutableStateOf("")
        private set
    var dateEditTransaction by mutableStateOf("")
        private set
    var editHead by mutableStateOf("")
        private set
    private var fromViewTransaction by mutableStateOf("")
    private var toViewTransaction by mutableStateOf("")
    private var transferData by mutableStateOf("")

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnNavigateToAddTransaction -> {
                event.navController.navigate(Router.AddTransaction.route)
            }

            is Event.OnSearchTransaction -> {
                searchText = event.value
                filterList = event.transactions.filter {transaction ->
                    transaction.subject.lowercase().contains(searchText.lowercase()) ||
                            transaction.amount.toString().lowercase().contains(searchText.lowercase()) ||
                            transaction.category.lowercase().contains(searchText.lowercase())
                }
            }

            is Event.OnGetTransaction -> {
                transactions = if (filterList != null && filterList!!.isNotEmpty()) {
                    filterList!!.sortedByDescending { it.timeStamp }
                } else {
                    event.transactions.sortedByDescending { it.timeStamp }
                }
            }

            is Event.OnShowOptionsDialog -> {
                showOptionsDialog = event.value
                selectedItem = event.selectedTransaction
            }
            is Event.OnShowTransactionDialog -> {
                showTransactionDialog = event.value
                selectedItem = event.selectedTransaction
            }

            is Event.OnViewTransaction -> {
                categoryViewTransaction = event.categories.filter { event.transaction.category == it.id }[0].name
                typeViewTransaction = when(event.transaction.type) {
                    Constant.SPENT -> {
                        fromViewTransaction = event.banks.filter { event.transaction.from == it.id }[0].name
                        transferData = "Paid from $fromViewTransaction"
                        "Paid from $fromViewTransaction"
                    }

                    Constant.BANK_TO_BANK -> {
                        fromViewTransaction = event.banks.filter { event.transaction.from == it.id }[0].name
                        toViewTransaction = event.banks.filter { event.transaction.to == it.id }[0].name
                        transferData = "Transfer from $fromViewTransaction to $toViewTransaction"
                        "Transfer from $fromViewTransaction to $toViewTransaction"
                    }

                    Constant.BANK_TO_PARTY -> {
                        fromViewTransaction = event.banks.filter { event.transaction.from == it.id }[0].name
                        toViewTransaction = event.parties.filter { event.transaction.to == it.id }[0].name
                        transferData = "Sent from $fromViewTransaction to $toViewTransaction"
                        "Sent from $fromViewTransaction to $toViewTransaction"
                    }

                    Constant.PARTY_TO_BANK -> {
                        toViewTransaction = event.banks.filter { event.transaction.to == it.id }[0].name
                        transferData = "Credited to $toViewTransaction"
                        "Credited to $toViewTransaction"
                    }

                    Constant.ADD_BALANCE_TO_BANK -> {
                        toViewTransaction = event.banks.filter { event.transaction.to == it.id }[0].name
                        transferData = "Credited to $toViewTransaction"
                        "Credited to $toViewTransaction"
                    }

                    Constant.REDUCE_BALANCE_FROM_BANK -> {
                        fromViewTransaction = event.banks.filter { event.transaction.from == it.id }[0].name
                        transferData = "Debited from $fromViewTransaction"
                        "Debited from $fromViewTransaction"
                    }

                    Constant.ADD_BALANCE_TO_PARTY -> {
                        toViewTransaction = event.parties.filter { event.transaction.to == it.id }[0].name
                        transferData = "Added to $toViewTransaction"
                        "Added to $toViewTransaction"
                    }

                    Constant.REDUCE_BALANCE_FROM_PARTY -> {
                        fromViewTransaction = event.parties.filter { event.transaction.from == it.id }[0].name
                        transferData = "Reduced to $fromViewTransaction"
                        "Reduced to $fromViewTransaction"
                    }

                    else -> {
                        "Something went wrong"
                    }
                }
            }

            is Event.OnShareTransaction -> {
                val text =
                    "Transaction Detail\n\nSubject: ${event.transaction.subject}\nAmount: ${event.transaction.amount}\nCategory: ${categoryViewTransaction}\nDate: ${event.date}\n${transferData}"
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, text)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(intent, "Share Transaction")
                event.context.startActivity(shareIntent)
            }

            is Event.OnShowEditDialog -> {
                showEditDialog = event.value
                typeEditTransaction = event.type
                editHead = if (typeEditTransaction == "Date") "Edit Transaction Date" else if (typeEditTransaction == "delete") "Delete Transaction" else "Edit Transaction Subject"
            }

            is Event.OnDateEditChange -> {
                dateEditTransaction = event.value
            }

            is Event.OnEditTransaction -> {
                if (event.type == "Date") {
                    selectedItem.date = event.dataViewModel.dateToEpoch(dateEditTransaction)
                } else {
                    selectedItem.subject = subjectEditTransaction
                }
                event.dataViewModel.upsertTransaction(selectedItem, event.context)
                event.onDismiss()

            }

            is Event.OnDismiss -> event.onDismiss()

            is Event.OnDeleteTransaction -> {
                event.dataViewModel.deleteTransaction(selectedItem) { status ->
                    if (status) {
                        Toast.makeText(
                            event.context,
                            "Transaction deleted",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            event.context,
                            "Transaction not deleted\nInsufficient amount",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    event.onDismiss()
                }
            }

            is Event.OnSubjectEditChange -> subjectEditTransaction = event.value
        }
    }

}