package com.gustavo.cocheckercompaniomkotlin.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference

import com.google.firebase.database.FirebaseDatabase
import com.gustavo.cocheckercompaniomkotlin.utils.DEVICES
import com.gustavo.cocheckercompaniomkotlin.utils.LOCATIONS
import com.gustavo.cocheckercompaniomkotlin.utils.USERS


class FirebaseDatabaseManager() {
    private var firebaseDatabase: FirebaseDatabase? = null
    private var currentUser: FirebaseUser? = null

    fun initializeFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance()
        currentUser = FirebaseAuth.getInstance().currentUser
        // Set up other configurations if needed
    }

    fun getCurrentUserReference() : DatabaseReference?{
        firebaseDatabase?.let {database->
        currentUser?.uid?.let {
            val user = database.getReference(USERS).child(it)
            user.keepSynced(true)
            user.orderByValue().limitToLast(20)
            return user
        }
        }
        return null
    }

    fun getFireBaseDatabaseSensors(): DatabaseReference? {
        return firebaseDatabase?.getReference(DEVICES)
    }

    fun getFireBaseDatabaseLocations(): DatabaseReference? {
        return firebaseDatabase?.getReference(LOCATIONS)
    }
}