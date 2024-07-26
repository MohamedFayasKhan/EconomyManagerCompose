package com.mohamedkhan.economymanagercompose.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mohamedkhan.economymanagercompose.constant.Constant
import com.mohamedkhan.economymanagercompose.database.Bank
import com.mohamedkhan.economymanagercompose.database.Category
import com.mohamedkhan.economymanagercompose.database.DataFetcher
import com.mohamedkhan.economymanagercompose.database.DataRepository
import com.mohamedkhan.economymanagercompose.database.Database
import com.mohamedkhan.economymanagercompose.database.Party
import com.mohamedkhan.economymanagercompose.database.Transaction
import com.mohamedkhan.economymanagercompose.database.Type
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DataViewModel(application: Application): AndroidViewModel(application) {

    private lateinit var repository: DataRepository
    private val _transactionLiveData = mutableStateListOf<Transaction>()
    val transactionLiveData: SnapshotStateList<Transaction> = _transactionLiveData

    private val _partiesLiveData = mutableStateListOf<Party>()
    val partiesLiveData: SnapshotStateList<Party> = _partiesLiveData

    private val _banksLiveData = mutableStateListOf<Bank>()
    val bankLiveData: SnapshotStateList<Bank> = _banksLiveData

    private val _categoryLiveData = mutableStateListOf<Category>()
    val categoryLiveData: SnapshotStateList<Category> = _categoryLiveData

    private var _typeLiveData = mutableStateListOf<Type>()
    val typeLiveData: SnapshotStateList<Type> = _typeLiveData

    private val _incomeLiveData = MutableLiveData<String>()
    val incomeLiveData: LiveData<String> get() = _incomeLiveData
    private val _expenseLiveData = MutableLiveData<String>()
    val expenseLiveData: LiveData<String> get() = _expenseLiveData
    private val _totalLiveData = MutableLiveData<String>()
    val totalLiveData: LiveData<String> get() = _totalLiveData
    private val _durationCategoryLiveData = MutableLiveData<List<Pair<String, Double>>>()
    val durationCategoryLiveData: LiveData<List<Pair<String, Double>>> get() = _durationCategoryLiveData

    fun initDatabase(uid: String?) {
        val database = uid?.let { Database.getDataBase().child(it).child(Constant.DATAS) }
        repository = DataRepository(database)
        _typeLiveData.addAll(listOf<Type>(
            Type(Constant.SPENT,Constant.SPENT_VALUE),
            Type(Constant.BANK_TO_BANK,Constant.BANK_TO_BANK_VALUE),
            Type(Constant.BANK_TO_PARTY,Constant.BANK_TO_PARTY_VALUE),
            //            Type(Constant.PARTY_TO_PARTY,Constant.PARTY_TO_PARTY_VALUE),
            Type(Constant.PARTY_TO_BANK,Constant.PARTY_TO_BANK_VALUE),
            Type(Constant.ADD_BALANCE_TO_BANK,Constant.ADD_BALANCE_TO_BANK_VALUE),
            Type(Constant.REDUCE_BALANCE_FROM_BANK,Constant.REDUCE_BALANCE_FROM_BANK_VALUE),
            Type(Constant.ADD_BALANCE_TO_PARTY,Constant.ADD_BALANCE_TO_PARTY_VALUE),
            Type(Constant.REDUCE_BALANCE_FROM_PARTY,Constant.REDUCE_BALANCE_FROM_PARTY_VALUE)
        ))
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

    private fun readTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            val fetcher = object : DataFetcher<Transaction> {
                override fun getDataFromFireBase(list: List<Transaction>) {
                    _transactionLiveData.clear()
                    _transactionLiveData.addAll(list.distinct().reversed())
                    viewModelScope.launch {
                        calculateIncome()
                        calculateExpense()
                        getChartData("Last 7 Days")
                    }
                }

                override fun getSingleData(data: Transaction) {}

            }
            repository.readTransactions(fetcher)
        }
    }

    private fun readParties() {
        viewModelScope.launch(Dispatchers.IO) {
            val fetcher = object : DataFetcher<Party> {
                override fun getDataFromFireBase(list: List<Party>) {
                    _partiesLiveData.clear()
                    partiesLiveData.addAll(list.distinct())
                }

                override fun getSingleData(data: Party) {

                }
            }
            repository.readParties(fetcher)
        }
    }

    private fun readBanks() {
        viewModelScope.launch(Dispatchers.IO) {
            val fetcher = object : DataFetcher<Bank> {
                override fun getDataFromFireBase(list: List<Bank>) {
                    _banksLiveData.clear()
                    _banksLiveData.addAll(list.distinct())
                    viewModelScope.launch {
                        calculateTotalAmount()
                    }
                }

                override fun getSingleData(data: Bank) {
                }

            }
            repository.readBanks(fetcher)
        }
    }

    private fun readCategories(){
        viewModelScope.launch(Dispatchers.IO) {
            val fetcher = object : DataFetcher<Category> {
                override fun getDataFromFireBase(list: List<Category>) {
                    _categoryLiveData.clear()
                    _categoryLiveData.addAll(list.distinct())
                }

                override fun getSingleData(data: Category) {

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
        repository.addTransaction(context, transaction, partiesLiveData, bankLiveData, checked) {
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

    fun getChartData(duration: String) {
            val data = mutableListOf<Pair<String, Double>>()
            _categoryLiveData.forEach { category->
                var categoryTransaction = _transactionLiveData.filter {transaction ->
                    category.id == transaction.category
                }
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                val currentDate = Calendar.getInstance().time
                val dateFormat = SimpleDateFormat(Constant.DATE_FORMAT, Locale.getDefault())
                val today = dateFormat.format(currentDate)
                when (duration) {
                    "This Year" -> {
                        categoryTransaction = filterTransactionsByDateRange(categoryTransaction, "Jan 01, $currentYear", today)!!
                    }
                    "This Month" -> {
                        val lastMonth = Calendar.getInstance()
                        lastMonth.add(Calendar.MONTH, -1)
                        lastMonth.set(Calendar.DAY_OF_MONTH, 1)
                        val lastDayOfLastMonth = Calendar.getInstance()
                        lastDayOfLastMonth.set(Calendar.DAY_OF_MONTH, 1)
                        lastDayOfLastMonth.add(Calendar.DAY_OF_MONTH, -1)
                        val lastMonthStart = dateFormat.format(lastMonth.time)
                        val lastMonthEnd = dateFormat.format(lastDayOfLastMonth.time)
                        categoryTransaction = filterTransactionsByDateRange(categoryTransaction, lastMonthStart, lastMonthEnd)!!
                    }
                    "Last 7 Days" -> {
                        val sevenDaysAgo = Calendar.getInstance()
                        sevenDaysAgo.add(Calendar.DAY_OF_MONTH, -7)
                        val sevenDaysAgoDate = dateFormat.format(sevenDaysAgo.time)
                        categoryTransaction =
                            filterTransactionsByDateRange(categoryTransaction, sevenDaysAgoDate, today)!!
                    }
                    "Today" -> {
                        categoryTransaction =
                            filterTransactionsByDateRange(categoryTransaction, today, today)!!
                    }
                }
                var amount: Double = 0.0
                categoryTransaction.forEach {
                    amount += it.amount.toDouble()
                }
                if (categoryTransaction.isNotEmpty()) {
                    data.add(Pair<String, Double>(category.name, amount))
                }
            }
            _durationCategoryLiveData.value = data
    }

    @SuppressLint("DefaultLocale")
    fun calculateIncome() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat(Constant.DATE_FORMAT, Locale.getDefault())
        val today = dateFormat.format(currentDate)
        var incomeData = _transactionLiveData.filter { it.income && it.type != Constant.BANK_TO_BANK }
        incomeData = filterTransactionsByDateRange(incomeData, "Jan 01, $currentYear", today)!!
        var value: Double = 0.0
        if (incomeData.isNotEmpty()) {
            for (data in incomeData.distinct()) {
                value += data.amount.toDouble()
            }
        }
        _incomeLiveData.value = String.format("%.2f", value)
    }

    @SuppressLint("DefaultLocale")
    fun calculateExpense() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat(Constant.DATE_FORMAT, Locale.getDefault())
        val today = dateFormat.format(currentDate)
        var expenseData = _transactionLiveData.filter { !it.income }
        expenseData = filterTransactionsByDateRange(expenseData, "Jan 01, $currentYear", today)!!
        var value: Double = 0.0
        if (!expenseData.isNullOrEmpty()) {
            for (data in expenseData.distinct()) {
                value += data.amount.toDouble()
            }
        }
        _expenseLiveData.value = String.format("%.2f", value)
    }

    fun calculateTotalAmount(){
        var value: Double = 0.0
        _banksLiveData.distinct().forEach {
            value += it.balance.toDouble()
        }
        _totalLiveData.value = String.format(Locale.getDefault(), "%.2f", value)
    }

    private fun filterTransactionsByDateRange(
        transactions: List<Transaction>,
        startDate: String,
        endDate: String
    ): List<Transaction>? {
        val dateFormat = SimpleDateFormat(Constant.DATE_FORMAT, Locale.ENGLISH)
        val start: Date = dateFormat.parse(startDate) ?: return emptyList()
        val end: Date = dateFormat.parse(endDate) ?: return emptyList()
        return transactions.filter {
            val s = it.date
            val transactionDate: Date = dateFormat.parse(it.date) ?: return@filter false
            transactionDate in start..end
        }
    }
}