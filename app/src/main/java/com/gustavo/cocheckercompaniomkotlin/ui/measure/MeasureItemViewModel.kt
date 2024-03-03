package com.gustavo.cocheckercompaniomkotlin.ui.measure

import android.view.View
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.MutableLiveData
import com.gustavo.cocheckercompaniomkotlin.model.data.MeasureItem
import com.gustavo.cocheckercompaniomkotlin.utils.QualityEnum
import com.gustavo.cocheckercompaniomkotlin.utils.extensions.isNotNullOrNotEmptyOrNotBlank

class MeasureItemViewModel {
    val locationName : MutableLiveData<String?> = MutableLiveData("")
    val locationNameVisibility : MutableLiveData<Int> = MutableLiveData(View.GONE)
    val mutableMeasureTime : MutableLiveData<String?> = MutableLiveData(null)
    val mutableCOText : MutableLiveData<String?> = MutableLiveData(null)
    val mutableCO2Text : MutableLiveData<String?> = MutableLiveData(null)
    val mutableTemperatureText : MutableLiveData<String?> = MutableLiveData(null)

    val mutableExpandedVisibility: MutableLiveData<Int> = MutableLiveData(View.GONE)

    val mutableMeasureQualityTextReference : MutableLiveData<Int?> = MutableLiveData(null)
    val mutableMeasureQualityTextColor: MutableLiveData<Color?> = MutableLiveData(null)

    val mutableCOTextColor: MutableLiveData<Color?> = MutableLiveData(null)
    val mutableCO2TextColor: MutableLiveData<Color?> = MutableLiveData(null)
    val mutableMeasureDate : MutableLiveData<String?> = MutableLiveData(null)

    fun bind(measure: MeasureItem){
        mutableExpandedVisibility.value = if(measure.expanded) View.VISIBLE else View.GONE
        if(measure.locationName.isNotNullOrNotEmptyOrNotBlank()){
            locationName.value = measure.locationName
            locationNameVisibility.value = View.VISIBLE
        }else {
            locationNameVisibility.value = View.GONE
        }
        mutableMeasureDate.value = measure.Date
        mutableMeasureTime.value = measure.Time
        val COQuality = checkCOQuality(measure.CO)
        mutableCOText.value = measure.CO.toString()
        mutableCOTextColor.value = COQuality.tintColor
        mutableCO2Text.value = measure.CO2.toString()
        mutableCO2TextColor.value = COQuality.tintColor
        mutableTemperatureText.value = measure.Temperature.toString()
        val quality = measure.quality ?: getQualityMeasure(measure)
        quality.let {
        mutableMeasureQualityTextReference.value = it.nameId
        mutableMeasureQualityTextColor.value = it.tintColor
        }
    }

    private fun getQualityMeasure(measure: MeasureItem): QualityEnum{
        val totalQuality = mutableListOf<QualityEnum>()
        totalQuality.add(checkCOQuality(measure.CO))
        totalQuality.add(checkCO2Quality(measure.CO2))
        totalQuality.sortBy { it.value }

        return totalQuality.first()
    }

    private fun checkCOQuality(co:Double):QualityEnum{
        return when(co){
            in (0.0)..<(9.0) ->{QualityEnum.GOOD}
            in (9.0)..<(11.0) ->{QualityEnum.DECENT}
            in (11.0)..<(13.0) ->{QualityEnum.BAD}
            in (13.0)..<(15.0) ->{QualityEnum.VERY_BAD}
            in (15.0)..< (Double.MAX_VALUE) ->{QualityEnum.EXTREMLY_BAD}
            else->{QualityEnum.UNKNOWN}
        }
    }
    private fun checkCO2Quality(co2:Double):QualityEnum {
        return when(co2){
            in (0.0)..<(600.0) ->{QualityEnum.GOOD}
            in (600.0)..<(800.0) ->{QualityEnum.DECENT}
            in (800.0)..<(1000.0) ->{QualityEnum.BAD}
            in (1000.0)..<(5000.0) ->{QualityEnum.VERY_BAD}
            in (6000.0)..< (30000.0) ->{QualityEnum.EXTREMLY_BAD}
            else->{QualityEnum.UNKNOWN}
        }
    }
}