package com.gustavo.cocheckercompaniomkotlin.model.data

import java.io.Serializable

data class SensorItemList(
    var mac: String,
    var name: String,
    var LastLocation: LastMeasure? = null
) : Serializable {
    constructor() : this("", "", null)
}
data class SimpleSensor(val mac: String)

data class NewSensorData(
    var mac: String = "",
    var name: String? = null,
    var ssid: String? = null,
    var password: String? = null
) : Serializable {
    fun toSensorItemList(): SensorItemList {
        return SensorItemList(mac = mac.toString(), name = name.toString(), LastLocation = null)
    }
}

data class SensorWifiData(var ssid: String, val mac: String, val password: String) : Serializable {
    fun toNewSensorData(): NewSensorData {
        return NewSensorData(ssid, mac)
    }

    companion object {
        fun defaultTest(): SensorWifiData {
            return SensorWifiData("ESP8266 Access Point", "5c:cf:7f:68:f5:6a", "thereisnospoon")
        }
    }
}

data class ScannedSensorResult(val sensorWifiData: SensorWifiData, val exists: Boolean, val failure:Boolean= false)

data class LastMeasure(
    val locationLatitude: String? = null,
    val locationLongitude: String? = null,
    val locationName: String? = null,
    val Time: String? = null,
    val date: String? = null,
    val Temperature: Long? = null,
    val Humidity: Long? = null,
    val CO: Double? = null,
    val CO2: Double? = null,
    val locationUuid: String? = null
) : Serializable {
    constructor() : this(null)
}