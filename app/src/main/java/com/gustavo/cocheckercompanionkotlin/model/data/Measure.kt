package com.gustavo.cocheckercompanionkotlin.model.data

import com.gustavo.cocheckercompanionkotlin.utils.QualityEnum

data class MeasureItem(
    val CO2:Double,
    val TimeStamp: String,
    val Time: String,
    val Date: String,
    val CO: Double,
    val Temperature: Double,
    val locationLongitude: String?,
    val locationLatitude: String?,
    val Humidity: Double,
    val locationUuid: String?,
    val locationName: String?,
    var expanded: Boolean = false,
    var quality: QualityEnum? = null
): GenericData(){
    constructor() : this(0.0, "","","",0.0,0.0,"","",0.0, "", null,false,null)
}