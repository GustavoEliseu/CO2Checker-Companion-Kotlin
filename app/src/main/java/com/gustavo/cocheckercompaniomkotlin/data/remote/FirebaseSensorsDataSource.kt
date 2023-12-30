package com.gustavo.cocheckercompaniomkotlin.data.remote

import com.google.firebase.database.DatabaseReference


class FirebaseSensorsDataSource {
    private var userRef: DatabaseReference? = FirebaseDatabaseManager.getCurrentUserReference()

    fun fetchUserData( onDataFetched: (DatabaseReference) -> Unit) {
        if(userRef == null){
            userRef = FirebaseDatabaseManager.getCurrentUserReference()
        }
        userRef?.let {
            onDataFetched(it)
        }
    }
}