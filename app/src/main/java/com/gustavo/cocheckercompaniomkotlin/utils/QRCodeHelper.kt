package com.gustavo.cocheckercompaniomkotlin.utils

import com.gustavo.cocheckercompaniomkotlin.model.data.SensorWifiData

class QRCodeHelper {
    companion object{
        fun getNewEspWifi(json: String): SensorWifiData? {
            return JsonHelper.getObjectFromJson<SensorWifiData>(json)
        }
    }
}