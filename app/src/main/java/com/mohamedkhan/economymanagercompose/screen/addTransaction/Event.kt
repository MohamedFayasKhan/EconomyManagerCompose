package com.mohamedkhan.economymanagercompose.screen.addTransaction

import android.content.Context
import androidx.navigation.NavHostController
import com.mohamedkhan.economymanagercompose.database.Bank
import com.mohamedkhan.economymanagercompose.database.Category
import com.mohamedkhan.economymanagercompose.database.Party
import com.mohamedkhan.economymanagercompose.database.ToggleSwitch
import com.mohamedkhan.economymanagercompose.database.Type

sealed class Event {
    data class OnAddCategoryChange(var value: Boolean) : Event()
    data class OnNewCategoryChange(var value: String) : Event()
    data class OnTypeChange(var value: String) : Event()
    data class OnTypeUpdate(var value: Boolean) : Event()
    data class OnFromChange(var value: String) : Event()
    data class OnFromValueChange(var value: String) : Event()
    data class OnToChange(var value: String) : Event()
    data class OnToValueChange(var value: String) : Event()
    data class OnLoanSwitchValueChange(var value: ToggleSwitch) : Event()
    data class OnAddEventClick(
        val it: Boolean,
        val navController: NavHostController,
        val context: Context
    ) : Event()

    data class OnCategoryChange(var value: String) : Event()
    data class OnCategoryDropdownClicked(
        val it: Category,
        val onOptionChange: (String?, String?) -> Unit,
        val onExpandedChange: (Boolean) -> Unit,
    ) : Event()

    data class OnBankDropdownClicked(
        val label: String,
        val it: Bank,
        val onExpandedChange: (Boolean) -> Unit
    ) : Event()

    data class OnPartyDropdownClicked(
        val label: String,
        val it: Party,
        val onExpandedChange: (Boolean) -> Unit
    ) : Event()

    data class OnTypeDropdownClicked(
        val it: Type,
        val onExpandedChange: (Boolean) -> Unit,
        val onOptionChange: (String?, String?) -> Unit
    ) : Event()

    data class OnDateChange(val value: String) : Event()
    data class OnSubjectChange(val value: String) : Event()
    data class OnAmountChange(val value: String) : Event()
    data class OnAddNewCategory(val value: String) : Event()
    data class OnDropdownCategoryChange(val type: String, val label: String, val value: String) : Event()
    data object OnExpandedCategory: Event()
    data object OnExpandedType: Event()
    data object OnExpandedFrom: Event()
    data object OnExpandedTo: Event()
}