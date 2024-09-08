package com.mohamedkhan.economymanagercompose.screen.transaction

import android.content.Context
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation.NavHostController
import com.mohamedkhan.economymanagercompose.database.Bank
import com.mohamedkhan.economymanagercompose.database.Category
import com.mohamedkhan.economymanagercompose.database.Party
import com.mohamedkhan.economymanagercompose.database.Transaction
import com.mohamedkhan.economymanagercompose.viewModel.DataViewModel

sealed class Event {
    data class OnNavigateToAddTransaction(
        val navController: NavHostController
    ) : Event()
    data class OnSearchTransaction(
        val transactions: SnapshotStateList<Transaction>,
        val value: String
    ) : Event()
    data class OnGetTransaction(
        val transactions: SnapshotStateList<Transaction>
    ) : Event()
    data class OnShowTransactionDialog(
        val value: Boolean,
        val selectedTransaction: Transaction
    ) : Event()
    data class OnShowOptionsDialog(
        val value: Boolean,
        val selectedTransaction: Transaction
    ) : Event()
    data class OnViewTransaction(
        val transactions: SnapshotStateList<Transaction>,
        val banks: SnapshotStateList<Bank>,
        val parties: SnapshotStateList<Party>,
        val categories: SnapshotStateList<Category>,
        val transaction: Transaction
    ) : Event()
    data class OnShareTransaction(
        val context: Context,
        val transaction: Transaction,
        val date: String
    ) : Event()
    data class OnShowEditDialog(
        val value: Boolean,
        val type: String
    ) : Event()
    data class OnDateEditChange(
        val value: String
    ) : Event()
    data class OnSubjectEditChange(
        val value: String
    ) : Event()
    data class OnEditTransaction(
        val type: String,
        val dataViewModel: DataViewModel,
        val context: Context,
        val onDismiss: () -> Unit
    ) : Event()
    data class OnDismiss(
        val onDismiss: () -> Unit
    ) : Event()
    data class OnDeleteTransaction(
        val dataViewModel: DataViewModel,
        val context: Context,
        val onDismiss: () -> Unit
    ) : Event()
}