package com.gustavo.cocheckercompanionkotlin.ui.location.viewmodel

import androidx.lifecycle.MutableLiveData
import com.gustavo.cocheckercompanionkotlin.base.BaseViewModel
import com.gustavo.cocheckercompanionkotlin.model.data.CustomResult
import com.gustavo.cocheckercompanionkotlin.model.data.LocationItemList
import com.gustavo.cocheckercompanionkotlin.model.domain.GetLocationDetailsUseCase
import com.gustavo.cocheckercompanionkotlin.ui.measure.MeasureListAdapter
import com.gustavo.cocheckercompanionkotlin.utils.LoggerUtil
import com.gustavo.cocheckercompanionkotlin.utils.extensions.isNullOrEmptyOrBlank
import com.gustavo.cocheckercompanionkotlin.utils.getSafeMapUrlString
import com.gustavo.cocheckercompanionkotlin.utils.getStringWithExtrasNonNullable
import com.gustavo.cocheckercompanionkotlin.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LocationDetailsViewModel @Inject constructor() : BaseViewModel() {
    private val getLocationDetailsUseCase: GetLocationDetailsUseCase = GetLocationDetailsUseCase()
    val myMeasuresListAdapter: MeasureListAdapter = MeasureListAdapter()

    val locationNameMutableText: MutableLiveData<String?> = MutableLiveData(null)
    val locationImageDecsription = MutableLiveData<String>()
    val locationUriMutableText = MutableLiveData<String?>()
    val toastMessageState = MutableLiveData<Int?>(null)


    suspend fun getLocationDetails(uid: String) {
        when (val result = getLocationDetailsUseCase.fetchUserLocationDetailsData(uid)) {
            is CustomResult.Success -> {
                if (result.data != null) {
                    locationDetails(result.data)
                }
            }

            is CustomResult.Error -> {
                toastMessageState.value = R.string.genericMeasureError
                LoggerUtil.printStackTraceOnlyInDebug(result.exception)
            }
        }
    }

    private fun locationDetails(locationItem: LocationItemList) {
        if(locationNameMutableText.value.isNullOrEmptyOrBlank()) {
            locationNameMutableText.value = locationItem.name
        }
        if(locationImageDecsription.value.isNullOrEmptyOrBlank()) {
            locationImageDecsription.value =
                getStringWithExtrasNonNullable(R.string.locationDescription, locationItem.name)
        }
        if(locationUriMutableText.value.isNullOrEmptyOrBlank()){
            locationUriMutableText.value = getSafeMapUrlString(locationItem.latitude.toString(),locationItem.longitude.toString())
        }
        locationItem.Measures?.let {measureMap->
            myMeasuresListAdapter.addAll(measureMap.map { it.value })
        }
    }

}