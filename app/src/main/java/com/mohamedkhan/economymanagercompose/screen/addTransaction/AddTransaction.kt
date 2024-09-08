package com.mohamedkhan.economymanagercompose.screen.addTransaction

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.mohamedkhan.economymanagercompose.constant.Constant
import com.mohamedkhan.economymanagercompose.database.Bank
import com.mohamedkhan.economymanagercompose.database.Category
import com.mohamedkhan.economymanagercompose.database.Party
import com.mohamedkhan.economymanagercompose.database.ToggleSwitch
import com.mohamedkhan.economymanagercompose.database.Transaction
import com.mohamedkhan.economymanagercompose.database.Type
import com.mohamedkhan.economymanagercompose.viewModel.DataViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun AddTransaction(dataViewModel: DataViewModel, navController: NavHostController) {
    val context = LocalContext.current
    val addViewModel = viewModel<AddTransactionViewModel>()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(10.dp)
        ) {
            TextFieldDate(addViewModel.date, addViewModel)
            TextFieldSubject(addViewModel.subject, addViewModel)
            TextFieldAmount(addViewModel.amount, addViewModel)
            DropDownCategory(
                addViewModel.expandedCategory,
                addViewModel.categoryValue,
                dataViewModel.categories,
                "Category",
                "Category",
                addViewModel,
                onExpandedChange = { addViewModel.onEvent(Event.OnExpandedCategory) }) { _, isAdd ->
                addViewModel.onEvent(Event.OnAddCategoryChange(isAdd != null))
            }

            if (addViewModel.isAddCategory) {
                TextFieldAddCategory(category = addViewModel.newCategoryValue, addViewModel)
            } else {
                addViewModel.onEvent(Event.OnNewCategoryChange(""))
            }

            DropDownCategory(
                addViewModel.expandedType,
                addViewModel.typeValue,
                dataViewModel.types,
                "Type",
                "Type",
                addViewModel,
                onExpandedChange = { addViewModel.onEvent(Event.OnExpandedType) }) { typeVal, _ ->
                if (typeVal != null) {
                    addViewModel.onEvent(Event.OnTypeChange(typeVal))
                    addViewModel.onEvent(Event.OnTypeUpdate(true))
                }
            }

            if (addViewModel.isTypeUpdated) {
                addViewModel.onEvent(Event.OnFromChange(""))
                addViewModel.onEvent(Event.OnToChange(""))
                addViewModel.onEvent(Event.OnFromValueChange(""))
                addViewModel.onEvent(Event.OnToValueChange(""))
                addViewModel.onEvent(Event.OnTypeUpdate(false))
            }

            when (addViewModel.type) {
                Constant.SPENT -> {
                    DropDownCategory(
                        addViewModel.expandedFrom,
                        addViewModel.fromValue,
                        dataViewModel.banks,
                        "Bank",
                        "From",
                        addViewModel,
                        onExpandedChange = { addViewModel.onEvent(Event.OnExpandedFrom) }) { _, _ -> }
                    addViewModel.onEvent(Event.OnToChange(Constant.SPENT))
                }

                Constant.BANK_TO_BANK -> {
                    DropDownCategory(
                        addViewModel.expandedFrom,
                        addViewModel.fromValue,
                        dataViewModel.banks,
                        "Bank",
                        "From",
                        addViewModel,
                        onExpandedChange = { addViewModel.onEvent(Event.OnExpandedFrom) }) { _, _ -> }
                    DropDownCategory(
                        addViewModel.expandedTo,
                        addViewModel.toValue,
                        dataViewModel.banks,
                        "Bank",
                        "To",
                        addViewModel,
                        onExpandedChange = { addViewModel.onEvent(Event.OnExpandedTo) }) { _, _ -> }
                }

                Constant.BANK_TO_PARTY -> {
                    DropDownCategory(
                        addViewModel.expandedFrom,
                        addViewModel.fromValue,
                        dataViewModel.banks,
                        "Bank",
                        "From",
                        addViewModel,
                        onExpandedChange = { addViewModel.onEvent(Event.OnExpandedFrom) }) { _, _ -> }
                    DropDownCategory(
                        addViewModel.expandedTo,
                        addViewModel.toValue,
                        dataViewModel.parties,
                        "Party",
                        "To",
                        addViewModel,
                        onExpandedChange = { addViewModel.onEvent(Event.OnExpandedTo) }) { _, _ -> }
                }

//                Constant.PARTY_TO_PARTY -> {
//                    DropDownCategory(
//                        expandedFrom,
//                        from,
//                        fromValue,
//                        viewModel.parties,
//                        "Party",
//                        "From",
//                        { expandedFrom = !expandedFrom }) {typeVal, isAdd ->}
//                    DropDownCategory(
//                        expandedTo,
//                        to,
//                        toValue,
//                        viewModel.parties,
//                        "Party",
//                        "To",
//                        { expandedTo = !expandedTo }) {typeVal, isAdd ->}
//                }

                Constant.PARTY_TO_BANK -> {
                    DropDownCategory(
                        addViewModel.expandedFrom,
                        addViewModel.fromValue,
                        dataViewModel.parties,
                        "Party",
                        "From",
                        addViewModel,
                        onExpandedChange = { addViewModel.onEvent(Event.OnExpandedFrom) }) { _, _ -> }
                    DropDownCategory(
                        addViewModel.expandedTo,
                        addViewModel.toValue,
                        dataViewModel.banks,
                        "Bank",
                        "To",
                        addViewModel,
                        onExpandedChange = { addViewModel.onEvent(Event.OnExpandedTo) }) { _, _ -> }
                }

                Constant.ADD_BALANCE_TO_BANK -> {
                    DropDownCategory(
                        addViewModel.expandedTo,
                        addViewModel.toValue,
                        dataViewModel.banks,
                        "Bank",
                        "To",
                        addViewModel,
                        onExpandedChange = { addViewModel.onEvent(Event.OnExpandedTo) }) { _, _ -> }
                    addViewModel.onEvent(Event.OnFromChange(Constant.ADJUSTMENT))
                }

                Constant.REDUCE_BALANCE_FROM_BANK -> {
                    DropDownCategory(
                        addViewModel.expandedFrom,
                        addViewModel.fromValue,
                        dataViewModel.banks,
                        "Bank",
                        "From",
                        addViewModel,
                        onExpandedChange = { addViewModel.onEvent(Event.OnExpandedFrom) }) { _, _ -> }
                    addViewModel.onEvent(Event.OnToChange(Constant.ADJUSTMENT))
                }

                Constant.ADD_BALANCE_TO_PARTY -> {
                    DropDownCategory(
                        addViewModel.expandedTo,
                        addViewModel.toValue,
                        dataViewModel.parties,
                        "Party",
                        "To",
                        addViewModel,
                        onExpandedChange = { addViewModel.onEvent(Event.OnExpandedTo) }) { _, _ -> }
                    addViewModel.onEvent(Event.OnFromChange(Constant.ADJUSTMENT))
                }

                Constant.REDUCE_BALANCE_FROM_PARTY -> {
                    DropDownCategory(
                        addViewModel.expandedFrom,
                        addViewModel.fromValue,
                        dataViewModel.parties,
                        "Party",
                        "From",
                        addViewModel,
                        onExpandedChange = { addViewModel.onEvent(Event.OnExpandedFrom) }) { _, _ -> }
                    addViewModel.onEvent(Event.OnToChange(Constant.ADJUSTMENT))
                }
            }

//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Text(text = loanSwitch.text)
//                Spacer(modifier = Modifier.weight(1f))
//                Switch(
//                    checked = loanSwitch.isChecked,
//                    onCheckedChange = {isChecked->
//                        loanSwitch = loanSwitch.copy(
//                            isChecked = isChecked
//                        )
//                    }
//                )
//            }
            LoanSwitch(loanSwitch = addViewModel.loanSwitch) {
                addViewModel.onEvent(Event.OnLoanSwitchValueChange(it))
            }

            AddButton(
                addViewModel,
                addViewModel.date,
                addViewModel.subject,
                addViewModel.amount,
                addViewModel.category,
                addViewModel.type,
                addViewModel.from,
                addViewModel.to,
                dataViewModel,
                addViewModel.loanSwitch,
                addViewModel.newCategoryValue
            ) {
                addViewModel.onEvent(Event.OnAddEventClick(it, navController, context))
            }
//            DropDownCategory(isTypeUpdated, expandedFrom, from, fromValue, viewModel.banks, "Bank", "From", onExpandedChange = { expandedFrom = !expandedFrom })
//            DropDownCategory(expandedTo, to, toValue, viewModel.parties, "Party", "To", onExpandedChange = { expandedTo = !expandedTo })
        }
    }
}

