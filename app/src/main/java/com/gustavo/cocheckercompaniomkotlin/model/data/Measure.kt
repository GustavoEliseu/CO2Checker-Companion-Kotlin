package com.gustavo.cocheckercompaniomkotlin.model.data

data class MeasureItem(val CO2:Double?): GenericData(){
    constructor() : this(0.0)
}