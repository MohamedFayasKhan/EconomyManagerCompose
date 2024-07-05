package com.mohamedkhan.economymanagercompose.database

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.mohamedkhan.economymanagercompose.constant.Constant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DataRepository(private val database: DatabaseReference?) {

    fun readTransactions(fetcher: DataFetcher<Transaction>) {
        val transactions = mutableListOf<Transaction>()
        if (database != null) {
            database.child(Constant.TRANSACTION_PATH)
                .addValueEventListener(object : ValueEventListener {
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
                        TODO("Not yet implemented")
                    }
                })
        }
    }

    fun readParties(fetcher: DataFetcher<Party>) {
        val parties = mutableListOf<Party>()
        if (database != null) {
            database.child(Constant.PARTY_PATH).addValueEventListener(object: ValueEventListener{
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
                    TODO("Not yet implemented")
                }

            })
        }
    }

    fun readBanks(fetcher: DataFetcher<Bank>) {
        val banks = mutableListOf<Bank>()
        if (database != null) {
            database.child(Constant.ACCOUNT_PATH).addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (s in snapshot.children) {
                        val bank = s.getValue(Bank::class.java)
                        if (bank!= null) {
                            banks.add(bank)
                        }
                    }
                    fetcher.getDataFromFireBase(banks)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

    fun readCategories(fetcher: DataFetcher<Category>) {
        val categories = mutableListOf<Category>()
        if (database != null) {
            database.child(Constant.CATEGORY_PATH).addValueEventListener(object: ValueEventListener{
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
                    TODO("Not yet implemented")
                }

            })
        }
    }

    fun getUniqueDatabaseId(): String? {
        return database?.push()?.key
    }

    fun getTimestamp(): String {
        val current = Date()
        val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        return formatter.format(current)
    }

    fun addTransaction(
        context: Context,
        transaction: Transaction,
        categoryLiveData: LiveData<List<Category>>,
        partiesLiveData: LiveData<List<Party>>,
        bankLiveData: LiveData<List<Bank>>,
        isLoan: Boolean,
        isTransactionCompleted: (Boolean) -> Unit
    ) {
        when (transaction.type) {
            Constant.SPENT -> {
                val banks = bankLiveData.value?.filter {
                    transaction.from == it.id
                }
                val bank = banks?.get(0)
                val oldBalance = bank?.balance?.toDouble()
                val amount = transaction.amount.toDouble()
                val newBalance = oldBalance?.minus(amount)
                bank?.balance = newBalance.toString()
                if (bank != null) {
                    upsertAccount(bank)
                    upsertTransaction(transaction, context)
                    isTransactionCompleted(true)
                } else {
                    Toast.makeText(context, "Error in updating transaction", Toast.LENGTH_LONG).show()
                    isTransactionCompleted(false)
                }
            }

            Constant.BANK_TO_BANK -> {
                val banks = bankLiveData.value?.filter {
                    transaction.from == it.id || transaction.to == it.id
                }
                val fromBank = banks?.get(0)
                val toBank = banks?.get(1)
                val fromOldBalance = fromBank?.balance?.toDouble()
                val toOldBalance = toBank?.balance?.toDouble()
                val fromNewBalance = fromOldBalance?.minus(transaction.amount.toDouble())
                val toNewBalance = toOldBalance?.plus(transaction.amount.toDouble())
                if (fromNewBalance != null && fromNewBalance >= 0) {
                    fromBank.balance = fromNewBalance.toString()
                    toBank?.balance = toNewBalance.toString()
                    if (fromBank!= null && toBank != null) {
                        upsertAccount(fromBank)
                        upsertAccount(toBank)
                        upsertTransaction(transaction, context)
                        isTransactionCompleted(true)
                    } else {
                        Toast.makeText(context, "Error in updating transaction", Toast.LENGTH_LONG).show()
                        isTransactionCompleted(false)
                    }

                } else {
                    Toast.makeText(context, "Insufficient balance", Toast.LENGTH_LONG).show()
                    isTransactionCompleted(false)
                }
            }

            Constant.BANK_TO_PARTY -> {
                val bank = bankLiveData.value?.filter {
                    transaction.from == it.id
                }
                val party = partiesLiveData.value?.filter {
                    transaction.to == it.id
                }
                val fromBank = bank?.get(0)
                val toParty = party?.get(1)
                val fromOldBalance = fromBank?.balance?.toDouble()
                val toOldBalance = toParty?.balance?.toDouble()
                val amountDouble = transaction.amount.toDouble()
                val fromNewBalance = fromOldBalance?.minus(amountDouble)
                if (fromNewBalance != null && fromNewBalance >= 0) {
                    if (isLoan) {// kadan kuduthal
                        val toNewBalance = toOldBalance?.plus(amountDouble)
                        toParty?.balance = toNewBalance.toString()
                        fromBank.balance = fromNewBalance.toString()
                        if (fromBank != null && toParty != null) {
                            upsertAccount(fromBank)
                            upsertParty(toParty)
                            upsertTransaction(transaction, context)
                            isTransactionCompleted(true)
                        } else {
                            Toast.makeText(context, "Error in updating transaction", Toast.LENGTH_LONG).show()
                            isTransactionCompleted(false)
                        }
                    } else {// kadan adaithal
                        val toNewBalance = toOldBalance?.minus(amountDouble)
                        toParty?.balance = toNewBalance.toString()
                        fromBank.balance = fromNewBalance.toString()
                        if (fromBank != null && toParty != null) {
                            upsertAccount(fromBank)
                            upsertParty(toParty)
                            upsertTransaction(transaction, context)
                            isTransactionCompleted(true)
                        } else {
                            Toast.makeText(context, "Error in updating transaction", Toast.LENGTH_LONG).show()
                            isTransactionCompleted(false)
                        }
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
                val party = partiesLiveData.value?.filter {
                    transaction.from == it.id
                }
                val bank = bankLiveData.value?.filter {
                    transaction.to == it.id
                }
                val fromParty = party?.get(0)
                val toBank = bank?.get(0)
                val fromOldBalance = fromParty?.balance?.toDouble()
                val toOldBalance = toBank?.balance?.toDouble()
                val amountDouble = transaction.amount.toDouble()
                val fromNewBalance = fromOldBalance?.minus(amountDouble)
                if (fromNewBalance != null && fromNewBalance >= 0){
                    if (isLoan) {
                        val toNewBalance = toOldBalance?.plus(amountDouble)
                        toBank?.balance = toNewBalance.toString()
                        if(fromParty != null && toBank != null) {
                            upsertParty(fromParty)
                            upsertAccount(toBank)
                            upsertTransaction(transaction, context)
                            isTransactionCompleted(true)
                        } else {
                            Toast.makeText(context, "Error in updating transaction", Toast.LENGTH_LONG).show()
                            isTransactionCompleted(false)
                        }
                    } else {
                        val fromNewBalance1 = fromOldBalance?.plus(amountDouble)
                        val toNewBalance = toOldBalance?.plus(amountDouble)
                        fromParty?.balance = fromNewBalance1.toString()
                        toBank?.balance = toNewBalance.toString()
                        if(fromParty != null && toBank != null) {
                            upsertParty(fromParty)
                            upsertAccount(toBank)
                            upsertTransaction(transaction, context)
                            isTransactionCompleted(true)
                        } else {
                            Toast.makeText(context, "Error in updating transaction", Toast.LENGTH_LONG).show()
                            isTransactionCompleted(false)
                        }
                    }
                }
            }

            Constant.ADD_BALANCE_TO_BANK -> {
                val bank = bankLiveData?.value?.filter {
                    transaction.to == it.id
                }
                val toBank = bank?.get(0)
                val toOldBalance = toBank?.balance?.toDouble()
                val amountDouble = transaction.amount.toDouble()
                val toNewBalance = toOldBalance?.plus(amountDouble)
                if (toBank != null) {
                    toBank?.balance = toNewBalance.toString()
                    upsertAccount(toBank)
                    upsertTransaction(transaction, context)
                    isTransactionCompleted(true)
                } else {
                    Toast.makeText(context, "Error in updating transaction", Toast.LENGTH_LONG).show()
                    isTransactionCompleted(false)
                }
            }

            Constant.REDUCE_BALANCE_FROM_BANK -> {
                val bank = bankLiveData?.value?.filter {
                    transaction.from == it.id
                }
                val fromBank = bank?.get(0)
                val fromOldBalance = fromBank?.balance?.toDouble()
                val amountDouble = transaction.amount.toDouble()
                val toNewBalance = fromOldBalance?.minus(amountDouble)
                if (fromBank != null) {
                    fromBank?.balance = toNewBalance.toString()
                    upsertAccount(fromBank)
                    upsertTransaction(transaction, context)
                    isTransactionCompleted(true)
                } else {
                    Toast.makeText(context, "Error in updating transaction", Toast.LENGTH_LONG).show()
                    isTransactionCompleted(false)
                }
            }

            Constant.ADD_BALANCE_TO_PARTY -> {
                val party = partiesLiveData?.value?.filter {
                    transaction.to == it.id
                }
                val toParty = party?.get(0)
                val toOldBalance = toParty?.balance?.toDouble()
                val amountDouble = transaction.amount.toDouble()
                val toNewBalance = toOldBalance?.plus(amountDouble)
                if (toParty != null) {
                    toParty?.balance = toNewBalance.toString()
                    upsertParty(toParty)
                    upsertTransaction(transaction, context)
                    isTransactionCompleted(true)
                } else {
                    Toast.makeText(context, "Error in updating transaction", Toast.LENGTH_LONG).show()
                    isTransactionCompleted(false)
                }
            }

            Constant.REDUCE_BALANCE_FROM_PARTY -> {
                val party = partiesLiveData?.value?.filter {
                    transaction.from == it.id
                }
                val fromParty = party?.get(0)
                val fromOldBalance = fromParty?.balance?.toDouble()
                val amountDouble = transaction.amount.toDouble()
                val toNewBalance = fromOldBalance?.minus(amountDouble)
                if (fromParty != null) {
                    fromParty?.balance = toNewBalance.toString()
                    upsertParty(fromParty)
                    upsertTransaction(transaction, context)
                    isTransactionCompleted(true)
                } else {
                    Toast.makeText(context, "Error in updating transaction", Toast.LENGTH_LONG).show()
                    isTransactionCompleted(false)
                }
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
}