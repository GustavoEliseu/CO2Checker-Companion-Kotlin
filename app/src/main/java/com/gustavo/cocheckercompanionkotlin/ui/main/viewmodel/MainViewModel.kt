package com.gustavo.cocheckercompanionkotlin.ui.main.viewmodel

import com.google.firebase.database.DatabaseError
import com.gustavo.cocheckercompanionkotlin.base.BaseViewModel
import com.gustavo.cocheckercompanionkotlin.model.data.NewSensorData
import com.gustavo.cocheckercompanionkotlin.model.domain.AddSensorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : BaseViewModel() {
    private val addSensorsUseCase = AddSensorUseCase()

    fun finishAddSensor(sensor: NewSensorData) {
        addSensorsUseCase.addSensor(sensor,
            object : AddSensorUseCase.UserAddSensorDataListener {
                override fun onChildAdded(locationItemList: NewSensorData) {

                }

                override fun onChildChanged(locationItemList: NewSensorData) {
                }

                override fun onChildRemoved(locationItemList: NewSensorData) {
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}