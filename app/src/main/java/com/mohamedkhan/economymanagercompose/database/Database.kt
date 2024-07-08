package com.mohamedkhan.economymanagercompose.database

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mohamedkhan.economymanagercompose.constant.Constant

class Database {

    companion object {

        private var INSTANCE: DatabaseReference? = null

        fun getDataBase(): DatabaseReference {
            var tempInstance = INSTANCE
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