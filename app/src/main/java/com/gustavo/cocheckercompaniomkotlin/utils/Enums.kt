package com.gustavo.cocheckercompaniomkotlin.utils

import com.gustavo.cocheckercompanionkotlin.R

interface DefaultEnum {
    val nameId: Int
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