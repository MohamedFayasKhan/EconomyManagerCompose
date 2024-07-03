package com.mohamedkhan.economymanagercompose.database

interface DataFetcher<T> {

    fun getDataFromFireBase(list: List<T>)
    fun getSingleData(data: T)
}