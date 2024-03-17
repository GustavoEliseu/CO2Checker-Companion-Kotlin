package com.gustavo.cocheckercompanionkotlin.ui.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gustavo.cocheckercompanionkotlin.model.data.LocationItemList
import com.gustavo.cocheckercompanionkotlin.utils.getStringWithExtrasNonNullable
import com.gustavo.cocheckercompanionkotlin.R

class LocationItemViewModel :
    ViewModel() {

    val locationNameText = MutableLiveData<String>()
    val locationMeasure = MutableLiveData<String>()
    val locationImageDecsription = MutableLiveData<String>()

    fun bind(locationItem: LocationItemList) {
        locationNameText.value = locationItem.name
        locationMeasure.value = (locationItem.lastMeasure?.date ?: "").toString()
        locationImageDecsription.value = getStringWithExtrasNonNullable(R.string.locationDescription,locationItem.name)
    }
}