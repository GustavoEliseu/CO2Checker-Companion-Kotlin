package com.gustavo.cocheckercompaniomkotlin.ui.main.adapters.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gustavo.cocheckercompaniomkotlin.model.SensorItemList
import com.gustavo.cocheckercompanionkotlin.R
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class SensorItemViewModel:
    ViewModel() {
    val mutableSensorName = MutableLiveData<String>()
    val mutableSensorLastMeasure = MutableLiveData<String>()
    val mutableSensorLastLocation = MutableLiveData<String?>()
    val mutableSensorLastLocationReference = MutableLiveData<Int?>()

    fun bind(sensorItem: SensorItemList){
        mutableSensorName.value = sensorItem.name
        if(sensorItem.LastLocation!= null){
            sensorItem.LastLocation?.let{
                mutableSensorLastLocationReference.value = null
                mutableSensorLastLocation.value = it.locationName
            }
        }else{
            mutableSensorLastMeasure.value= ""
            mutableSensorLastLocation.value = ""
            mutableSensorLastLocationReference.value = R.string.not_have_measure_sensor
        }
    }

}