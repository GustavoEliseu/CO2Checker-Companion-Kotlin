package com.gustavo.cocheckercompaniomkotlin.ui.newlocation.viewmodel

import android.location.Address
import android.view.View
import android.widget.ArrayAdapter
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.gustavo.cocheckercompaniomkotlin.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddLocationViewModel @Inject constructor():BaseViewModel() {

    val markerDescriptionVisibility : MutableLiveData<Int> = MutableLiveData(View.GONE)
    val predictionsMutableVisibility: MutableLiveData<Int> = MutableLiveData(View.VISIBLE)
    val selectedLatLng: MutableLiveData<LatLng?> = MutableLiveData(null)
    override fun initialize() {
    }

}