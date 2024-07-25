package com.mohamedkhan.economymanagercompose.database

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Bank(
    val id: String,
    var name: String,
    var number: String,
    var balance: String,
    val isActive: Boolean
) {
    constructor() : this(null.toString(), null.toString(), null.toString(), null.toString(), false)
}

data class Party(
    val id: String,
    var name: String,
    val number: String,
    var balance: String,
    val isActive: Boolean,
    var receivable: Boolean
) {
    constructor(): this(null.toString(), null.toString(), null.toString(), null.toString(), false, false)
}

data class Category(
    val id: String,
    val name: String
) {
    constructor(): this(null.toString(), null.toString())
}

data class Type(
    val id: String,
    val name: String
) {
    constructor(): this(null.toString(), null.toString())
}

data class Transaction(
    val id: String,
    val subject: String,
    val amount: String,
    val category: String,
    val date: String,
    val timeStamp: String,
    val type: String,
    val from: String,
    val to: String,
    val income: Boolean
){
    constructor(): this(null.toString(),null.toString(),null.toString(),null.toString(),null.toString(),null.toString(),null.toString(),null.toString(),null.toString(),false)
}

data class ToggleSwitch(
    val text: String,
    var isChecked: Boolean
)

data class CategoryData(
    val name: String,
    val amount: Double,
    val length: Dp
) {
    constructor(): this(null.toString(), 0.0, 0.dp)
}