package com.gustavo.cocheckercompaniomkotlin.ui.newlocation.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseError
import com.gustavo.cocheckercompaniomkotlin.base.BaseViewModel
import com.gustavo.cocheckercompaniomkotlin.model.data.NewLocationData
import com.gustavo.cocheckercompaniomkotlin.model.domain.AddLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddLocationViewModel @Inject constructor() : BaseViewModel() {

    val markerDescriptionVisibility: MutableLiveData<Int> = MutableLiveData(View.GONE)
    val predictionsMutableVisibility: MutableLiveData<Int> = MutableLiveData(View.VISIBLE)
    val saveLocationLoadingVisibility: MutableLiveData<Int> = MutableLiveData(View.GONE)
    val saveButtonVisibility: MutableLiveData<Int> = MutableLiveData(View.VISIBLE)
    val saveLocationButtonClicked: MutableLiveData<Boolean> = MutableLiveData(false)
    val selectedLatLng: MutableLiveData<LatLng?> = MutableLiveData(null)

    private val addNewLocationUseCase = AddLocationUseCase()

    val saveLocationResult: MutableLiveData<SaveLocationResult> =
        MutableLiveData(SaveLocationResult.NONE)

    fun finishSaveLocation(newLocationData: NewLocationData) {
        saveLocationResult.value = SaveLocationResult.LOADING
        addNewLocationUseCase.addNewLocation(newLocationData,
            object : AddLocationUseCase.UserAddLocationDataListener {
                override fun onChildAdded(locationItemList: NewLocationData) {
                    saveLocationResult.value = SaveLocationResult.SUCCESS
                }

                override fun onCancelled(error: DatabaseError) {
                    saveLocationResult.value = SaveLocationResult.FAILURE
                }
            }
        )
    }

    fun showSaveLoading(showLoad: Boolean) {
        saveLocationLoadingVisibility.value = if (showLoad) View.VISIBLE else View.GONE
        saveButtonVisibility.value = if (showLoad) View.GONE else View.VISIBLE
    }

    fun saveLocationClick() {
        saveLocationButtonClicked.value = true
    }

    enum class SaveLocationResult {
        NONE, LOADING, FAILURE, SUCCESS
    }
}