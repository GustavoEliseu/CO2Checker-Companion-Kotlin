package com.gustavo.cocheckercompaniomkotlin.ui.main.viewmodel

import androidx.lifecycle.MutableLiveData
import com.gustavo.cocheckercompaniomkotlin.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor():BaseViewModel() {


    val emptyLocationsListMessageVisibility = MutableLiveData<Int>()

    private fun onClickLocation(locationUuid: String,locationName: String) {
    }

    override fun initialize() {
    }
}