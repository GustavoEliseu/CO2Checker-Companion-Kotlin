package com.gustavo.cocheckercompaniomkotlin.ui.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gustavo.cocheckercompaniomkotlin.model.data.LocationItemList

class LocationItemViewModel :
    ViewModel() {

    val locationNameText = MutableLiveData<String>()
    val locationMeasure = MutableLiveData<String>()

    fun bind(locationItem: LocationItemList) {
        locationNameText.value = locationItem.name
        locationMeasure.value = (locationItem.lastMeasure?.date ?: "").toString()
    }
}