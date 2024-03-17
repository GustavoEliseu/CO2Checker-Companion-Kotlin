package com.gustavo.cocheckercompanionkotlin.ui.sensor

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.gustavo.cocheckercompanionkotlin.base.BaseActivity
import com.gustavo.cocheckercompanionkotlin.ui.sensor.viewmodel.SensorDetailsViewModel
import com.gustavo.cocheckercompanionkotlin.utils.SENSOR_MAC
import com.gustavo.cocheckercompanionkotlin.utils.extensions.toast
import com.gustavo.cocheckercompanionkotlin.R
import com.gustavo.cocheckercompanionkotlin.databinding.ActivitySensorDetailsBinding
import kotlinx.coroutines.launch

fun Context.sensorDetailsIntent(sensorMac: String): Intent {
    return Intent(this, SensorDetailsActivity::class.java).apply{
        this.putExtra(SENSOR_MAC, sensorMac)
    }
}
class SensorDetailsActivity: BaseActivity<SensorDetailsViewModel>() {
    override val mViewModel: SensorDetailsViewModel by viewModels()
    private var mBinding: ActivitySensorDetailsBinding? = null

    override fun getLayoutId(): Int =  R.layout.activity_sensor_details

    override fun initializeUi() {
        val mac = intent.getStringExtra(SENSOR_MAC)
        mBinding = DataBindingUtil.setContentView(this, getLayoutId())
        mBinding?.viewModel = mViewModel

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