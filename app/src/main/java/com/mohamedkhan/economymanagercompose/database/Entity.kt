package com.mohamedkhan.economymanagercompose.database

data class Bank(
    val id: String,
    var name: String,
    var number: String,
    var balance: String,
    val active: Boolean
) {
    constructor() : this(null.toString(), null.toString(), null.toString(), null.toString(), false)
}

data class Party(
    val id: String,
    var name: String,
    var number: String,
    var balance: String,
    val active: Boolean,
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
    var subject: String,
    val amount: String,
    val category: String,
    var date: String,
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