@Composable
fun LoanSwitch(loanSwitch: ToggleSwitch, onSwitchChanged: (ToggleSwitch) -> Unit) {
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

@Composable
fun AddButton(
    addViewModel: AddTransactionViewModel,
    date: String,
    subject: String,
    amount: String,
    category: String,
    type: String,
    from: String,
    to: String,
    viewModel: DataViewModel,
    loanSwitch: ToggleSwitch,
    newCategory: String,
    isTransactionCompleted: (Boolean) -> Unit
) {
    val context = LocalContext.current
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            if (addViewModel.validate(date) && addViewModel.validate(subject) && addViewModel.validate(amount) && addViewModel.validate(category) && addViewModel.validate(type) && addViewModel.validate(from) && addViewModel.validate(to)) {
//                if (newCategory != "") {
//                    addViewModel.onEvent(Event.OnCategoryChange(viewModel.addCategory(newCategory)))
//                }
                var isCompleted = false
                when (type) {
                    Constant.SPENT -> {
                        val transaction = Transaction(
                            viewModel.getUniqueDatabaseId().toString(),
                            subject,
                            amount.toDouble(),
                            if (newCategory != "") {
                                viewModel.addCategory(newCategory)
                            } else {
                                category
                            },
                            viewModel.dateToEpoch(date),
                            viewModel.timeStampToEpoch(),
                            type,
                            from,
                            to,
                            false
                        )
                        viewModel.addTransaction(transaction, context, loanSwitch.isChecked) {
                            isCompleted = it
                        }
                    }

                    Constant.BANK_TO_BANK -> {
                        val transaction = Transaction(
                            viewModel.getUniqueDatabaseId().toString(),
                            subject,
                            amount.toDouble(),
                            if (newCategory != "") {
                                viewModel.addCategory(newCategory)
                            } else {
                                category
                            },
                            viewModel.dateToEpoch(date),
                            viewModel.timeStampToEpoch(),
                            type,
                            from,
                            to,
                            false
                        )
                        viewModel.addTransaction(transaction, context, loanSwitch.isChecked) {
                            isCompleted = it
                        }
                    }

                    Constant.ADD_BALANCE_TO_BANK -> {
                        val transaction = Transaction(
                            viewModel.getUniqueDatabaseId().toString(),
                            subject,
                            amount.toDouble(),
                            if (newCategory != "") {
                                viewModel.addCategory(newCategory)
                            } else {
                                category
                            },
                            viewModel.dateToEpoch(date),
                            viewModel.timeStampToEpoch(),
                            type,
                            from,
                            to,
                            true
                        )
                        viewModel.addTransaction(transaction, context, loanSwitch.isChecked) {
                            isCompleted = it
                        }
                    }

                    Constant.REDUCE_BALANCE_FROM_BANK -> {
                        val transaction = Transaction(
                            viewModel.getUniqueDatabaseId().toString(),
                            subject,
                            amount.toDouble(),
                            if (newCategory != "") {
                                viewModel.addCategory(newCategory)
                            } else {
                                category
                            },
                            viewModel.dateToEpoch(date),
                            viewModel.timeStampToEpoch(),
                            type,
                            from,
                            to,
                            false
                        )
                        viewModel.addTransaction(transaction, context, loanSwitch.isChecked) {
                            isCompleted = it
                        }
                    }

                    Constant.BANK_TO_PARTY -> {
                        val transaction = Transaction(
                            viewModel.getUniqueDatabaseId().toString(),
                            subject,
                            amount.toDouble(),
                            if (newCategory != "") {
                                viewModel.addCategory(newCategory)
                            } else {
                                category
                            },
                            viewModel.dateToEpoch(date),
                            viewModel.timeStampToEpoch(),
                            type,
                            from,
                            to,
                            false
                        )
                        viewModel.addTransaction(transaction, context, loanSwitch.isChecked) {
                            isCompleted = it
                        }
                    }

//            Constant.PARTY_TO_PARTY -> {
//
//            }

                    Constant.PARTY_TO_BANK -> {
                        val transaction = Transaction(
                            viewModel.getUniqueDatabaseId().toString(),
                            subject,
                            amount.toDouble(),
                            if (newCategory != "") {
                                viewModel.addCategory(newCategory)
                            } else {
                                category
                            },
                            viewModel.dateToEpoch(date),
                            viewModel.timeStampToEpoch(),
                            type,
                            from,
                            to,
                            true
                        )
                        viewModel.addTransaction(transaction, context, loanSwitch.isChecked) {
                            isCompleted = it
                        }
                    }

                    Constant.ADD_BALANCE_TO_PARTY -> {
                        val transaction = Transaction(
                            viewModel.getUniqueDatabaseId().toString(),
                            subject,
                            amount.toDouble(),
                            if (newCategory != "") {
                                viewModel.addCategory(newCategory)
                            } else {
                                category
                            },
                            viewModel.dateToEpoch(date),
                            viewModel.timeStampToEpoch(),
                            type,
                            from,
                            to,
                            loanSwitch.isChecked
                        )
                        viewModel.addTransaction(transaction, context, loanSwitch.isChecked) {
                            isCompleted = it
                        }
                    }

                    Constant.REDUCE_BALANCE_FROM_PARTY -> {
                        val transaction = Transaction(
                            viewModel.getUniqueDatabaseId().toString(),
                            subject,
                            amount.toDouble(),
                            if (newCategory != "") {
                                viewModel.addCategory(newCategory)
                            } else {
                                category
                            },
                            viewModel.dateToEpoch(date),
                            viewModel.timeStampToEpoch(),
                            type,
                            from,
                            to,
                            !loanSwitch.isChecked
                        )
                        viewModel.addTransaction(transaction, context, loanSwitch.isChecked) {
                            isCompleted = it
                        }
                    }
                }
                isTransactionCompleted(isCompleted)
            } else {
                isTransactionCompleted(false)
            }
        }) {
        Text(text = "Add Transaction")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> DropDownCategory(
    expanded: Boolean,
    value: String,
    liveData: SnapshotStateList<T>,
    type: String,
    label: String,
    addViewModel: AddTransactionViewModel,
    onExpandedChange: (Boolean) -> Unit,
    onOptionChange: (String?, String?) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            value = value,
            onValueChange = {
                addViewModel.onEvent(Event.OnDropdownCategoryChange(type, label,  it))
            },
            readOnly = true,
            label = {
                Text(text = label)
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier.fillMaxWidth(),

            ) {
            when (type) {
                "Category" -> {
                    mergeAddCategory((liveData as SnapshotStateList<Category>)).forEach {
                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            text = { Text(text = it.name) },
                            onClick = {
                                addViewModel.onEvent(Event.OnCategoryDropdownClicked(it = it, onOptionChange = onOptionChange, onExpandedChange = onExpandedChange))
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
                "Bank" -> {
                    (liveData as SnapshotStateList<Bank>).forEach {
                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            text = { Text(text = it.name) },
                            onClick = {
                                addViewModel.onEvent(Event.OnBankDropdownClicked(label = label, it = it, onExpandedChange = onExpandedChange))
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
                "Party" -> {
                    (liveData as SnapshotStateList<Party>).forEach {
                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            text = { Text(text = it.name) },
                            onClick = {
                                addViewModel.onEvent(Event.OnPartyDropdownClicked(label = label, it = it, onExpandedChange = onExpandedChange))
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
                "Type" -> {
                    (liveData as SnapshotStateList<Type>).forEach {
                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            text = { Text(text = it.name) },
                            onClick = {
                                addViewModel.onEvent(Event.OnTypeDropdownClicked(it = it, onExpandedChange = onExpandedChange, onOptionChange = onOptionChange))
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TextFieldAmount(amount: String, addViewModel: AddTransactionViewModel) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = amount,
        onValueChange = {
            addViewModel.onEvent(Event.OnAmountChange(it))
        },
        label = {
            Text(text = "Amount")
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.Clear,
                contentDescription = "Clear",
                modifier = Modifier.clickable {
                    addViewModel.onEvent(Event.OnAmountChange(""))
                }
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
    )
}

@Composable
private fun TextFieldSubject(subject: String, addViewModel: AddTransactionViewModel) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = subject,
        onValueChange = {
            addViewModel.onEvent(Event.OnSubjectChange(it))
        },
        label = {
            Text(text = "Subject")
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.Clear,
                contentDescription = "Clear",
                modifier = Modifier.clickable {
                    addViewModel.onEvent(Event.OnSubjectChange(""))
                }
            )
        }
    )
}

@Composable
fun TextFieldAddCategory(category: String, addViewModel: AddTransactionViewModel) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = category,
        onValueChange = {
            addViewModel.onEvent(Event.OnNewCategoryChange(it))
        },
        label = {
            Text(text = "Add Category")
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.Clear,
                contentDescription = "Clear",
                modifier = Modifier.clickable {
                    addViewModel.onEvent(Event.OnNewCategoryChange(""))
                }
            )
        }
    )
}

@Composable
fun TextFieldDate(date: String, addViewModel: AddTransactionViewModel) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat(Constant.DATE_FORMAT, Locale.getDefault())
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            addViewModel.onEvent(Event.OnDateChange(dateFormat.format(calendar.time)))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = date,
        onValueChange = {
            addViewModel.onEvent(Event.OnDateChange(it))
        },
        label = {
            Text(text = "Date")
        },
        readOnly = true,
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = "Calendar",
                modifier = Modifier.clickable {
                    datePickerDialog.show()
                }
            )
        }
    )
}

fun mergeAddCategory(liveCategories: List<Category>?): List<Category> {
    val categories = mutableListOf<Category>()
    val addTransaction = Category(Constant.ADD_CATEGORY, Constant.ADD_CATEGORY_VALUE)
    if (liveCategories != null) {
        categories.add(addTransaction)
        categories.addAll(liveCategories)
    } else {
        categories.add(addTransaction)
    }
    return categories
}