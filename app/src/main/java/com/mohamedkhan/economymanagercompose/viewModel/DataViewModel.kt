package com.mohamedkhan.economymanagercompose.viewModel

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
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
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Calendar
import java.util.Locale

class DataViewModel(application: Application) : AndroidViewModel(application) {

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

    var totalIncome by mutableStateOf("")
        private set
    var totalExpense by mutableStateOf("")
        private set
    private val _totalBankBalance = mutableStateOf("")
    val totalBankBalance: State<String> get() = _totalBankBalance
//    private val _durationCategories = MutableLiveData<List<Pair<String, Double>>>()
//    val durationCategories: LiveData<List<Pair<String, Double>>> get() = _durationCategories
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
                    }
                }

                override fun getSingleData(data: Transaction) {}

            }
            repository.readTransactions(fetcher)
        }
    }

    fun dateToEpoch(date: String): Long {
        var dateMillis: Long
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val formatter = DateTimeFormatter.ofPattern(Constant.DATE_FORMAT)
                val localDate = LocalDate.parse(date, formatter)
                val instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
                dateMillis = instant.toEpochMilli()
            } catch (e: DateTimeParseException) {
                val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH)
                val localDate = LocalDate.parse(date, formatter)
                val instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
                dateMillis = instant.toEpochMilli()
            }
            return dateMillis
        }
        return System.currentTimeMillis()
    }

    fun epochToDate(date: Long): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val instant = Instant.ofEpochMilli(date)
            val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            val formatter = DateTimeFormatter.ofPattern(Constant.DATE_FORMAT)
            return localDateTime.format(formatter)
        }
        return System.currentTimeMillis().toString()
    }

    fun epochToTimeStamp(timeStamp: Long): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val instant = Instant.ofEpochMilli(timeStamp)
            val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            val formatter = DateTimeFormatter.ofPattern(Constant.TIMESTAMP_FORMAT)
            return localDateTime.format(formatter)
        }
        return System.currentTimeMillis().toString()
    }

    fun timeStampToEpoch(): Long {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val now: Instant = Instant.now()
            val timeStampMillis: Long = now.toEpochMilli()
            return timeStampMillis
        }
        return System.currentTimeMillis()
    }

    private fun readParties() {
        viewModelScope.launch(Dispatchers.IO) {
            val fetcher = object : DataFetcher<Party> {
                override fun getDataFromFireBase(list: List<Party>) {
                    _parties.clear()
                    parties.addAll(list.distinct())
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

    private fun readCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            val fetcher = object : DataFetcher<Category> {
                override fun getDataFromFireBase(list: List<Category>) {
                    _categories.clear()
                    _categories.addAll(list.distinct())
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
                    value += data.amount
                }
            }
            totalIncome = String.format(Locale.getDefault(),"%.2f", value)
        }
    }

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
                    value += data.amount
                }
            }
            totalExpense = String.format(Locale.getDefault(), "%.2f", value)
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
        val start = dateToEpoch(startDate)
        val end = dateToEpoch(endDate)
        return transactions.filter {
            it.date >= start || it.date <= end
        }
    }

    fun deleteTransaction(transaction: Transaction, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            when (transaction.type) {
                Constant.SPENT -> {
                    val banks = _banks.filter { it.id == transaction.from }
                    val fromBank = banks[0]
                    val fromBalance = fromBank.balance.toDouble()
                    val fromNewBalance = fromBalance + transaction.amount
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
                    val fromNewBalance = fromBalance + transaction.amount
                    val toNewBalance = toBalance - transaction.amount
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
                    val toNewBalance = toBalance - transaction.amount
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
                    val fromNewBalance = fromBalance + transaction.amount
                    fromBank.balance = fromNewBalance.toString()
                    repository.upsertAccount(fromBank)
                    repository.deleteTransaction(transaction)
                    onComplete(true)
                }

                Constant.ADD_BALANCE_TO_PARTY -> {
                    val parties = _parties.filter { it.id == transaction.to }
                    val toParty = parties[0]
                    val toBalance = toParty.balance.toDouble()
                    val toNewBalance = toBalance - transaction.amount
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
                    val fromNewBalance = fromBalance + transaction.amount
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
                    val fromNewBalance = fromBalance + transaction.amount
                    val toNewBalance = toBalance - transaction.amount
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
                        toParty.balance = (toBalance + transaction.amount).toString()
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
                    val fromNewBalance = fromBalance + transaction.amount
                    val toNewBalance = toBalance - transaction.amount
                    if (toNewBalance >= 0) {
                        toBank.balance = toNewBalance.toString()
                        if (fromParty.receivable) {
//                        kadan kolmuthal
                            fromParty.balance = fromNewBalance.toString()
                        } else {
//                        kadan vaanguthal
                            fromParty.balance =
                                (fromBalance - transaction.amount).toString()
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