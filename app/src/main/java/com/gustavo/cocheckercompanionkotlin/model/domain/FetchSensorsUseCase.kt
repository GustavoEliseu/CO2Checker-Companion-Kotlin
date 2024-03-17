package com.gustavo.cocheckercompanionkotlin.model.domain

import com.google.firebase.database.DatabaseError
import com.gustavo.cocheckercompanionkotlin.data.remote.firebase.FirebaseUserDataSource
import com.gustavo.cocheckercompanionkotlin.model.data.SensorItemList

class FetchSensorsUseCase {
    val firebaseDataSource = FirebaseUserDataSource()
    val currentSensorsPage = 20

    interface UserSensorsDataListener {
        fun onChildAdded(sensor: SensorItemList)
        fun onChildChanged(sensor: SensorItemList)
        fun onChildRemoved(sensor: SensorItemList)
        fun onCancelled(error: DatabaseError)
    }

    fun fetchUserSensorsData(userListener: UserSensorsDataListener) {
        firebaseDataSource.fetchUserDataSensors(currentSensorsPage,userListener)
    }

    fun fetchSensorsList(result:(List<SensorItemList>?)->Unit){
        firebaseDataSource.fetchUserDataInitialSensors(result)
    }
}