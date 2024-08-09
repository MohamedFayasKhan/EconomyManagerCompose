package com.mohamedfayaskhan.economywear.presentation.database

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mohamedfayaskhan.economywear.presentation.constant.Constant

class Database {
    companion object {

        private var INSTANCE: DatabaseReference? = null

        fun getDataBase(): DatabaseReference {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val fbInstance = FirebaseDatabase.getInstance().reference.child(Constant.DATA)
                INSTANCE= fbInstance
                return fbInstance
            }
        }
    }
}