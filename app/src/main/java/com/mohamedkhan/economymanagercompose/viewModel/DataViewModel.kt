package com.mohamedkhan.economymanagercompose.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mohamedkhan.economymanagercompose.constant.Constant
import com.mohamedkhan.economymanagercompose.database.Bank
import com.mohamedkhan.economymanagercompose.database.Category
import com.mohamedkhan.economymanagercompose.database.CategoryData
import com.mohamedkhan.economymanagercompose.database.DataFetcher
import com.mohamedkhan.economymanagercompose.database.DataRepository
import com.mohamedkhan.economymanagercompose.database.Database
import com.mohamedkhan.economymanagercompose.database.Party
import com.mohamedkhan.economymanagercompose.database.Transaction
import com.mohamedkhan.economymanagercompose.database.Type
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Collections

class DataViewModel(application: Application): AndroidViewModel(application) {

    private lateinit var repository: DataRepository
//    private val _usersLiveData = MutableLiveData<List<User>>()
//    val usersLiveData: LiveData<List<User>> get() = _usersLiveData
    private val _transactionLiveData = MutableLiveData<List<Transaction>>()
    val transactionLiveData: LiveData<List<Transaction>> get() = _transactionLiveData

    private val _partiesLiveData = MutableLiveData<List<Party>>()
    val partiesLiveData: LiveData<List<Party>> get() = _partiesLiveData

    private val _banksLiveData = MutableLiveData<List<Bank>>()
    val bankLiveData: LiveData<List<Bank>> get() = _banksLiveData

    private val _categoryLiveData = MutableLiveData<List<Category>>()
    val categoryLiveData: LiveData<List<Category>> get() = _categoryLiveData

    private val _typeLiveData = MutableLiveData<List<Type>>()
    val typeLiveData: LiveData<List<Type>> get() = _typeLiveData

    private val _incomeLiveData = MutableLiveData<String>()
    val incomeLiveData: LiveData<String> get() = _incomeLiveData
    private val _expenseLiveData = MutableLiveData<String>()
    val expenseLiveData: LiveData<String> get() = _expenseLiveData

    fun initDatabase(uid: String?) {
        val database = uid?.let { Database.getDataBase().child(it).child(Constant.DATAS) }
        repository = DataRepository(database)
        _typeLiveData.value = listOf<Type>(
            Type(Constant.SPENT,Constant.SPENT_VALUE),
            Type(Constant.BANK_TO_BANK,Constant.BANK_TO_BANK_VALUE),
            Type(Constant.BANK_TO_PARTY,Constant.BANK_TO_PARTY_VALUE),
//            Type(Constant.PARTY_TO_PARTY,Constant.PARTY_TO_PARTY_VALUE),
            Type(Constant.PARTY_TO_BANK,Constant.PARTY_TO_BANK_VALUE),
            Type(Constant.ADD_BALANCE_TO_BANK,Constant.ADD_BALANCE_TO_BANK_VALUE),
            Type(Constant.REDUCE_BALANCE_FROM_BANK,Constant.REDUCE_BALANCE_FROM_BANK_VALUE),
            Type(Constant.ADD_BALANCE_TO_PARTY,Constant.ADD_BALANCE_TO_PARTY_VALUE),
            Type(Constant.REDUCE_BALANCE_FROM_PARTY,Constant.REDUCE_BALANCE_FROM_PARTY_VALUE)
        )
    }

    fun performTasks(onComplete: () -> Unit) {
        viewModelScope.launch {
            readCategories()
            readParties()
            readBanks()
            readTransactions()
            onComplete()
        }
    }

    fun readTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            val fetcher = object : DataFetcher<Transaction> {
                override fun getDataFromFireBase(list: List<Transaction>) {
                    Collections.reverse(list)
                    _transactionLiveData.value = list
                    calculateIncome()
                    calculateExpense()
                }

                override fun getSingleData(data: Transaction) {
                    TODO("Not yet implemented")
                }

            }
            repository.readTransactions(fetcher)
        }
    }

    fun readParties() {
        viewModelScope.launch(Dispatchers.IO) {
            val fetcher = object : DataFetcher<Party> {
                override fun getDataFromFireBase(list: List<Party>) {
                    _partiesLiveData.value = list
                }

                override fun getSingleData(data: Party) {
                    TODO("Not yet implemented")
                }
            }
            repository.readParties(fetcher)
        }
    }

    fun readBanks() {
        viewModelScope.launch(Dispatchers.IO) {
            val fetcher = object : DataFetcher<Bank> {
                override fun getDataFromFireBase(list: List<Bank>) {
                    _banksLiveData.value = list
                }

                override fun getSingleData(data: Bank) {
                    TODO("Not yet implemented")
                }

            }
            repository.readBanks(fetcher)
        }
    }

    fun readCategories(){
        viewModelScope.launch(Dispatchers.IO) {
            val fetcher = object : DataFetcher<Category> {
                override fun getDataFromFireBase(list: List<Category>) {
                    _categoryLiveData.value = list
                }

                override fun getSingleData(data: Category) {
                    TODO("Not yet implemented")
                }

            }
            repository.readCategories(fetcher)
        }
    }

    fun getUniqueDatabaseId(): String? {
        return repository.getUniqueDatabaseId()
    }

    fun getTimestamp(): String {
        return repository.getTimestamp()
    }

    fun addTransaction(transaction: Transaction, context: Context, checked: Boolean, isTransactionCompleted: (Boolean) -> Unit) {
        repository.addTransaction(context, transaction, categoryLiveData, partiesLiveData, bankLiveData, checked) {
            isTransactionCompleted(it)
        }
    }

    fun addCategory(category: String): String {
        return repository.upsertCategory(category)
    }

    fun addBank(bank: Bank) {
        repository.upsertAccount(bank)
    }

    fun addParty(party: Party) {
        repository.upsertParty(party)
    }

    fun getChartData(onComplete: (List<CategoryData>) -> Unit) {
        viewModelScope.launch {
            val data = mutableListOf<CategoryData>()
            _categoryLiveData.value?.forEach { category->
                val categoryTransaction = _transactionLiveData.value?.filter {transaction ->
                    category.id == transaction.category
                }
                var amount: Double = 0.0
                categoryTransaction?.forEach {
                    amount += it.amount.toDouble()
                }
                if (!categoryTransaction.isNullOrEmpty()) {
                    data.add(CategoryData(category.name, amount, repository.getCategoryLength(category.name)))
                }
            }
            onComplete(data)
        }
    }

    fun calculateIncome() {
        val incomeData = _transactionLiveData.value?.filter { it.income && it.type != Constant.BANK_TO_BANK }
        var value: Double = 0.0
        if (!incomeData.isNullOrEmpty()) {
            for (data in incomeData) {
                value += data.amount.toDouble()
            }
        }
        _incomeLiveData.value = value.toString()
    }

    fun calculateExpense() {
        val expenseData = _transactionLiveData.value?.filter { !it.income }
        var value: Double = 0.0
        if (!expenseData.isNullOrEmpty()) {
            for (data in expenseData) {
                value += data.amount.toDouble()
            }
        }
        _expenseLiveData.value = value.toString()
    }
}