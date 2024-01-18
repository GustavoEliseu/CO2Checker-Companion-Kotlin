package com.gustavo.cocheckercompaniomkotlin.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gustavo.cocheckercompaniomkotlin.base.BaseActivity
import com.gustavo.cocheckercompaniomkotlin.data.remote.FirebaseDatabaseManager
import com.gustavo.cocheckercompaniomkotlin.model.data.NewSensorData
import com.gustavo.cocheckercompaniomkotlin.ui.location.locationDetailsIntent
import com.gustavo.cocheckercompaniomkotlin.ui.main.custom.NewSensorDialog
import com.gustavo.cocheckercompaniomkotlin.ui.main.sensors.SensorsListFragment
import com.gustavo.cocheckercompaniomkotlin.ui.main.viewmodel.MainViewModel
import com.gustavo.cocheckercompaniomkotlin.ui.sensor.sensorDetailsIntent
import com.gustavo.cocheckercompanionkotlin.R
import com.gustavo.cocheckercompanionkotlin.databinding.ActivityMainBinding

fun Context.mainIntent(): Intent {
    return Intent(this, MainActivity::class.java)
}

class MainActivity : BaseActivity<MainViewModel>() {

    private lateinit var binding: ActivityMainBinding
    override val mViewModel: MainViewModel by viewModels()
    override fun getLayoutId(): Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseDatabaseManager.initializeFirebase()

        val navView: BottomNavigationView = binding.navView

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_sensors, R.id.navigation_dashboard, R.id.navigation_locations
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        initializeUi()
    }

    override fun initializeUi() {
    }

    fun openSensor(deviceMac: String) {
       startActivity(sensorDetailsIntent(deviceMac))
    }

    fun openLocation(locationUid: String, locationName: String) {
        startActivity(locationDetailsIntent(locationUid, locationName))
    }

    fun addSensor(sensor: NewSensorData?){
        val dialog = NewSensorDialog(sensor, ::startQRCodeNewSensor, ::finishAddSensor)
        dialog.show(supportFragmentManager, null)
    }

    private fun startQRCodeNewSensor(sensor: NewSensorData? = null) {
        //startActivityForResult(QRReaderIntent(fromAddSensor = true, sensor), SENSOR_DATA_REQUEST)
    }

    private fun finishAddSensor(sensor: NewSensorData?) {
        sensor?.let {
            mViewModel.finishAddSensor(it)
        }
    }
}