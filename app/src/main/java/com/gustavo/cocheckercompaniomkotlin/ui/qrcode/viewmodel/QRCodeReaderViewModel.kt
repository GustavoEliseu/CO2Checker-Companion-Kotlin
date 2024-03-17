package com.gustavo.cocheckercompaniomkotlin.ui.qrcode.viewmodel

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DatabaseError
import com.gustavo.cocheckercompaniomkotlin.base.BaseViewModel
import com.gustavo.cocheckercompaniomkotlin.model.data.ScannedSensorResult
import com.gustavo.cocheckercompaniomkotlin.model.data.SensorWifiData
import com.gustavo.cocheckercompaniomkotlin.model.domain.FetchSensorsUseCase
import com.gustavo.cocheckercompaniomkotlin.model.domain.SensorExistsUseCase
import com.gustavo.cocheckercompaniomkotlin.utils.LoggerUtil
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