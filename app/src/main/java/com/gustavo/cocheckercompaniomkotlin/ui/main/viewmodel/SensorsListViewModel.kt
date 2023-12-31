package com.gustavo.cocheckercompaniomkotlin.ui.main.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DatabaseError
import com.gustavo.cocheckercompaniomkotlin.base.BaseViewModel
import com.gustavo.cocheckercompaniomkotlin.model.data.SensorItemList
import com.gustavo.cocheckercompaniomkotlin.model.data.SimpleSensor
import com.gustavo.cocheckercompaniomkotlin.model.domain.FetchSensorsUseCase
import com.gustavo.cocheckercompaniomkotlin.ui.main.adapters.SensorListAdapter
import com.gustavo.cocheckercompaniomkotlin.utils.LoggerUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SensorsListViewModel @Inject constructor() : BaseViewModel() {
    val mySensorListAdapter: SensorListAdapter =
        SensorListAdapter(::onClickSensor)
    private val fetchSensorsUseCase = FetchSensorsUseCase()


    val emptySensorListMessageVisibility = MutableLiveData<Int>()
    val mutableSensorClick = MutableLiveData<SimpleSensor>()

    override fun initialize() {
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
}