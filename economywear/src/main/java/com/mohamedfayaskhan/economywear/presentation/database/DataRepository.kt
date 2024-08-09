package com.mohamedfayaskhan.economywear.presentation.database

import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.mohamedfayaskhan.economywear.presentation.constant.Constant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DataRepository(private val database: DatabaseReference?) {
    fun readTransactions(fetcher: DataFetcher<Transaction>) {
        val transactions = mutableListOf<Transaction>()
        database?.child(Constant.TRANSACTION_PATH)?.addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (s in snapshot.children) {
                    val transaction = s.getValue(Transaction::class.java)
                    if (transaction != null) {
                        transactions.add(transaction)
                    }
                }
                fetcher.getDataFromFireBase(transactions)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Firebase Error", error.message)
            }
        })
    }

    fun readParties(fetcher: DataFetcher<Party>) {
        val parties = mutableListOf<Party>()
        database?.child(Constant.PARTY_PATH)?.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (s in snapshot.children) {
                    val party = s.getValue(Party::class.java)
                    if (party != null) {
                        parties.add(party)
                    }
                }
                fetcher.getDataFromFireBase(parties)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Firebase Error", error.message)
            }

        })
    }

    fun readBanks(fetcher: DataFetcher<Bank>) {
        database?.child(Constant.ACCOUNT_PATH)?.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val banks = mutableListOf<Bank>()
                for (s in snapshot.children) {
                    val bank = s.getValue(Bank::class.java)
                    if (bank!= null) {
                        banks.add(bank)
                    }
                }
                fetcher.getDataFromFireBase(banks)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Firebase Error", error.message)
            }

        })
    }

    fun readCategories(fetcher: DataFetcher<Category>) {
        val categories = mutableListOf<Category>()
        database?.child(Constant.CATEGORY_PATH)?.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (s in snapshot.children) {
                    val category = s.getValue(Category::class.java)
                    if (category != null) {
                        categories.add(category)
                    }
                }
                fetcher.getDataFromFireBase(categories)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Firebase Error", error.message)
            }

        })
    }

    fun getUniqueDatabaseId(): String? {
        return database?.push()?.key
    }

    fun getTimestamp(): String {
        val current = Date()
        val formatter = SimpleDateFormat(Constant.TIMESTAMP_FORMAT, Locale.getDefault())
        return formatter.format(current)
    }

    fun addTransaction(
        context: Context,
        transaction: Transaction,
        partiesLiveData: SnapshotStateList<Party>,
        bankLiveData: SnapshotStateList<Bank>,
        isLoan: Boolean,
        isTransactionCompleted: (Boolean) -> Unit
    ) {
        when (transaction.type) {
            Constant.SPENT -> {
                val banks = bankLiveData.filter {
                    transaction.from == it.id
                }
                val bank = banks[0]
                val oldBalance = bank.balance.toDouble()
                val amount = transaction.amount.toDouble()
                val newBalance = oldBalance.minus(amount)
                bank.balance = newBalance.toString()
                upsertAccount(bank)
                upsertTransaction(transaction, context)
                isTransactionCompleted(true)
            }

            Constant.BANK_TO_BANK -> {
                val banks = bankLiveData.filter {
                    transaction.from == it.id || transaction.to == it.id
                }
                val fromBank = banks[0]
                val toBank = banks[1]
                val fromOldBalance = fromBank.balance.toDouble()
                val toOldBalance = toBank.balance.toDouble()
                val fromNewBalance = fromOldBalance.minus(transaction.amount.toDouble())
                val toNewBalance = toOldBalance.plus(transaction.amount.toDouble())
                if (fromNewBalance >= 0) {
                    fromBank.balance = fromNewBalance.toString()
                    toBank.balance = toNewBalance.toString()
                    upsertAccount(fromBank)
                    upsertAccount(toBank)
                    upsertTransaction(transaction, context)
                    isTransactionCompleted(true)

                } else {
                    Toast.makeText(context, "Insufficient balance", Toast.LENGTH_LONG).show()
                    isTransactionCompleted(false)
                }
            }

            Constant.BANK_TO_PARTY -> {
                val bank = bankLiveData.filter {
                    transaction.from == it.id
                }
                val party = partiesLiveData.filter {
                    transaction.to == it.id
                }
                val fromBank = bank[0]
                val toParty = party[1]
                val fromOldBalance = fromBank.balance.toDouble()
                val toOldBalance = toParty.balance.toDouble()
                val amountDouble = transaction.amount.toDouble()
                val fromNewBalance = fromOldBalance.minus(amountDouble)
                if (fromNewBalance >= 0) {
                    if (isLoan) {// kadan kuduthal
                        val toNewBalance = toOldBalance.plus(amountDouble)
                        toParty.balance = toNewBalance.toString()
                        fromBank.balance = fromNewBalance.toString()
                        upsertAccount(fromBank)
                        upsertParty(toParty)
                        upsertTransaction(transaction, context)
                        isTransactionCompleted(true)
                    } else {// kadan adaithal
                        val toNewBalance = toOldBalance.minus(amountDouble)
                        toParty.balance = toNewBalance.toString()
                        fromBank.balance = fromNewBalance.toString()
                        upsertAccount(fromBank)
                        upsertParty(toParty)
                        upsertTransaction(transaction, context)
                        isTransactionCompleted(true)
                    }
                } else {
                    Toast.makeText(context, "Insufficient balance", Toast.LENGTH_LONG).show()
                    isTransactionCompleted(false)
                }
            }

//            Constant.PARTY_TO_PARTY -> {
//                val parties = partiesLiveData.value?.filter {
//                    transaction.from == it.id || transaction.to == it.id
//                }
//                val fromParty = parties?.get(0)
//                val toParty = parties?.get(1)
//                val fromOldBalance = fromParty?.balance?.toDouble()
//                val toOldBalance = toParty?.balance?.toDouble()
//                val amountDouble = transaction.amount.toDouble()
//                val fromNewBalance = fromOldBalance?.minus(amountDouble)
//                val toNewBalance = toOldBalance?.plus(amountDouble)
//                if (fromNewBalance != null && fromNewBalance >= 0) {
//
//                } else {
//                    Toast.makeText(context, "Insufficient balance", Toast.LENGTH_LONG).show()
//                }
//            }

            Constant.PARTY_TO_BANK -> {
                val party = partiesLiveData.filter {
                    transaction.from == it.id
                }
                val bank = bankLiveData.filter {
                    transaction.to == it.id
                }
                val fromParty = party[0]
                val toBank = bank[0]
                val fromOldBalance = fromParty.balance.toDouble()
                val toOldBalance = toBank.balance.toDouble()
                val amountDouble = transaction.amount.toDouble()
                val fromNewBalance = fromOldBalance.minus(amountDouble)
                if (fromNewBalance >= 0){
                    if (isLoan) {
                        val toNewBalance = toOldBalance.plus(amountDouble)
                        toBank.balance = toNewBalance.toString()
                        upsertParty(fromParty)
                        upsertAccount(toBank)
                        upsertTransaction(transaction, context)
                        isTransactionCompleted(true)
                    } else {
                        val fromNewBalance1 = fromOldBalance.plus(amountDouble)
                        val toNewBalance = toOldBalance.plus(amountDouble)
                        fromParty.balance = fromNewBalance1.toString()
                        toBank.balance = toNewBalance.toString()
                        upsertParty(fromParty)
                        upsertAccount(toBank)
                        upsertTransaction(transaction, context)
                        isTransactionCompleted(true)
                    }
                }
            }

            Constant.ADD_BALANCE_TO_BANK -> {
                val bank = bankLiveData.filter {
                    transaction.to == it.id
                }
                val toBank = bank[0]
                val toOldBalance = toBank.balance.toDouble()
                val amountDouble = transaction.amount.toDouble()
                val toNewBalance = toOldBalance.plus(amountDouble)
                toBank.balance = toNewBalance.toString()
                upsertAccount(toBank)
                upsertTransaction(transaction, context)
                isTransactionCompleted(true)
            }

            Constant.REDUCE_BALANCE_FROM_BANK -> {
                val bank = bankLiveData.filter {
                    transaction.from == it.id
                }
                val fromBank = bank[0]
                val fromOldBalance = fromBank.balance.toDouble()
                val amountDouble = transaction.amount.toDouble()
                val toNewBalance = fromOldBalance.minus(amountDouble)
                fromBank.balance = toNewBalance.toString()
                upsertAccount(fromBank)
                upsertTransaction(transaction, context)
                isTransactionCompleted(true)
            }

            Constant.ADD_BALANCE_TO_PARTY -> {
                val party = partiesLiveData.filter {
                    transaction.to == it.id
                }
                val toParty = party[0]
                val toOldBalance = toParty.balance.toDouble()
                val amountDouble = transaction.amount.toDouble()
                val toNewBalance = toOldBalance.plus(amountDouble)
                toParty.balance = toNewBalance.toString()
                upsertParty(toParty)
                upsertTransaction(transaction, context)
                isTransactionCompleted(true)
            }

            Constant.REDUCE_BALANCE_FROM_PARTY -> {
                val party = partiesLiveData.filter {
                    transaction.from == it.id
                }
                val fromParty = party[0]
                val fromOldBalance = fromParty.balance.toDouble()
                val amountDouble = transaction.amount.toDouble()
                val toNewBalance = fromOldBalance.minus(amountDouble)
                fromParty.balance = toNewBalance.toString()
                upsertParty(fromParty)
                upsertTransaction(transaction, context)
                isTransactionCompleted(true)
            }
        }
    }

    fun upsertTransaction(transaction: Transaction, context: Context) {
        database?.child(Constant.TRANSACTION_PATH)?.child(transaction.id)?.setValue(transaction)
        Toast.makeText(context, "Transaction updated", Toast.LENGTH_LONG).show()
    }

    fun upsertAccount(bank: Bank) {
        database?.child(Constant.ACCOUNT_PATH)?.child(bank.id)?.setValue(bank)
    }

    fun upsertParty(party: Party) {
        database?.child(Constant.PARTY_PATH)?.child(party.id)?.setValue(party)
    }

    fun upsertCategory(category: String): String {
        val key = database?.child(Constant.CATEGORY_PATH)?.push()?.key
        database?.child(Constant.CATEGORY_PATH)?.child(key.toString())?.setValue(Category(key.toString(), category))
        return key.toString()
    }

    fun getCategoryLength(text: String): Dp {
        val paint = Paint()
        val textSizeInSp = 16f
        val density = 160 // Assuming mdpi (160dpi) as the baseline density

        // Convert sp to pixels
        val textSizeInPx = textSizeInSp * density / 160
        paint.textSize = textSizeInPx
        // Measure the text width in pixels
        val textWidthInPixels = paint.measureText(text)
        // Convert pixels to dp
        val textWidthInDp = textWidthInPixels / (density.toFloat() / 160)

        return textWidthInDp.dp
    }

    fun updateTransactionDate(id: String, date: String) {
        database?.child(Constant.TRANSACTION_PATH)?.child(id)?.child("date")?.setValue(date)
    }

    fun deleteTransaction(transaction: Transaction) {
        database?.child(Constant.TRANSACTION_PATH)?.child(transaction.id)?.removeValue()
    }
}