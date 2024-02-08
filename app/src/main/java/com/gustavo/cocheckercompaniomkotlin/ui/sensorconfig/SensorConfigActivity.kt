package com.gustavo.cocheckercompaniomkotlin.ui.sensorconfig

import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.activity.viewModels
import com.gustavo.cocheckercompaniomkotlin.base.BaseActivity
import com.gustavo.cocheckercompaniomkotlin.model.data.SensorWifiData
import com.gustavo.cocheckercompaniomkotlin.ui.sensorconfig.viewmodel.SensorConfigViewModel
import com.gustavo.cocheckercompaniomkotlin.utils.LOCATION_EXTRA
import com.gustavo.cocheckercompaniomkotlin.utils.WIFI_DATA

fun Context.configSensorIntent(
    newSensorData: SensorWifiData,
    location: Location? = null
): Intent {
    return Intent(this, SensorConfigActivity::class.java).apply {
        this.putExtra(WIFI_DATA, newSensorData)
        this.putExtra(LOCATION_EXTRA, location)
    }
}
class SensorConfigActivity: BaseActivity<SensorConfigViewModel>() {
    override val mViewModel: SensorConfigViewModel by viewModels()
    override fun getLayoutId(): Int {
        TODO("Not yet implemented")
    }

    override fun initializeUi() {
        TODO("Not yet implemented")
    }
}