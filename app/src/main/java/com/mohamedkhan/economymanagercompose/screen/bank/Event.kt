package com.mohamedkhan.economymanagercompose.screen.bank

import android.content.Context
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.mohamedkhan.economymanagercompose.database.Bank
import com.mohamedkhan.economymanagercompose.viewModel.DataViewModel

sealed class Event {
    data class OnShowAddBankDialog(
        val value: Boolean
    ) : Event()
    data class OnNameAddBankDialogChange(
        val value: String
    ) : Event()
    data class OnNumberAddBankDialogChange(
        val value: String
    ) : Event()
    data class OnOpeningBalanceAddBankDialogChange(
        val value: String
    ) : Event()
    data class OnAddBank(
        val context: Context,
        val dataViewModel: DataViewModel,
        val onDismiss: () -> Unit
    ): Event()
    data class OnSearchBank(
        val banks: SnapshotStateList<Bank>,
        val value: String
    ) : Event()
    data class OnGetBank(
        val banks: SnapshotStateList<Bank>
    ): Event()
    data class OnShowBankOptionDialog(
        val value: Boolean,
        val selectedBank: Bank
    ) : Event()
    data class OnShowEditBankDialog(
        val value: Boolean,
        val type: String
    ) : Event()
    data class OnNameEditBankChange(
        val value: String
    ) : Event()
    data class OnNumberEditBankChange(
        val value: String
    ) : Event()
    data class OnSaveEditBank(
        val context: Context,
        val type: String,
        val dataViewModel: DataViewModel,
        val onDismiss: () -> Unit
    ) : Event()
}