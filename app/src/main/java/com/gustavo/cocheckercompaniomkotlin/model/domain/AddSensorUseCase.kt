package com.gustavo.cocheckercompaniomkotlin.model.domain

import com.google.firebase.database.DatabaseError
import com.gustavo.cocheckercompaniomkotlin.data.remote.firebase.FirebaseUserDataSource
import com.gustavo.cocheckercompaniomkotlin.model.data.NewSensorData

class AddSensorUseCase {
    val firebaseUserDataSource = FirebaseUserDataSource()

    interface UserAddSensorDataListener {
        fun onChildAdded(sensorItemList: NewSensorData)
        fun onChildChanged(sensorItemList: NewSensorData)
        fun onChildRemoved(sensorItemList: NewSensorData)
        fun onCancelled(error: DatabaseError)
    }

    fun addSensor(newSensorData: NewSensorData, userSensorListener: UserAddSensorDataListener) {
        firebaseUserDataSource.addSensorToUser(newSensorData, userSensorListener)
    }
}