package com.gustavo.cocheckercompaniomkotlin.model.data

import com.squareup.moshi.Json
import java.io.Serializable

data class LocationItemList(
    @Json(name = "UUID")
    var uuid: String,
    @Json(name = "Name")
    var name: String,
    var uri: String,
    var lastMeasure: LastMeasure? = null,
    @Json(name = "Latitude")
    val latitude: String?,
    @Json(name = "Longitude")
    val longitude: String?
) : Serializable{
    constructor() : this("", "", "", null, null, null)
}