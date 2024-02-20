package com.gustavo.cocheckercompaniomkotlin.model.domain

import com.google.firebase.database.DatabaseError
import com.gustavo.cocheckercompaniomkotlin.data.remote.firebase.FirebaseUserLocationDataSource
import com.gustavo.cocheckercompaniomkotlin.model.data.NewLocationData

class AddLocationUseCase() {
    val firebaseUserLocationsDataSource = FirebaseUserLocationDataSource()
    interface UserAddLocationDataListener {
        fun onChildAdded(locationItemList: NewLocationData)
        fun onCancelled(error: DatabaseError)
    }

    fun addNewLocation(newLocationData: NewLocationData, userLocationListener: UserAddLocationDataListener){
        firebaseUserLocationsDataSource.createLocation(newLocationData,userLocationListener)
    }
}