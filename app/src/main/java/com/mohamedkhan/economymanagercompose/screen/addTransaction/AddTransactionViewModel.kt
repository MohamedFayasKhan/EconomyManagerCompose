package com.mohamedkhan.economymanagercompose.screen.addTransaction

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.mohamedkhan.economymanagercompose.constant.Constant
import com.mohamedkhan.economymanagercompose.database.ToggleSwitch

class AddTransactionViewModel: ViewModel() {
    var date by mutableStateOf(value = "")
        private set
    var subject by mutableStateOf(value = "")
        private set
    var amount by mutableStateOf(value = "")
        private set
    var type by mutableStateOf(value = "")
        private set
    var typeValue by mutableStateOf(value = "")
        private set
    var expandedType by mutableStateOf(false)
        private set
    var category by mutableStateOf(value = "")
        private set
    var categoryValue by mutableStateOf(value = "")
        private set
    var newCategoryValue by mutableStateOf(value = "")
        private set
    var expandedCategory by mutableStateOf(false)
        private set
    var from by mutableStateOf(value = "")
        private set
    var fromValue by mutableStateOf(value = "")
        private set
    var expandedFrom by mutableStateOf(false)
        private set
    var to by mutableStateOf(value = "")
        private set
    var toValue by mutableStateOf(value = "")
        private set
    var expandedTo by mutableStateOf(false)
        private set
    var isTypeUpdated by mutableStateOf(false)
        private set
    var loanSwitch by
        mutableStateOf(
            value = ToggleSwitch("Give Loan", false)
        )
        private set
    var isAddCategory by mutableStateOf(false)
        private set


    fun validate(value: String): Boolean = value != ""

    fun onEvent(event: Event) {
        when (event) {
            is Event.OnAddCategoryChange -> {
                isAddCategory = event.value
            }

            is Event.OnNewCategoryChange -> {
                newCategoryValue = event.value
            }

            is Event.OnTypeChange -> {
                type = event.value
            }

            is Event.OnTypeUpdate -> {
                isTypeUpdated = event.value
            }

            is Event.OnFromChange -> {
                from = event.value
            }
            is Event.OnFromValueChange -> {
                fromValue = event.value
            }
            is Event.OnToChange -> {
                to = event.value
            }
            is Event.OnToValueChange -> {
                toValue = event.value
            }

            is Event.OnLoanSwitchValueChange -> {
                loanSwitch = event.value
            }

            is Event.OnAddEventClick -> {
                if (event.it) {
                    event.navController.popBackStack()
                } else {
                    Toast.makeText(event.context, "Fill all details", Toast.LENGTH_LONG).show()
                }
            }

            is Event.OnCategoryChange -> {
                category = event.value
            }

            is Event.OnCategoryDropdownClicked -> {
                categoryValue = event.it.name
                category = event.it.id
                event.onExpandedChange(false)
                if (event.it.id == Constant.ADD_CATEGORY) {
                    event.onOptionChange(null, category)
                } else {
                    event.onOptionChange(null, null)
                }
            }

            is Event.OnBankDropdownClicked -> {
                if (event.label == "From") {
                    fromValue = event.it.name + " - " + event.it.balance
                    from = event.it.id
                } else {
                    toValue = event.it.name + " - " + event.it.balance
                    to = event.it.id
                }
                event.onExpandedChange(false)
            }

            is Event.OnPartyDropdownClicked -> {
                if (event.label == "From") {
                    fromValue = event.it.name + " - " + event.it.balance
                    from = event.it.id
                } else {
                    toValue = event.it.name + " - " + event.it.balance
                    to = event.it.id
                }
                event.onExpandedChange(false)
            }

            is Event.OnTypeDropdownClicked -> {
                typeValue = event.it.name
                type = event.it.id
                event.onExpandedChange(false)
                event.onOptionChange(type, null)
            }

            is Event.OnDateChange -> {
                date = event.value
            }

            is Event.OnAddNewCategory -> {
                newCategoryValue = event.value
            }

            is Event.OnSubjectChange -> {
                subject = event.value
            }

            is Event.OnAmountChange -> {
                amount = event.value
            }

            is Event.OnDropdownCategoryChange -> {
                when (event.type) {
                    "Category" -> {
                        categoryValue = event.value
                    }
                    "Bank" -> {
                        if (event.label == "From") {
                            fromValue = event.value
                        } else {
                            toValue = event.value
                        }
                    }
                    "Party" -> {
                        if (event.label == "From") {
                            fromValue = event.value
                        } else {
                            toValue = event.value
                        }
                    }
                    "Type" -> {
                        typeValue = event.value
                    }
                }
            }

            Event.OnExpandedCategory -> {
                expandedCategory = !expandedCategory
            }
            Event.OnExpandedFrom -> {
                expandedFrom = !expandedFrom
            }
            Event.OnExpandedTo -> {
                expandedTo = !expandedTo
            }
            Event.OnExpandedType -> {
                expandedType = !expandedType
            }
        }
    }

}
