package com.gustavo.cocheckercompaniomkotlin.ui.main.viewmodel

import com.google.firebase.database.DatabaseError
import com.gustavo.cocheckercompaniomkotlin.base.BaseViewModel
import com.gustavo.cocheckercompaniomkotlin.model.data.NewSensorData
import com.gustavo.cocheckercompaniomkotlin.model.domain.AddSensorUseCase
import com.gustavo.cocheckercompaniomkotlin.model.domain.FetchLocationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor():BaseViewModel() {
    private val addSensorsUseCase = AddSensorUseCase()

    override fun initialize() {
    }

    fun finishAddSensor(sensor: NewSensorData) {
        addSensorsUseCase.addSensor(sensor,
            object: AddSensorUseCase.UserAddSensorDataListener {
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