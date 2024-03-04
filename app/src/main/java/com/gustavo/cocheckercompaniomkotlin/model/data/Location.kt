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
    val longitude: String?,
    @Json(name = "Measures")
    val Measures: HashMap<String,MeasureItem>?
) : Serializable{
    constructor() : this("", "", "", null, null, null, null)
}
data class NewLocationData(
    @Json(name = "UUID")
    var uuid: String,
    @Json(name = "Name")
    var Name: String? = null,
    var Latitude: String? = null,
    var Longitude: String? = null,
    var Owners: String? = null
) : Serializable, GenericData()

data class SimpleLocation(val locationId: String,val locationName:String, val longitude: String? = null, val latitude: String? = null){
        fun toNewLocationData(): NewLocationData{
            return NewLocationData(locationId,locationName,latitude,longitude)
        }
}