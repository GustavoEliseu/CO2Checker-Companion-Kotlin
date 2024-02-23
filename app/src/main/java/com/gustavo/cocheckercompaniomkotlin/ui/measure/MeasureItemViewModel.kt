package com.gustavo.cocheckercompaniomkotlin.ui.measure

import androidx.lifecycle.MutableLiveData
import com.gustavo.cocheckercompaniomkotlin.model.data.MeasureItem

class MeasureItemViewModel {
    val mutableMeasureUID : MutableLiveData<String?> = MutableLiveData(null)
    fun bind(measure: MeasureItem){
        mutableMeasureUID.value = measure.CO2.toString()
    }
}