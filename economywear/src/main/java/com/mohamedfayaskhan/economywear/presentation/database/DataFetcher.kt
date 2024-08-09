package com.mohamedfayaskhan.economywear.presentation.database

interface DataFetcher<T> {
    fun getDataFromFireBase(list: List<T>)
    fun getSingleData(data: T)
}