package com.gustavo.cocheckercompaniomkotlin.model.domain

import com.google.firebase.database.DatabaseError
import com.gustavo.cocheckercompaniomkotlin.data.remote.firebase.FirebaseUserDataSource
import com.gustavo.cocheckercompaniomkotlin.model.data.SensorWifiData

class SensorExistsUseCase {
        val firebaseDataSource = FirebaseUserDataSource()

        interface SensorExistsDataListener {
            fun onItemExists(sensor: SensorWifiData, exists:Boolean)
            fun onCancelled(error: DatabaseError)
        }

        fun checkIfSensorAlreadyRegistered(sensor: SensorWifiData,userListener: SensorExistsDataListener) {
            firebaseDataSource.checkIfSensorAlreadyRegistered(sensor,userListener)
        }

}