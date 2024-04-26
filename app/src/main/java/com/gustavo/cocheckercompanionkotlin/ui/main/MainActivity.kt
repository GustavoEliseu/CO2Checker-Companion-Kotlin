package com.gustavo.cocheckercompanionkotlin.ui.main

import android.Manifest.permission
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresPermission
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gustavo.cocheckercompanionkotlin.data.remote.firebase.FirebaseDatabaseManager
import com.gustavo.cocheckercompanionkotlin.model.data.NewLocationData
import com.gustavo.cocheckercompanionkotlin.model.data.NewSensorData
import com.gustavo.cocheckercompanionkotlin.ui.location.locationDetailsIntent
import com.gustavo.cocheckercompanionkotlin.ui.main.custom.NewSensorDialog
import com.gustavo.cocheckercompanionkotlin.ui.main.viewmodel.MainViewModel
import com.gustavo.cocheckercompanionkotlin.ui.newlocation.configNewLocationIntent
import com.gustavo.cocheckercompanionkotlin.ui.qrcode.QRReaderIntent
import com.gustavo.cocheckercompanionkotlin.ui.sensor.sensorDetailsIntent
import com.gustavo.cocheckercompanionkotlin.utils.EDIT_SENSOR_DATA_RESULT
import com.gustavo.cocheckercompanionkotlin.utils.PERMISSION_COARSE_LOCATION
import com.gustavo.cocheckercompanionkotlin.utils.PERMISSION_FINE_LOCATION
import com.gustavo.cocheckercompanionkotlin.utils.LOCATION_PERMISSION_REQUEST
import com.gustavo.cocheckercompanionkotlin.utils.NEW_SENSOR_DATA_RESULT
import com.gustavo.cocheckercompanionkotlin.utils.WIFI_DATA
import com.gustavo.cocheckercompanionkotlin.utils.extensions.longToast
import com.gustavo.cocheckercompanionkotlin.utils.extensions.toast
import com.gustavo.cocheckercompanionkotlin.R
import com.gustavo.cocheckercompanionkotlin.base.BaseActivity
import com.gustavo.cocheckercompanionkotlin.databinding.ActivityMainBinding
import com.gustavo.cocheckercompanionkotlin.utils.extensions.getSerializable
import java.util.UUID


fun Context.mainIntent(): Intent {
    return Intent(this, MainActivity::class.java)
}

@Suppress("DEPRECATION")
class MainActivity : BaseActivity<MainViewModel>(),
    LocationListener {

    private lateinit var mBinding: ActivityMainBinding

    private var dialog = NewSensorDialog(NewSensorData(), ::startQRCodeNewSensor, ::finishAddSensor)
    override val mViewModel: MainViewModel by viewModels()
    override fun getLayoutId(): Int = R.layout.activity_main

    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            NEW_SENSOR_DATA_RESULT -> {
                val mySensorData: NewSensorData? = getSerializable(result.data, WIFI_DATA, NewSensorData::class.java)
                Toast.makeText(this, mySensorData?.mac, Toast.LENGTH_SHORT).show()
            }

            EDIT_SENSOR_DATA_RESULT -> {
                val mySensorData: NewSensorData? =
                    getSerializable(result.data, WIFI_DATA,NewSensorData::class.java)
                dialog.updateValuesFromQR(mySensorData)
            }
        }
    }

    var requestResult: () -> Unit? = ::blank
    private var locationManager: LocationManager? = null
    private var currentLocation: Location? = null
    private var tempLocationData: NewLocationData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        FirebaseDatabaseManager.initializeFirebase()
        val navView: BottomNavigationView = mBinding.navContainer.navView
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_sensors, R.id.navigation_locations
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        mBinding.navContainer.QrFABButton.setOnClickListener {
            startForResult.launch(QRReaderIntent(fromAddSensor = false, null))
        }
    }

    override fun initializeUi() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    }

    fun openSensor(deviceMac: String) {
        startActivity(sensorDetailsIntent(deviceMac))
    }

    fun openLocation(locationUid: String, locationName: String, locationMapUri: String?) {
        startActivity(locationDetailsIntent(locationUid, locationName, locationMapUri))
    }

    fun addLocation() {
        tempLocationData = NewLocationData(uuid = UUID.randomUUID().toString())
        val permissionsArray = arrayOf(
            PERMISSION_COARSE_LOCATION,
            PERMISSION_FINE_LOCATION
        )
        requestResult = ::finishAddLocation
        askPermissions(permissionsArray)
    }

    @RequiresPermission(anyOf = [permission.ACCESS_COARSE_LOCATION, permission.ACCESS_FINE_LOCATION])
    private fun finishAddLocation() {
        getLocation()
        if (currentLocation != null) {
            startActivity(configNewLocationIntent(currentLocation))
        } else {
            toast("Não foi possível detectar sua localização")
        }
        requestResult = ::blank
    }

    private fun blank(){}

    @RequiresPermission(anyOf = [permission.ACCESS_COARSE_LOCATION, permission.ACCESS_FINE_LOCATION])
    private fun getLocation() {
        if (locationManager == null) {
            initializeUi()
        }
        currentLocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
    }

    fun addSensor(sensor: NewSensorData?) {
        dialog = NewSensorDialog(sensor, ::startQRCodeNewSensor, ::finishAddSensor)
        dialog.show(supportFragmentManager, null)
    }

    private fun startQRCodeNewSensor(sensor: NewSensorData? = null) {
        startForResult.launch(QRReaderIntent(fromAddSensor = true, sensor))
    }

    private fun finishAddSensor(sensor: NewSensorData?) {
        sensor?.let {
            mViewModel.finishAddSensor(it)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var anyPermissionGranted = false
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            grantResults.forEachIndexed { index, result ->
                if (result == PackageManager.PERMISSION_GRANTED) {
                    anyPermissionGranted = true
                }
            }
            if (anyPermissionGranted) {
                onPermissionGranted(permissions)
            } else {
                grantResults.forEachIndexed { index, result ->
                    if (shouldShowRequestPermissionRationale(permissions[index])) {
                        onPermissionDenied()
                    } else {
                        onPermissionDenied(permanently = true)
                    }
                }
            }
        }
    }

    override fun onPermissionDenied(permanently: Boolean) {
        if (permanently) longToast(R.string.permissions_location_settings)
        else toast(R.string.permissions_location)
    }

    override fun onPermissionGranted(permissions: Array<out String>, requestCode: Int?) {
        requestResult()
    }

    override fun onLocationChanged(location: Location) {
        currentLocation = location
    }
}