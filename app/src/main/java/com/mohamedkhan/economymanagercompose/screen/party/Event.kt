package com.mohamedkhan.economymanagercompose.screen.party

import android.content.Context
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.mohamedkhan.economymanagercompose.database.Party
import com.mohamedkhan.economymanagercompose.database.ToggleSwitch
import com.mohamedkhan.economymanagercompose.viewModel.DataViewModel

sealed class Event {
    data class OnShowAddPartyDialog(
        val value: Boolean
    ): Event()
    data class OnNameAddPartyChange(
        val value: String
    ): Event()
    data class OnNumberAddPartyChange(
        val value: String
    ): Event()
    data class OnOpeningBalanceAddPartyChange(
        val value: String
    ): Event()
    data class OnLoanSwitchAddPartyChange(
        val value: ToggleSwitch
    ): Event()
    data class OnAddParty(
        val context: Context,
        val dataViewModel: DataViewModel,
        val onDismiss: () -> Unit
    ) : Event()
    data class OnSearchTextChange(
        val parties: SnapshotStateList<Party>,
        val value: String
    ): Event()
    data class OnGetParties(
        val parties: SnapshotStateList<Party>,
    ): Event()
    data class OnShowPartyOptionDialog(
        val value: Boolean,
        val selectedParty: Party
    ) : Event()
    data class OnShowEditPartyDialog(
        val value: Boolean,
        val type: String
    ) : Event()
    data class OnNameEditPartyChange(
        val value: String
    ): Event()
    data class OnNumberEditPartyChange(
        val value: String
    ): Event()
    data class OnLoanSwitchEditPartyChange(
        val value: ToggleSwitch
    ): Event()
    data class OnSaveEditParty(
        val context: Context,
        val type: String,
        val dataViewModel: DataViewModel,
        val onDismiss: () -> Unit
    ) : Event()
}