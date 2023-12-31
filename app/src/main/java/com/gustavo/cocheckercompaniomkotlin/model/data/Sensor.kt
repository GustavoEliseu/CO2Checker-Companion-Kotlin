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