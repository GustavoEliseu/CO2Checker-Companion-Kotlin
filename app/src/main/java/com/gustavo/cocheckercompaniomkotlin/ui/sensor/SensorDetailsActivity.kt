package com.gustavo.cocheckercompaniomkotlin.ui.sensor

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.gustavo.cocheckercompaniomkotlin.base.BaseActivity
import com.gustavo.cocheckercompaniomkotlin.ui.sensor.viewmodel.SensorDetailsViewModel
import com.gustavo.cocheckercompaniomkotlin.utils.SENSOR_MAC
import com.gustavo.cocheckercompaniomkotlin.utils.extensions.toast
import com.gustavo.cocheckercompanionkotlin.R
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

fun Context.sensorDetailsIntent(sensorMac: String): Intent {
    return Intent(this, SensorDetailsActivity::class.java).apply{
        this.putExtra(SENSOR_MAC, sensorMac)
    }
}
class SensorDetailsActivity: BaseActivity<SensorDetailsViewModel>() {
    override val mViewModel: SensorDetailsViewModel by viewModels()

    override fun getLayoutId(): Int =  R.layout.activity_sensor_details

    override fun initializeUi() {
        val mac = intent.getStringExtra(SENSOR_MAC)

        if(mac == null){
            toast("Não foi possível identificar o sensor no servidor, tente novamente.")
            finish()
        }

        mViewModel.toastMessageState.observe(this){
            if(it!= null){
                toast(it)
                mViewModel.toastMessageState.value = null
            }
        }

        lifecycleScope.launch {
            mac?.let {
            mViewModel.getMeasures(it)
            }
        }
    }

    override fun onDestroy() {
        mViewModel.toastMessageState.removeObservers(this)
        super.onDestroy()
    }
}