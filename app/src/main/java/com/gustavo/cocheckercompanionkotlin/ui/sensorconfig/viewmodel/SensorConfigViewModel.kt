package com.gustavo.cocheckercompanionkotlin.ui.sensorconfig.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DatabaseError
import com.gustavo.cocheckercompanionkotlin.base.BaseViewModel
import com.gustavo.cocheckercompanionkotlin.model.data.LocationItemList
import com.gustavo.cocheckercompanionkotlin.model.data.NewLocationData
import com.gustavo.cocheckercompanionkotlin.model.data.SimpleLocation
import com.gustavo.cocheckercompanionkotlin.model.data.UserWifi
import com.gustavo.cocheckercompanionkotlin.model.domain.AddLocationUseCase
import com.gustavo.cocheckercompanionkotlin.model.domain.CheckIfSensorConnectionUseCase
import com.gustavo.cocheckercompanionkotlin.model.domain.FetchLocationsUseCase
import com.gustavo.cocheckercompanionkotlin.model.domain.SendWifiToSensorUseCase
import com.gustavo.cocheckercompanionkotlin.ui.newlocation.viewmodel.AddLocationViewModel
import com.gustavo.cocheckercompanionkotlin.utils.LoggerUtil
import com.gustavo.cocheckercompanionkotlin.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SensorConfigViewModel @Inject constructor(
    private val sendWifiToSensorUseCase: SendWifiToSensorUseCase,
    private val checkIfSensorConnectionUseCase: CheckIfSensorConnectionUseCase
) : BaseViewModel() {
    private val getLocationListUseCase = FetchLocationsUseCase()
    private val addLocationUseCase = AddLocationUseCase()

    val mutableSSIDWarningText = MutableLiveData<String>()
    val mutableLocationWarningText = MutableLiveData<String>()
    val mutableSendBtnText = MutableLiveData<String>()
    val mutableButtonEnabled = MutableLiveData<Boolean>()
    val mutableConnectVisibility = MutableLiveData<Int>()
    val mutableReconnectVisibility = MutableLiveData<Int>()
    val mutableLoadingVisibility = MutableLiveData<Int>()
    val mutableTextVisibility = MutableLiveData<Int>()
    val mutableTextLocationVisibility = MutableLiveData<Int>()
    val saveLocationCheckVisibility = MutableLiveData<Int>()
    val wifiEditTextVisibility = MutableLiveData<Int>()
    val wifiSpinnerVisibility = MutableLiveData<Int>()
    val mutableBtnTextColor = MutableLiveData<Int>()


    //Request mutableStates
    val mutableSensorConnectionState = MutableLiveData(SensorConnectionStatus.UNKNOWN)
    val sendWifiMutableResult = MutableLiveData<SendWifiResult>(SendWifiResult.SendResultEmpty(""))
    val saveLocationResult: MutableLiveData<AddLocationViewModel.SaveLocationResult> =
        MutableLiveData(AddLocationViewModel.SaveLocationResult.NONE)

    //Ui MutableStates
    val sendDataButtonMutableClickedState = MutableLiveData(false)
    val retryConnectionClickedState = MutableLiveData(false)
    val locationListChangeState = MutableLiveData<LocationItemList?>(null)

    fun initialize() {
        wifiEditTextVisibility.value = View.GONE
        wifiSpinnerVisibility.value = View.VISIBLE
    }

    suspend fun sendWifiData(
        wifi: UserWifi,
        date: String,
        simpleLocation: SimpleLocation,
        espStatusError: String,
        saveLocationChecked: Boolean = false
    ) {
        sendWifiToSensorUseCase.sendWifiDataToSensor(wifi, date, simpleLocation).collect {
            if (it.isSuccess) {
                sendWifiMutableResult.value = SendWifiResult.SendResultConnected(
                    saveLocationChecked,
                    simpleLocation,
                    espStatusError
                )
                buttonLoading(false)
            } else if (it.isFailure) {
                val exception = it.exceptionOrNull()
                sendWifiMutableResult.value =
                    SendWifiResult.SendResultFailure(exception?.message ?: "")
                buttonLoading(false)
            } else{
                buttonLoading(false)
            }
        }
    }

    suspend fun saveLocation(newLocationData: NewLocationData) {
        addLocationUseCase.addNewLocation(newLocationData, object :
            AddLocationUseCase.UserAddLocationDataListener {
            override fun onChildAdded(locationItemList: NewLocationData) {
                saveLocationResult.value = AddLocationViewModel.SaveLocationResult.SUCCESS
            }

            override fun onCancelled(error: DatabaseError) {
                saveLocationResult.value = AddLocationViewModel.SaveLocationResult.FAILURE
            }
        })
    }

    suspend fun checkIfSensorIsConnected(espStatusError: String) {
        checkIfSensorConnectionUseCase.checkIfSensorIsConnected().collect {
            if (it.isSuccess) {
                val responseBody = it.getOrNull()
                if (responseBody != null) {
                    if (responseBody.toString() == espStatusError) {
                        mutableSensorConnectionState.value =
                            SensorConnectionStatus.NETWORK_NOT_FOUND
                    } else {
                        mutableSensorConnectionState.value = SensorConnectionStatus.CONNECTED
                    }
                } else {
                    sensorConnectedFailure()
                    return@collect
                }
                buttonLoading(false)
            } else {
                sensorConnectedFailure()
                buttonLoading(false)
            }
        }
    }

    fun getLocationList() {
        getLocationListUseCase.fetchUserLocationsData(object :
            FetchLocationsUseCase.UserLocationsDataListener {
            override fun onChildAdded(locationItemList: LocationItemList) {
                locationListChangeState.value = locationItemList
            }

            override fun onChildChanged(locationItemList: LocationItemList) {
            }

            override fun onChildRemoved(locationItemList: LocationItemList) {
            }

            override fun onCancelled(error: DatabaseError) {
                LoggerUtil.printStackTraceOnlyInDebug(error.toException())
            }

        })
    }

    fun wifiSSIDSpinnerVisibility(spinnerVisible: Boolean) {
        wifiEditTextVisibility.value = if (spinnerVisible) View.GONE else View.VISIBLE
        wifiSpinnerVisibility.value = if (spinnerVisible) View.VISIBLE else View.GONE
    }

    fun editButtonOnClick() {
        wifiSSIDSpinnerVisibility(isSpinnerVisible())
    }

    fun isSpinnerVisible(): Boolean {
        return wifiSpinnerVisibility.value == View.VISIBLE
    }

    private fun sensorConnectedFailure() {
        mutableSensorConnectionState.value = SensorConnectionStatus.GENERIC_ERROR
        mutableTextVisibility.value = View.VISIBLE
    }

    fun buttonLoading(loading: Boolean) {
        mutableConnectVisibility.value = if (loading) View.GONE else View.VISIBLE
        mutableLoadingVisibility.value = if (loading) View.VISIBLE else View.GONE
    }

    fun setWarningWrongSSID(showWarning: Boolean, warningMessage: String) {
        mutableSSIDWarningText.postValue(warningMessage)
        mutableTextVisibility.postValue(if (showWarning) View.VISIBLE else View.GONE)
    }

    fun setWarningLocation(showWarning: Boolean, warningMessage: String) {
        mutableLocationWarningText.value = warningMessage
        mutableTextLocationVisibility.value = if (showWarning) View.VISIBLE else View.GONE
    }

    fun updateBtnTextAndState(enabled: Boolean, message: String) {
        mutableButtonEnabled.postValue(enabled)
        mutableSendBtnText.postValue(message)
        mutableBtnTextColor.postValue(if (enabled) R.color.white else R.color.black)
    }

    fun retryConnectionWifiVisibility(showRetry: Boolean) {
        mutableConnectVisibility.postValue(View.GONE)
        mutableReconnectVisibility.postValue(if (showRetry) View.VISIBLE else View.GONE)
        mutableConnectVisibility.postValue(if (showRetry) View.GONE else View.VISIBLE)
    }

    fun sendDataClick() {
        sendDataButtonMutableClickedState.value = true
    }

    fun retryConnectionClick() {
        retryConnectionClickedState.value = true
    }

    enum class SensorConnectionStatus {
        UNKNOWN, CONNECTED, GENERIC_ERROR, NETWORK_NOT_FOUND;
    }

    sealed class SendWifiResult {
        data class SendResultEmpty(val message: String) : SendWifiResult()
        data class SendResultConnected(
            val saveLocationChecked: Boolean,
            val simpleLocation: SimpleLocation,
            val espStatusError: String
        ) : SendWifiResult() {}

        data class SendResultFailure(val message: String) :
            SendWifiResult()
    }
}