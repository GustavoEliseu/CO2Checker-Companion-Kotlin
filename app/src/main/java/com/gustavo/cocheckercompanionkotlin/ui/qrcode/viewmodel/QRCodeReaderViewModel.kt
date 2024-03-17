package com.gustavo.cocheckercompanionkotlin.ui.qrcode.viewmodel

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DatabaseError
import com.gustavo.cocheckercompanionkotlin.base.BaseViewModel
import com.gustavo.cocheckercompanionkotlin.model.data.ScannedSensorResult
import com.gustavo.cocheckercompanionkotlin.model.data.SensorWifiData
import com.gustavo.cocheckercompanionkotlin.model.domain.SensorExistsUseCase
import com.gustavo.cocheckercompanionkotlin.utils.LoggerUtil
import javax.inject.Inject

class QRCodeReaderViewModel @Inject constructor() : BaseViewModel() {

    val scannedQRcodeState : MutableLiveData<ScannedSensorResult?> = MutableLiveData(null)
    private val checkSensorsUseCase = SensorExistsUseCase()

    fun isSensorRegistered(sensor: SensorWifiData){
        checkSensorsUseCase.checkIfSensorAlreadyRegistered(sensor,object :
            SensorExistsUseCase.SensorExistsDataListener {
            override fun onItemExists(sensor: SensorWifiData, exists: Boolean) {
                scannedQRcodeState.value = ScannedSensorResult(sensor,exists, false)
            }

            override fun onCancelled(error: DatabaseError) {
                LoggerUtil.printStackTraceOnlyInDebug(error.toException())
                scannedQRcodeState.value = ScannedSensorResult(sensor,
                    exists = false,
                    failure = false
                )
            }
        })
    }
}