package com.gustavo.cocheckercompanionkotlin.utils

import com.gustavo.cocheckercompanionkotlin.model.data.SensorWifiData

class QRCodeHelper {
    companion object{
        fun getNewEspWifi(json: String): SensorWifiData? {
            return JsonHelper.getObjectFromJson<SensorWifiData>(json)
        }
    }
}