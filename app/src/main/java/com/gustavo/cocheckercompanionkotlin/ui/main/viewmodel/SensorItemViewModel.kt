package com.gustavo.cocheckercompanionkotlin.ui.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gustavo.cocheckercompanionkotlin.model.data.SensorItemList
import com.gustavo.cocheckercompanionkotlin.R


class SensorItemViewModel :
    ViewModel() {
    val mutableSensorName = MutableLiveData<String>()
    val mutableSensorLastMeasure = MutableLiveData<String>()
    val mutableSensorLastLocation = MutableLiveData<String?>()
    val mutableSensorLastLocationReference = MutableLiveData<Int?>()

    fun bind(sensorItem: SensorItemList) {
        mutableSensorName.value = sensorItem.name
        if (sensorItem.LastLocation != null) {
            sensorItem.LastLocation?.let {
                mutableSensorLastLocationReference.value = null
                mutableSensorLastLocation.value = it.locationName
            }
        } else {
            mutableSensorLastMeasure.value = ""
            mutableSensorLastLocation.value = ""
            mutableSensorLastLocationReference.value = R.string.not_have_measure_sensor
        }
    }

}