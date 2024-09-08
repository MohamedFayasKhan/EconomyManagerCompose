package com.mohamedfayaskhan.economywear.presentation.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mohamedfayaskhan.economywear.presentation.constant.Constant
import com.mohamedfayaskhan.economywear.presentation.database.Bank
import com.mohamedfayaskhan.economywear.presentation.database.Category
import com.mohamedfayaskhan.economywear.presentation.database.DataFetcher
import com.mohamedfayaskhan.economywear.presentation.database.DataRepository
import com.mohamedfayaskhan.economywear.presentation.database.Database
import com.mohamedfayaskhan.economywear.presentation.database.Party
import com.mohamedfayaskhan.economywear.presentation.database.Transaction
import com.mohamedfayaskhan.economywear.presentation.database.Type
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DataViewModel(application: Application): AndroidViewModel(application) {
    private lateinit var repository: DataRepository
    private val _transactions = mutableStateListOf<Transaction>()
    val transactions: SnapshotStateList<Transaction> = _transactions

    private val _parties = mutableStateListOf<Party>()
    val parties: SnapshotStateList<Party> = _parties

    private val _banks = mutableStateListOf<Bank>()
    val banks: SnapshotStateList<Bank> = _banks

    private val _categories = mutableStateListOf<Category>()
    val categories: SnapshotStateList<Category> = _categories

    private var _types = mutableStateListOf<Type>()
    val types: SnapshotStateList<Type> = _types

    private val _totalIncome = MutableLiveData<String>()
    val totalIncome: LiveData<String> get() = _totalIncome
    private val _totalExpense = MutableLiveData<String>()
    val totalExpense: LiveData<String> get() = _totalExpense
    private val _totalBankBalance = mutableStateOf<String>("")
    val totalBankBalance: MutableState<String> get() = _totalBankBalance
    private val _categoryMap = mutableMapOf<String, Category>()
    val categoryMap: MutableMap<String, Category> get() = _categoryMap
    private val _bankMap = mutableMapOf<String, Bank>()
    val bankMap: MutableMap<String, Bank> get() = _bankMap
    private val _partyMap = mutableMapOf<String, Party>()
    val partyMap: MutableMap<String, Party> get() = _partyMap
    private val _durationcategories = MutableLiveData<List<Pair<String, Double>>>()
//    val durationcategories: LiveData<List<Pair<String, Double>>> get() = _durationcategories
//    private val _categoryPieData = MutableLiveData<List<PieChartInput>>()
//    val categoryPieData: LiveData<List<PieChartInput>> get() = _categoryPieData

    fun initDatabase(uid: String?) {
        val database = uid?.let { Database.getDataBase().child(it).child(Constant.DATAS) }
        repository = DataRepository(database)
        _types.addAll(
            listOf(
                Type(Constant.SPENT, Constant.SPENT_VALUE),
                Type(Constant.BANK_TO_BANK, Constant.BANK_TO_BANK_VALUE),
                Type(Constant.BANK_TO_PARTY, Constant.BANK_TO_PARTY_VALUE),
                //            Type(Constant.PARTY_TO_PARTY,Constant.PARTY_TO_PARTY_VALUE),
                Type(Constant.PARTY_TO_BANK, Constant.PARTY_TO_BANK_VALUE),
                Type(Constant.ADD_BALANCE_TO_BANK, Constant.ADD_BALANCE_TO_BANK_VALUE),
                Type(Constant.REDUCE_BALANCE_FROM_BANK, Constant.REDUCE_BALANCE_FROM_BANK_VALUE),
                Type(Constant.ADD_BALANCE_TO_PARTY, Constant.ADD_BALANCE_TO_PARTY_VALUE),
                Type(Constant.REDUCE_BALANCE_FROM_PARTY, Constant.REDUCE_BALANCE_FROM_PARTY_VALUE)
            )
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

    private fun readTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            val fetcher = object : DataFetcher<Transaction> {
                override fun getDataFromFireBase(list: List<Transaction>) {
                    _transactions.clear()
                    _transactions.addAll(list.distinct().reversed())
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
                    _parties.clear()
                    parties.addAll(list.distinct())
                    createPartyMap(_parties)
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
                    _banks.clear()
                    _banks.addAll(list.distinct())
                    calculateTotalAmount()
                    createBankMap(_banks)
                }

                override fun getSingleData(data: Bank) {
                }

            }
            repository.readBanks(fetcher)
        }
    }

    private fun readCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            val fetcher = object : DataFetcher<Category> {
                override fun getDataFromFireBase(list: List<Category>) {
                    _categories.clear()
                    _categories.addAll(list.distinct())
                    createCategoryMap(_categories)
                }

                override fun getSingleData(data: Category) {

                }

            }
            repository.readCategories(fetcher)
        }
    }

    private fun createCategoryMap(categories: SnapshotStateList<Category>) {
        for (category in categories) {
            _categoryMap[category.id] = category
        }
    }

    private fun createPartyMap(parties: SnapshotStateList<Party>) {
        for (party in parties) {
            _partyMap[party.id] = party
        }
    }

    private fun createBankMap(banks: SnapshotStateList<Bank>) {
        for (bank in banks) {
            _bankMap[bank.id] = bank
        }
    }

    fun getUniqueDatabaseId(): String? {
        return repository.getUniqueDatabaseId()
    }

    fun getTimestamp(): String {
        return repository.getTimestamp()
    }

    fun addTransaction(
        transaction: Transaction,
        context: Context,
        checked: Boolean,
        isTransactionCompleted: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            repository.addTransaction(
                context,
                transaction,
                parties,
                banks,
                checked
            ) {
                isTransactionCompleted(it)
            }
        }
    }

    fun upsertTransaction(transaction: Transaction, context: Context) {
        viewModelScope.launch {
            repository.upsertTransaction(transaction, context)
        }
    }

    fun addCategory(category: String): String {
        return repository.upsertCategory(category)
    }

    fun addBank(bank: Bank) {
        viewModelScope.launch {
            repository.upsertAccount(bank)
        }
    }

    fun addParty(party: Party) {
        viewModelScope.launch {
            repository.upsertParty(party)
        }
    }

    fun getChartData(duration: String) {
        viewModelScope.launch {
            val data = mutableListOf<Pair<String, Double>>()
//            val dataPie = mutableListOf<PieChartInput>()
            _categories.forEach { category ->
                var categoryTransaction = _transactions.filter { transaction ->
                    category.id == transaction.category
                }
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                val currentDate = Calendar.getInstance().time
                val dateFormat = SimpleDateFormat(Constant.DATE_FORMAT, Locale.getDefault())
                val today = dateFormat.format(currentDate)
                when (duration) {
                    "This Year" -> {
                        categoryTransaction = filterTransactionsByDateRange(
                            categoryTransaction,
                            "Jan 01, $currentYear",
                            today
                        )
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
                        categoryTransaction = filterTransactionsByDateRange(
                            categoryTransaction,
                            lastMonthStart,
                            lastMonthEnd
                        )
                    }

                    "Last 7 Days" -> {
                        val sevenDaysAgo = Calendar.getInstance()
                        sevenDaysAgo.add(Calendar.DAY_OF_MONTH, -7)
                        val sevenDaysAgoDate = dateFormat.format(sevenDaysAgo.time)
                        categoryTransaction =
                            filterTransactionsByDateRange(
                                categoryTransaction,
                                sevenDaysAgoDate,
                                today
                            )
                    }

                    "Today" -> {
                        categoryTransaction =
                            filterTransactionsByDateRange(categoryTransaction, today, today)
                    }
                }
                var amount = 0.0
                categoryTransaction.forEach {
                    amount += it.amount.toDouble()
                }
                if (categoryTransaction.isNotEmpty()) {
                    data.add(Pair(category.name, amount))
//                    val color = kotlin.random.Random.nextLong(0xFFFFFFFF)
//                    dataPie.add(PieChartInput(color = Color(color), amount.toInt(), category.name))
                }
            }
            _durationcategories.value = data
//            _categoryPieData.value = dataPie
        }
    }

    @SuppressLint("DefaultLocale")
    fun calculateIncome() {
        viewModelScope.launch {
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val currentDate = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat(Constant.DATE_FORMAT, Locale.getDefault())
            val today = dateFormat.format(currentDate)
            var incomeData =
                _transactions.filter { it.income && it.type != Constant.BANK_TO_BANK }
            incomeData = filterTransactionsByDateRange(incomeData, "Jan 01, $currentYear", today)
            var value = 0.0
            if (incomeData.isNotEmpty()) {
                for (data in incomeData.distinct()) {
                    value += data.amount.toDouble()
                }
            }
            _totalIncome.value = String.format("%.2f", value)
        }
    }

    @SuppressLint("DefaultLocale")
    fun calculateExpense() {
        viewModelScope.launch {
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val currentDate = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat(Constant.DATE_FORMAT, Locale.getDefault())
            val today = dateFormat.format(currentDate)
            var expenseData = _transactions.filter { !it.income }
            expenseData =
                filterTransactionsByDateRange(expenseData, "Jan 01, $currentYear", today)
            var value = 0.0
            if (expenseData.isNotEmpty()) {
                for (data in expenseData.distinct()) {
                    value += data.amount.toDouble()
                }
            }
            _totalExpense.value = String.format("%.2f", value)
        }
    }

    fun calculateTotalAmount() {
        viewModelScope.launch {
            var value = 0.0
            _banks.distinct().forEach {
                value += it.balance.toDouble()
            }
            _totalBankBalance.value = String.format(Locale.getDefault(), "%.2f", value)
        }
    }

    private fun filterTransactionsByDateRange(
        transactions: List<Transaction>,
        startDate: String,
        endDate: String
    ): List<Transaction> {
        val dateFormat = SimpleDateFormat(Constant.DATE_FORMAT, Locale.ENGLISH)
        val start: Date = dateFormat.parse(startDate) ?: return emptyList()
        val end: Date = dateFormat.parse(endDate) ?: return emptyList()
        return transactions.filter {
            val transactionDate: Date = dateFormat.parse(it.date) ?: return@filter false
            transactionDate in start..end
        }
    }

    fun deleteTransaction(transaction: Transaction, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            when (transaction.type) {
                Constant.SPENT -> {
                    val banks = _banks.filter { it.id == transaction.from }
                    val fromBank = banks[0]
                    val fromBalance = fromBank.balance.toDouble()
                    val fromNewBalance = fromBalance + transaction.amount.toDouble()
                    fromBank.balance = fromNewBalance.toString()
                    repository.upsertAccount(fromBank)
                    repository.deleteTransaction(transaction)
                    onComplete(true)
                }

                Constant.BANK_TO_BANK -> {
                    val banksFrom = _banks.filter { it.id == transaction.from }
                    val banksTo = _banks.filter { it.id == transaction.to }
                    val fromBank = banksFrom[0]
                    val toBank = banksTo[0]
                    val fromBalance = fromBank.balance.toDouble()
                    val toBalance = toBank.balance.toDouble()
                    val fromNewBalance = fromBalance + transaction.amount.toDouble()
                    val toNewBalance = toBalance - transaction.amount.toDouble()
                    if (toNewBalance >= 0) {
                        fromBank.balance = fromNewBalance.toString()
                        toBank.balance = toNewBalance.toString()
                        repository.upsertAccount(fromBank)
                        repository.upsertAccount(toBank)
                        repository.deleteTransaction(transaction)
                        onComplete(true)
                    } else {
                        onComplete(false)
                    }
                }

                Constant.ADD_BALANCE_TO_BANK -> {
                    val banks = _banks.filter { it.id == transaction.to }
                    val toBank = banks[0]
                    val toBalance = toBank.balance.toDouble()
                    val toNewBalance = toBalance - transaction.amount.toDouble()
                    if (toNewBalance >= 0) {
                        toBank.balance = toNewBalance.toString()
                        repository.upsertAccount(toBank)
                        repository.deleteTransaction(transaction)
                        onComplete(true)
                    } else {
                        onComplete(false)
                    }
                }

                Constant.REDUCE_BALANCE_FROM_BANK -> {
                    val banks = _banks.filter { it.id == transaction.from }
                    val fromBank = banks[0]
                    val fromBalance = fromBank.balance.toDouble()
                    val fromNewBalance = fromBalance + transaction.amount.toDouble()
                    fromBank.balance = fromNewBalance.toString()
                    repository.upsertAccount(fromBank)
                    repository.deleteTransaction(transaction)
                    onComplete(true)
                }

                Constant.ADD_BALANCE_TO_PARTY -> {
                    val parties = _parties.filter { it.id == transaction.to }
                    val toParty = parties[0]
                    val toBalance = toParty.balance.toDouble()
                    val toNewBalance = toBalance - transaction.amount.toDouble()
                    if (toNewBalance >= 0) {
                        toParty.balance = toNewBalance.toString()
                        repository.upsertParty(toParty)
                        repository.deleteTransaction(transaction)
                        onComplete(true)
                    } else {
                        onComplete(false)
                    }
                }

                Constant.REDUCE_BALANCE_FROM_PARTY -> {
                    val parties = _parties.filter { it.id == transaction.from }
                    val fromParty = parties[0]
                    val fromBalance = fromParty.balance.toDouble()
                    val fromNewBalance = fromBalance + transaction.amount.toDouble()
                    fromParty.balance = fromNewBalance.toString()
                    repository.upsertParty(fromParty)
                    repository.deleteTransaction(transaction)
                    onComplete(true)
                }

                Constant.BANK_TO_PARTY -> {
                    val banks = _banks.filter { it.id == transaction.from }
                    val fromBank = banks[0]
                    val parties = _parties.filter { it.id == transaction.to }
                    val toParty = parties[0]
                    val fromBalance = fromBank.balance.toDouble()
                    val toBalance = toParty.balance.toDouble()
                    val fromNewBalance = fromBalance + transaction.amount.toDouble()
                    val toNewBalance = toBalance - transaction.amount.toDouble()
                    fromBank.balance = fromNewBalance.toString()
                    var isCompleted = false
                    if (toParty.receivable) {
//                            kadan kuduthal
                        if (toNewBalance >= 0) {
                            toParty.balance = toNewBalance.toString()
                            isCompleted = true
                        }
                    } else {
//                            kadan adaithal
                        toParty.balance = (toBalance + transaction.amount.toDouble()).toString()
                        isCompleted = true
                    }
                    if (isCompleted) {
                        repository.upsertAccount(fromBank)
                        repository.upsertParty(toParty)
                        repository.deleteTransaction(transaction)
                        onComplete(true)
                    } else {
                        onComplete(false)
                    }
                }

                Constant.PARTY_TO_BANK -> {
                    val parties = _parties.filter { it.id == transaction.from }
                    val fromParty = parties[0]
                    val banks = _banks.filter { it.id == transaction.to }
                    val toBank = banks[0]
                    val fromBalance = fromParty.balance.toDouble()
                    val toBalance = toBank.balance.toDouble()
                    val fromNewBalance = fromBalance + transaction.amount.toDouble()
                    val toNewBalance = toBalance - transaction.amount.toDouble()
                    if (toNewBalance >= 0) {
                        toBank.balance = toNewBalance.toString()
                        if (fromParty.receivable) {
//                        kadan kolmuthal
                            fromParty.balance = fromNewBalance.toString()
                        } else {
//                        kadan vaanguthal
                            fromParty.balance =
                                (fromBalance - transaction.amount.toDouble()).toString()
                        }
                        repository.upsertParty(fromParty)
                        repository.upsertAccount(toBank)
                        repository.deleteTransaction(transaction)
                        onComplete(true)
                    } else {
                        onComplete(false)
                    }
                }
            }
            readTransactions()
        }
    }
}