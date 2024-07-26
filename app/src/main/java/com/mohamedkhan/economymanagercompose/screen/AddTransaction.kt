package com.mohamedkhan.economymanagercompose.screen

import android.app.DatePickerDialog
import android.widget.Toast
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
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
fun AddTransaction(viewModel: DataViewModel, navController: NavHostController) {
    val date = remember { mutableStateOf(value = "") }
    val subject = remember { mutableStateOf(value = "") }
    val amount = remember { mutableStateOf(value = "") }
    val type = remember { mutableStateOf(value = "") }
    val typeValue = remember { mutableStateOf(value = "") }
    var expandedType by remember { mutableStateOf(false) }
    val category = remember { mutableStateOf(value = "") }
    val categoryValue = remember { mutableStateOf(value = "") }
    val newCategoryValue = remember { mutableStateOf(value = "") }
    var expandedCategory by remember { mutableStateOf(false) }
    val from = remember { mutableStateOf(value = "") }
    val fromValue = remember { mutableStateOf(value = "") }
    var expandedFrom by remember { mutableStateOf(false) }
    val to = remember { mutableStateOf(value = "") }
    val toValue = remember { mutableStateOf(value = "") }
    var expandedTo by remember { mutableStateOf(false) }
    var isTypeUpdated = remember { mutableStateOf(false) }
    var loanSwitch by remember {
        mutableStateOf(
            value = ToggleSwitch("Give Loan", false)
        )
    }
    var isAddCategory = remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(10.dp)
        ) {
            TextFieldDate(date)
            TextFieldSubject(subject)
            TextFieldAmount(amount)
            DropDownCategory(
                expandedCategory,
                category,
                categoryValue,
                viewModel.categoryLiveData,
                "Category",
                "Category",
                onExpandedChange = { expandedCategory = !expandedCategory }) { typeVal, isAdd ->
                isAddCategory.value = isAdd != null
            }

            if (isAddCategory.value) {
                TextFieldAddCategory(category = newCategoryValue)
            } else {
                newCategoryValue.value = ""
            }

            DropDownCategory(
                expandedType,
                type,
                typeValue,
                viewModel.typeLiveData,
                "Type",
                "Type",
                onExpandedChange = { expandedType = !expandedType }) { typeVal, isAdd ->
                if (typeVal != null) {
                    type.value = typeVal
                    isTypeUpdated.value = true
                }
            }

            if (isTypeUpdated.value) {
                from.value = ""
                to.value = ""
                fromValue.value = ""
                toValue.value = ""
                isTypeUpdated.value = false
            }

            when (type.value) {
                Constant.SPENT -> {
                    DropDownCategory(
                        expandedFrom,
                        from,
                        fromValue,
                        viewModel.bankLiveData,
                        "Bank",
                        "From",
                        { expandedFrom = !expandedFrom }) { typeVal, isAdd -> }
                    to.value = Constant.SPENT
                }

                Constant.BANK_TO_BANK -> {
                    DropDownCategory(
                        expandedFrom,
                        from,
                        fromValue,
                        viewModel.bankLiveData,
                        "Bank",
                        "From",
                        { expandedFrom = !expandedFrom }) { typeVal, isAdd -> }
                    DropDownCategory(
                        expandedTo,
                        to,
                        toValue,
                        viewModel.bankLiveData,
                        "Bank",
                        "To",
                        { expandedTo = !expandedTo }) { typeVal, isAdd -> }
                }

                Constant.BANK_TO_PARTY -> {
                    DropDownCategory(
                        expandedFrom,
                        from,
                        fromValue,
                        viewModel.bankLiveData,
                        "Bank",
                        "From",
                        { expandedFrom = !expandedFrom }) { typeVal, isAdd -> }
                    DropDownCategory(
                        expandedTo,
                        to,
                        toValue,
                        viewModel.partiesLiveData,
                        "Party",
                        "To",
                        { expandedTo = !expandedTo }) { typeVal, isAdd -> }
                }

//                Constant.PARTY_TO_PARTY -> {
//                    DropDownCategory(
//                        expandedFrom,
//                        from,
//                        fromValue,
//                        viewModel.partiesLiveData,
//                        "Party",
//                        "From",
//                        { expandedFrom = !expandedFrom }) {typeVal, isAdd ->}
//                    DropDownCategory(
//                        expandedTo,
//                        to,
//                        toValue,
//                        viewModel.partiesLiveData,
//                        "Party",
//                        "To",
//                        { expandedTo = !expandedTo }) {typeVal, isAdd ->}
//                }

                Constant.PARTY_TO_BANK -> {
                    DropDownCategory(
                        expandedFrom,
                        from,
                        fromValue,
                        viewModel.partiesLiveData,
                        "Party",
                        "From",
                        { expandedFrom = !expandedFrom }) { typeVal, isAdd -> }
                    DropDownCategory(
                        expandedTo,
                        to,
                        toValue,
                        viewModel.bankLiveData,
                        "Bank",
                        "To",
                        { expandedTo = !expandedTo }) { typeVal, isAdd -> }
                }

                Constant.ADD_BALANCE_TO_BANK -> {
                    DropDownCategory(
                        expandedTo,
                        to,
                        toValue,
                        viewModel.bankLiveData,
                        "Bank",
                        "To",
                        { expandedTo = !expandedTo }) { typeVal, isAdd -> }
                    from.value = Constant.ADJUSTMENT
                }

                Constant.REDUCE_BALANCE_FROM_BANK -> {
                    DropDownCategory(
                        expandedFrom,
                        from,
                        fromValue,
                        viewModel.bankLiveData,
                        "Bank",
                        "From",
                        { expandedFrom = !expandedFrom }) { typeVal, isAdd -> }
                    to.value = Constant.ADJUSTMENT
                }

                Constant.ADD_BALANCE_TO_PARTY -> {
                    DropDownCategory(
                        expandedTo,
                        to,
                        toValue,
                        viewModel.partiesLiveData,
                        "Party",
                        "To",
                        { expandedTo = !expandedTo }) { typeVal, isAdd -> }
                    from.value = Constant.ADJUSTMENT
                }

                Constant.REDUCE_BALANCE_FROM_PARTY -> {
                    DropDownCategory(
                        expandedFrom,
                        from,
                        fromValue,
                        viewModel.partiesLiveData,
                        "Party",
                        "From",
                        { expandedFrom = !expandedFrom }) { typeVal, isAdd -> }
                    to.value = Constant.ADJUSTMENT
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
            LoanSwitch(loanSwitch = loanSwitch) {
                loanSwitch = it
            }

            AddButton(
                date,
                subject,
                amount,
                category,
                type,
                from,
                to,
                viewModel,
                loanSwitch,
                newCategoryValue.value
            ) {
                if (it) {
                    navController.popBackStack()
                } else {
                    Toast.makeText(context, "Fill all details", Toast.LENGTH_LONG).show()
                }
            }
//            DropDownCategory(isTypeUpdated, expandedFrom, from, fromValue, viewModel.bankLiveData, "Bank", "From", onExpandedChange = { expandedFrom = !expandedFrom })
//            DropDownCategory(expandedTo, to, toValue, viewModel.partiesLiveData, "Party", "To", onExpandedChange = { expandedTo = !expandedTo })
        }
    }
}

