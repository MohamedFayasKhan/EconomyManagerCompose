package com.mohamedkhan.economymanagercompose.database

data class Bank(
    val id: String,
    val name: String,
    val number: String,
    val balance: String,
    val isActive: Boolean
) {
    constructor() : this(null.toString(), null.toString(), null.toString(), null.toString(), false)
}

data class Party(
    val id: String,
    val name: String,
    val number: String,
    val balance: String,
    val isActive: Boolean,
    val receivable: Boolean
) {
    constructor(): this(null.toString(), null.toString(), null.toString(), null.toString(), false, false)
}

data class Category(
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