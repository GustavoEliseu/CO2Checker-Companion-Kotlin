package com.gustavo.cocheckercompaniomkotlin.ui.location.viewmodel

import androidx.lifecycle.MutableLiveData
import com.gustavo.cocheckercompaniomkotlin.base.BaseViewModel
import com.gustavo.cocheckercompaniomkotlin.model.data.CustomResult
import com.gustavo.cocheckercompaniomkotlin.model.data.LocationItemList
import com.gustavo.cocheckercompaniomkotlin.model.domain.GetLocationDetailsUseCase
import com.gustavo.cocheckercompaniomkotlin.ui.measure.MeasureListAdapter
import com.gustavo.cocheckercompaniomkotlin.utils.LoggerUtil
import com.gustavo.cocheckercompaniomkotlin.utils.extensions.isNullOrEmptyOrBlank
import com.gustavo.cocheckercompaniomkotlin.utils.getSafeMapUrlString
import com.gustavo.cocheckercompaniomkotlin.utils.getStringWithExtrasNonNullable
import com.gustavo.cocheckercompanionkotlin.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LocationDetailsViewModel @Inject constructor() : BaseViewModel() {
    val getLocationDetailsUseCase: GetLocationDetailsUseCase = GetLocationDetailsUseCase()
    val myMeasuresListAdapter: MeasureListAdapter = MeasureListAdapter()

    val locationNameMutableText: MutableLiveData<String?> = MutableLiveData(null)
    val locationImageDecsription = MutableLiveData<String>()
    val locationUriMutableText = MutableLiveData<String?>()

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
        currentLocation = locationItem
        locationItem.Measures?.let {measureMap->
            myMeasuresListAdapter.addAll(measureMap.map { it.value })
        }
    }

    override fun initialize() {

    }
}