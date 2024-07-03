package com.mohamedkhan.economymanagercompose.database

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.mohamedkhan.economymanagercompose.constant.Constant

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
}