private fun validate(value: MutableState<String>): Boolean = value.value != ""

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
    date: MutableState<String>,
    subject: MutableState<String>,
    amount: MutableState<String>,
    category: MutableState<String>,
    type: MutableState<String>,
    from: MutableState<String>,
    to: MutableState<String>,
    viewModel: DataViewModel,
    loanSwitch: ToggleSwitch,
    newCategory: String,
    isTransactionCompleted: (Boolean) -> Unit
) {
    val context = LocalContext.current
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            if (validate(date) && validate(subject) && validate(amount) && validate(category) && validate(type) && validate(from) && validate(to)) {
                if (newCategory != "") {
                    category.value = viewModel.addCategory(newCategory)
                }
                var isCompleted = false
                when (type.value) {
                    Constant.SPENT -> {
                        val transaction = Transaction(
                            viewModel.getUniqueDatabaseId().toString(),
                            subject.value,
                            amount.value,
                            category.value,
                            date.value,
                            viewModel.getTimestamp(),
                            type.value,
                            from.value,
                            to.value,
                            false
                        )
                        viewModel.addTransaction(transaction, context, loanSwitch.isChecked) {
                            isCompleted = it
                        }
                    }

                    Constant.BANK_TO_BANK -> {
                        val transaction = Transaction(
                            viewModel.getUniqueDatabaseId().toString(),
                            subject.value,
                            amount.value,
                            category.value,
                            date.value,
                            viewModel.getTimestamp(),
                            type.value,
                            from.value,
                            to.value,
                            false
                        )
                        viewModel.addTransaction(transaction, context, loanSwitch.isChecked) {
                            isCompleted = it
                        }
                    }

                    Constant.ADD_BALANCE_TO_BANK -> {
                        val transaction = Transaction(
                            viewModel.getUniqueDatabaseId().toString(),
                            subject.value,
                            amount.value,
                            category.value,
                            date.value,
                            viewModel.getTimestamp(),
                            type.value,
                            from.value,
                            to.value,
                            true
                        )
                        viewModel.addTransaction(transaction, context, loanSwitch.isChecked) {
                            isCompleted = it
                        }
                    }

                    Constant.REDUCE_BALANCE_FROM_BANK -> {
                        val transaction = Transaction(
                            viewModel.getUniqueDatabaseId().toString(),
                            subject.value,
                            amount.value,
                            category.value,
                            date.value,
                            viewModel.getTimestamp(),
                            type.value,
                            from.value,
                            to.value,
                            false
                        )
                        viewModel.addTransaction(transaction, context, loanSwitch.isChecked) {
                            isCompleted = it
                        }
                    }

                    Constant.BANK_TO_PARTY -> {
                        val transaction = Transaction(
                            viewModel.getUniqueDatabaseId().toString(),
                            subject.value,
                            amount.value,
                            category.value,
                            date.value,
                            viewModel.getTimestamp(),
                            type.value,
                            from.value,
                            to.value,
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
                            subject.value,
                            amount.value,
                            category.value,
                            date.value,
                            viewModel.getTimestamp(),
                            type.value,
                            from.value,
                            to.value,
                            true
                        )
                        viewModel.addTransaction(transaction, context, loanSwitch.isChecked) {
                            isCompleted = it
                        }
                    }

                    Constant.ADD_BALANCE_TO_PARTY -> {
                        val transaction = Transaction(
                            viewModel.getUniqueDatabaseId().toString(),
                            subject.value,
                            amount.value,
                            category.value,
                            date.value,
                            viewModel.getTimestamp(),
                            type.value,
                            from.value,
                            to.value,
                            loanSwitch.isChecked
                        )
                        viewModel.addTransaction(transaction, context, loanSwitch.isChecked) {
                            isCompleted = it
                        }
                    }

                    Constant.REDUCE_BALANCE_FROM_PARTY -> {
                        val transaction = Transaction(
                            viewModel.getUniqueDatabaseId().toString(),
                            subject.value,
                            amount.value,
                            category.value,
                            date.value,
                            viewModel.getTimestamp(),
                            type.value,
                            from.value,
                            to.value,
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
    id: MutableState<String>,
    value: MutableState<String>,
    liveData: SnapshotStateList<T>,
    type: String,
    label: String,
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
            value = value.value,
            onValueChange = {
                value.value = it
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
            if (type == "Category") {
                mergeAddCategory((liveData as SnapshotStateList<Category>))?.forEach {
                    DropdownMenuItem(
                        modifier = Modifier.fillMaxWidth(),
                        text = { Text(text = it.name) },
                        onClick = {
                            onExpandedChange(false)
                            value.value = it.name
                            id.value = it.id
                            if (it.id == Constant.ADD_CATEGORY) {
                                onOptionChange(null, id.value)
                            } else {
                                onOptionChange(null, null)
                            }
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            } else if (type == "Bank") {
                (liveData as SnapshotStateList<Bank>)?.forEach {
                    DropdownMenuItem(
                        modifier = Modifier.fillMaxWidth(),
                        text = { Text(text = it.name) },
                        onClick = {
                            onExpandedChange(false)
                            value.value = it.name + " - " + it.balance
                            id.value = it.id

                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            } else if (type == "Party") {
                (liveData as SnapshotStateList<Party>)?.forEach {
                    DropdownMenuItem(
                        modifier = Modifier.fillMaxWidth(),
                        text = { Text(text = it.name) },
                        onClick = {
                            onExpandedChange(false)
                            value.value = it.name + " - " + it.balance
                            id.value = it.id

                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            } else if (type == "Type") {
                (liveData as SnapshotStateList<Type>)?.forEach {
                    DropdownMenuItem(
                        modifier = Modifier.fillMaxWidth(),
                        text = { Text(text = it.name) },
                        onClick = {
                            onExpandedChange(false)
                            value.value = it.name
                            id.value = it.id
                            onOptionChange(it.id, null)
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
}

@Composable
fun TextFieldAmount(amount: MutableState<String>) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = amount.value,
        onValueChange = {
            amount.value = it
        },
        label = {
            Text(text = "Amount")
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.Clear,
                contentDescription = "Clear",
                modifier = Modifier.clickable {
                    amount.value = ""
                }
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
    )
}

@Composable
fun TextFieldSubject(subject: MutableState<String>) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = subject.value,
        onValueChange = {
            subject.value = it
        },
        label = {
            Text(text = "Subject")
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.Clear,
                contentDescription = "Clear",
                modifier = Modifier.clickable {
                    subject.value = ""
                }
            )
        }
    )
}

@Composable
fun TextFieldAddCategory(category: MutableState<String>) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = category.value,
        onValueChange = {
            category.value = it
        },
        label = {
            Text(text = "Add Category")
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.Clear,
                contentDescription = "Clear",
                modifier = Modifier.clickable {
                    category.value = ""
                }
            )
        }
    )
}

@Composable
fun TextFieldDate(date: MutableState<String>) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat(Constant.DATE_FORMAT, Locale.getDefault())
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            date.value = dateFormat.format(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = date.value,
        onValueChange = {
            date.value = it
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