package com.gustavo.cocheckercompaniomkotlin.utils

import androidx.compose.ui.graphics.Color
import com.gustavo.cocheckercompanionkotlin.R

interface DefaultEnum {
    val nameId: Int
}

interface ColorEnum: DefaultEnum{
    val tintColor: Color
}

enum class QualityEnum(val value:Int): ColorEnum{
    UNKNOWN(-1){
        override val nameId = R.string.unknown_quality
        override val tintColor = Color.Black
    },
    EXTREMLY_BAD(0){
        override val nameId = R.string.extremly_bad
        override val tintColor = Color(195,0,0)
                   },
    VERY_BAD(2){
        override val nameId = R.string.very_bad
        override val tintColor = Color(254,1,1)
    },
    BAD(2){
        override val nameId = R.string.bad
        override val tintColor = Color(195,0,0)
    },
    DECENT(3){
        override val nameId = R.string.decent
        override val tintColor = Color.Yellow
    },
    GOOD(4){
        override val nameId = R.string.good
        override val tintColor = Color.Green
    },
}
enum class SensorOptions : DefaultEnum{
    ADD_SENSOR{
        override val nameId = R.string.add_sensor
    },
    VIEW_SENSOR{
        override val nameId = R.string.view_sensor
    },
    CONFIGURE_SENSOR{
        override val nameId = R.string.configure_sensor
    };

    companion object {
        fun array(alreadyAdded: Boolean, failure: Boolean = false): Array<SensorOptions> {
            val options = mutableListOf(CONFIGURE_SENSOR, ADD_SENSOR, VIEW_SENSOR)
            if (alreadyAdded || failure) options.remove(ADD_SENSOR)
            if (!alreadyAdded || failure) options.remove(VIEW_SENSOR)
            return options.toTypedArray()
        }
    }
}