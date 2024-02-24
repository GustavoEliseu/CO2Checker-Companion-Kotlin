package com.gustavo.cocheckercompaniomkotlin.model.data

data class MeasureItem(
    val CO2:Double?,
    val TimeStamp: String,
    val Time: String,
    val Date: String,
    val CO: Double?,
    val Temperature: Double?,
    val locationLongitude: String?,
    val locationLatitude: String?,
    val Humidity: Double?,
    val locationUuid: String?
): GenericData(){
    constructor() : this(0.0, "","","",0.0,0.0,"","",0.0, "")
}