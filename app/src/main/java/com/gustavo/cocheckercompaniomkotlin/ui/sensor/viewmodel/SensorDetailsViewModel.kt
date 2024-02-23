package com.gustavo.cocheckercompaniomkotlin.ui.sensor.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gustavo.cocheckercompaniomkotlin.base.BaseViewModel
import com.gustavo.cocheckercompaniomkotlin.model.data.CustomResult
import com.gustavo.cocheckercompaniomkotlin.model.domain.GetSensorMeasureUseCase
import com.gustavo.cocheckercompaniomkotlin.ui.measure.MeasureListAdapter
import com.gustavo.cocheckercompaniomkotlin.utils.LoggerUtil
import com.gustavo.cocheckercompaniomkotlin.utils.extensions.isNotNullOrNotEmptyOrNotBlank
import com.gustavo.cocheckercompanionkotlin.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SensorDetailsViewModel @Inject constructor() : BaseViewModel() {
    val getSensorMeasureUseCase: GetSensorMeasureUseCase = GetSensorMeasureUseCase()
    val myMeasuresListAdapter: MeasureListAdapter =
        MeasureListAdapter(::loadMoreMeasures)


    val emptyMessageVisibility = MutableLiveData<Int>()
    var deviceUid: String = ""
    private var isLastPage = false

    val toastMessageState = MutableLiveData<Int?>(null)


    suspend fun getMeasures(uid: String) {
        deviceUid = uid
        when (val result = getSensorMeasureUseCase.fetchUserSensorsData(deviceUid)) {
            is CustomResult.Success -> {
                if (result.data != null) {
                    myMeasuresListAdapter.addAll(result.data)
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

    private fun loadMoreMeasures() {
        viewModelScope.launch {
            getMeasures(deviceUid)
        }
    }

    override fun initialize() {


    }
}