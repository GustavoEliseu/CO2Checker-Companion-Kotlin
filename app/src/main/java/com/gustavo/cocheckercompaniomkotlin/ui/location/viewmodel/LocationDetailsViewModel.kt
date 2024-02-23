package com.gustavo.cocheckercompaniomkotlin.ui.location.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gustavo.cocheckercompaniomkotlin.base.BaseViewModel
import com.gustavo.cocheckercompaniomkotlin.model.data.CustomResult
import com.gustavo.cocheckercompaniomkotlin.model.data.LocationItemList
import com.gustavo.cocheckercompaniomkotlin.model.domain.GetLocationDetailsUseCase
import com.gustavo.cocheckercompaniomkotlin.model.domain.GetLocationMeasureUseCase
import com.gustavo.cocheckercompaniomkotlin.model.domain.GetSensorMeasureUseCase
import com.gustavo.cocheckercompaniomkotlin.ui.measure.MeasureListAdapter
import com.gustavo.cocheckercompaniomkotlin.utils.LoggerUtil
import com.gustavo.cocheckercompanionkotlin.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationDetailsViewModel @Inject constructor() : BaseViewModel() {
    val getLocationMeasureUseCase: GetLocationMeasureUseCase = GetLocationMeasureUseCase()
    val getLocationDetailsUseCase: GetLocationDetailsUseCase = GetLocationDetailsUseCase()
    val myMeasuresListAdapter: MeasureListAdapter =
        MeasureListAdapter(::loadMoreMeasures)


    val emptyMessageVisibility = MutableLiveData<Int>()
    var locationUid: String = ""
    private var isLastPage = false

    val toastMessageState = MutableLiveData<Int?>(null)
    var currentLocation:LocationItemList? = null


    suspend fun getLocationDetails(uid: String) {
        locationUid = uid
        when (val result = getLocationDetailsUseCase.fetchUserLocationDetailsData(locationUid)) {
            is CustomResult.Success -> {
                if (result.data != null) {
                    locationDetails(result.data)
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

    suspend fun getLocationMeasures(uid: String) {
        locationUid = uid
        when (val result = getLocationMeasureUseCase.fetchLocationsMeasureData(locationUid)) {
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

    private fun locationDetails(locationItemList: LocationItemList) {
        currentLocation = locationItemList
        viewModelScope.launch {
            getLocationMeasures(locationItemList.uuid)
        }
    }

    private fun loadMoreMeasures() {
        viewModelScope.launch {
            getLocationMeasures(locationUid)
        }
    }

    override fun initialize() {


    }
}