package com.mohamedkhan.economymanagercompose.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mohamedkhan.economymanagercompose.constant.Constant
import com.mohamedkhan.economymanagercompose.database.Bank
import com.mohamedkhan.economymanagercompose.database.DataFetcher
import com.mohamedkhan.economymanagercompose.database.DataRepository
import com.mohamedkhan.economymanagercompose.database.Database
import com.mohamedkhan.economymanagercompose.database.Party
import com.mohamedkhan.economymanagercompose.database.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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

    fun initDatabase(uid: String?) {
        val database = uid?.let { Database.getDataBase().child(it).child(Constant.DATAS) }
        repository = DataRepository(database)
    }

    fun performTasks(onComplete: () -> Unit) {
        viewModelScope.launch {
            readTransactions()
            readParties()
            readBanks()
            onComplete()
        }
    }

    fun readTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            val fetcher = object : DataFetcher<Transaction> {
                override fun getDataFromFireBase(list: List<Transaction>) {
                    Collections.reverse(list)
                    _transactionLiveData.value = list
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
}