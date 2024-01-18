package com.gustavo.cocheckercompaniomkotlin.ui.sensor

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.viewModels
import com.gustavo.cocheckercompaniomkotlin.base.BaseActivity
import com.gustavo.cocheckercompaniomkotlin.ui.location.viewmodel.LocationDetailsViewModel
import com.gustavo.cocheckercompaniomkotlin.utils.SENSOR_MAC
import com.gustavo.cocheckercompanionkotlin.R

fun Context.sensorDetailsIntent(sensorMac: String): Intent {
    val intent = Intent(this, SensorDetailsActivity::class.java)
    intent.putExtra(SENSOR_MAC, sensorMac)
    return intent
}
class SensorDetailsActivity: BaseActivity<LocationDetailsViewModel>() {
    override val mViewModel: LocationDetailsViewModel by viewModels()

    override fun getLayoutId(): Int =  R.layout.activity_sensor_details

    override fun initializeUi() {
        val mac = intent.getStringExtra(SENSOR_MAC)

        if(mac != null){
            Toast.makeText(this,"Sucesso ${mac}",Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this,"Ocorreu um erro",Toast.LENGTH_SHORT).show()
        }
    }
}