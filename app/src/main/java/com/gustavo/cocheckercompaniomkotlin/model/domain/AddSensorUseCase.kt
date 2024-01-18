package com.gustavo.cocheckercompaniomkotlin.model.domain

import com.google.firebase.database.DatabaseError
import com.gustavo.cocheckercompaniomkotlin.data.remote.FirebaseUserDataSource
import com.gustavo.cocheckercompaniomkotlin.model.data.LocationItemList
import com.gustavo.cocheckercompaniomkotlin.model.data.NewSensorData

class AddSensorUseCase {
    val firebaseUserDataSource = FirebaseUserDataSource()
    interface UserAddSensorDataListener {
        fun onChildAdded(locationItemList: NewSensorData)
        fun onChildChanged(locationItemList: NewSensorData)
        fun onChildRemoved(locationItemList: NewSensorData)
        fun onCancelled(error: DatabaseError)
    }

    fun addSensor(newSensorData: NewSensorData, userLocationListener: UserAddSensorDataListener){
        firebaseUserDataSource.addSensorToUser(newSensorData,userLocationListener)
    }
}