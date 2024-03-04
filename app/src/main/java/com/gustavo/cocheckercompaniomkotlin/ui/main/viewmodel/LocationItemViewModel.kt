package com.gustavo.cocheckercompaniomkotlin.ui.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gustavo.cocheckercompaniomkotlin.model.data.LocationItemList
import com.gustavo.cocheckercompaniomkotlin.utils.getStringWithExtrasNonNullable
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