package com.gustavo.cocheckercompanionkotlin.model.domain

import com.google.firebase.database.DatabaseError
import com.gustavo.cocheckercompanionkotlin.data.remote.firebase.FirebaseUserDataSource
import com.gustavo.cocheckercompanionkotlin.model.data.LocationItemList

class FetchLocationsUseCase {
    val firebaseUserDataSource = FirebaseUserDataSource()
    private val currentLocationsPage = 20

    interface UserLocationsDataListener {
        fun onChildAdded(locationItemList: LocationItemList)
        fun onChildChanged(locationItemList: LocationItemList)
        fun onChildRemoved(locationItemList: LocationItemList)
        fun onCancelled(error: DatabaseError)
    }

    fun fetchUserLocationsData(userLocationListener: UserLocationsDataListener) {
        firebaseUserDataSource.fetchUserDataLocations(currentLocationsPage, userLocationListener)
    }

    fun fetchSensorsList(result: (List<LocationItemList>?) -> Unit) {
        firebaseUserDataSource.fetchUserDataInitialLocations(result)
    }
}