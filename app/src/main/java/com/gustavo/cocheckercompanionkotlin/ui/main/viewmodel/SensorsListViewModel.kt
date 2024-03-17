package com.gustavo.cocheckercompanionkotlin.ui.main.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DatabaseError
import com.gustavo.cocheckercompanionkotlin.base.BaseViewModel
import com.gustavo.cocheckercompanionkotlin.model.data.SensorItemList
import com.gustavo.cocheckercompanionkotlin.model.data.SimpleSensor
import com.gustavo.cocheckercompanionkotlin.model.domain.FetchSensorsUseCase
import com.gustavo.cocheckercompanionkotlin.ui.main.adapters.SensorListAdapter
import com.gustavo.cocheckercompanionkotlin.utils.LoggerUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SensorsListViewModel @Inject constructor() : BaseViewModel() {
    val mySensorListAdapter: SensorListAdapter =
        SensorListAdapter(::onClickSensor)
    private val fetchSensorsUseCase = FetchSensorsUseCase()


    val emptySensorListMessageVisibility = MutableLiveData<Int>()
    val mutableSensorClick = MutableLiveData<SimpleSensor>()
    val fabAddSensorClick = MutableLiveData<Boolean>()

    fun initialize() {
        mySensorListAdapter.clear()
        fetchSensorsUseCase.fetchUserSensorsData(object : FetchSensorsUseCase.UserSensorsDataListener {
            override fun onChildAdded(sensor: SensorItemList) {
                emptySensorListMessageVisibility.postValue(View.GONE)
                mySensorListAdapter.addSensor(sensor)
            }

            override fun onChildChanged(sensor: SensorItemList) {
                mySensorListAdapter.removeSensor(sensor)
            }

            override fun onChildRemoved(sensor: SensorItemList) {
                mySensorListAdapter.removeSensor(sensor)
                if (mySensorListAdapter.itemCount == 0) {
                    emptySensorListMessageVisibility.postValue(View.VISIBLE)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                LoggerUtil.printStackTraceOnlyInDebug(error.toException())
            }
        })
    }

    private fun onClickSensor(sensorMac: String) {
        mutableSensorClick.value = SimpleSensor(sensorMac)
    }

    fun fabClick(){
        fabAddSensorClick.value = true
    }
}