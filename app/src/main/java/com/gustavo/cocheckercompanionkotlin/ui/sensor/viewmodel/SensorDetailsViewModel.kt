package com.gustavo.cocheckercompanionkotlin.ui.sensor.viewmodel

import androidx.lifecycle.MutableLiveData
import com.gustavo.cocheckercompanionkotlin.base.BaseViewModel
import com.gustavo.cocheckercompanionkotlin.model.data.CustomResult
import com.gustavo.cocheckercompanionkotlin.model.domain.GetSensorMeasureUseCase
import com.gustavo.cocheckercompanionkotlin.ui.measure.MeasureListAdapter
import com.gustavo.cocheckercompanionkotlin.utils.LoggerUtil
import com.gustavo.cocheckercompanionkotlin.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SensorDetailsViewModel @Inject constructor() : BaseViewModel() {
    val getSensorMeasureUseCase: GetSensorMeasureUseCase = GetSensorMeasureUseCase()
    val myMeasuresListAdapter: MeasureListAdapter = MeasureListAdapter()


    val emptyMessageVisibility = MutableLiveData<Int>()
    var deviceUid: String = ""
    private var isLastPage = false

    val toastMessageState = MutableLiveData<Int?>(null)


    suspend fun getMeasures(uid: String) {
        deviceUid = uid
        when (val result = getSensorMeasureUseCase.fetchUserSensorsData(deviceUid)) {
            is CustomResult.Success -> {
                if (result.data != null) {
                    if (myMeasuresListAdapter.isDataEmpty()){
                        myMeasuresListAdapter.setCurrentData(result.data)
                    }else{
                        myMeasuresListAdapter.addAll(result.data)
                    }
                } else {
                    isLastPage = true
                }
            }

            is CustomResult.Error -> {
                toastMessageState.value = R.string.genericMeasureError
                LoggerUtil.printStackTraceOnlyInDebug(result.exception)
            }
        }
    }
}