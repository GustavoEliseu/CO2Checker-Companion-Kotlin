package com.gustavo.cocheckercompaniomkotlin.ui.sensorconfig

import android.Manifest.permission.ACCESS_NETWORK_STATE
import android.Manifest.permission.ACCESS_WIFI_STATE
import android.Manifest.permission.CHANGE_WIFI_STATE
import android.Manifest.permission.INTERNET
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.SpinnerAdapter
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.gustavo.cocheckercompaniomkotlin.base.BaseActivity
import com.gustavo.cocheckercompaniomkotlin.model.data.LocationItemList
import com.gustavo.cocheckercompaniomkotlin.model.data.SensorWifiData
import com.gustavo.cocheckercompaniomkotlin.model.data.SimpleLocation
import com.gustavo.cocheckercompaniomkotlin.model.data.UserWifi
import com.gustavo.cocheckercompaniomkotlin.ui.sensorconfig.viewmodel.SensorConfigViewModel
import com.gustavo.cocheckercompaniomkotlin.ui.sensorconfig.viewmodel.SensorConfigViewModel.SensorConnectionStatus
import com.gustavo.cocheckercompaniomkotlin.utils.ConnectionManager
import com.gustavo.cocheckercompaniomkotlin.utils.INTERNET_PERMISSION_REQUEST
import com.gustavo.cocheckercompaniomkotlin.utils.LOCATION_EXTRA
import com.gustavo.cocheckercompaniomkotlin.utils.LOCATION_PERMISSION_REQUEST
import com.gustavo.cocheckercompaniomkotlin.utils.LoggerUtil
import com.gustavo.cocheckercompaniomkotlin.utils.NetworkListener
import com.gustavo.cocheckercompaniomkotlin.utils.PERMISSION_COARSE_LOCATION
import com.gustavo.cocheckercompaniomkotlin.utils.PERMISSION_FINE_LOCATION
import com.gustavo.cocheckercompaniomkotlin.utils.WIFI_DATA
import com.gustavo.cocheckercompaniomkotlin.utils.extensions.isLegacy
import com.gustavo.cocheckercompaniomkotlin.utils.extensions.isNotNullOrNotEmptyOrNotBlank
import com.gustavo.cocheckercompaniomkotlin.utils.extensions.longToast
import com.gustavo.cocheckercompaniomkotlin.utils.extensions.toast
import com.gustavo.cocheckercompaniomkotlin.utils.safeRun
import com.gustavo.cocheckercompanionkotlin.R
import com.gustavo.cocheckercompanionkotlin.databinding.ActivitySensorConfigBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

fun Context.configSensorIntent(
    newSensorData: SensorWifiData,
    location: Location? = null
): Intent {
    return Intent(this, SensorConfigActivity::class.java).apply {
        this.putExtra(WIFI_DATA, newSensorData)
        this.putExtra(LOCATION_EXTRA, location)
    }
}
@AndroidEntryPoint
class SensorConfigActivity : BaseActivity<SensorConfigViewModel>() {
    override val mViewModel: SensorConfigViewModel by viewModels()
    private var mBinding: ActivitySensorConfigBinding? = null
    private var defaultWifi = ""

    private val connectivityManager by lazy {
        applicationContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val currentLocation by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(LOCATION_EXTRA, Location::class.java)
        } else {
            intent?.getParcelableExtra(LOCATION_EXTRA) as? Location
        }
    }

    private val sensorWifiData by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(WIFI_DATA, SensorWifiData::class.java)
        } else {
            intent.getSerializableExtra(WIFI_DATA) as? SensorWifiData
        }
    }

    private var newLocationUuid =
        UUID.randomUUID().toString();

    private var retryControl = true
    private var correctWifi: Boolean = false
    private var wifiManager: WifiManager? = null
    private var locationManager: LocationManager? = null
    private val myWifiListSpinner = mutableListOf("")
    private val myLocationsList = mutableListOf<LocationItemList>()
    private val myLocationsListSpinner =
        mutableListOf("")

    private var currentSelectedWifi = ""
    private var currentPassword = ""

    private val broadcastReceiver by lazy {
        ConnectionManager.espNetworkAutoConnectionCreater(
            applicationContext,
            sensorWifiData,
            ::checkEspWifi,
            ::isCorrectWifi,
            ::canRetryControl,
            ::tryConnection,
            ::retryNotAvailable,
            ::connectedToCorrectWifi
        )
    }

    override fun getLayoutId(): Int = R.layout.activity_sensor_config

    @SuppressLint("MissingPermission") // Only way to get here is with permissionGranted
    private fun wifiListAdapter(): SpinnerAdapter {
        myWifiListSpinner.clear()
        myWifiListSpinner.addAll(
            mutableListOf(
                getString(R.string.select_wifi),
                getString(R.string.update_wifi)
            )
        )
        val adapter = ArrayAdapter(
            this,
            R.layout.simple_spinner_item,
            myWifiListSpinner
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        wifiManager?.let {
            myWifiListSpinner.addAll(ConnectionManager.getWifi24(it))
            return adapter
        }
        safeRun {
            myWifiListSpinner.addAll(
                ConnectionManager.getWifi24(
                    applicationContext?.getSystemService(
                        Context.WIFI_SERVICE
                    ) as WifiManager
                )
            )
        }
        return adapter
    }

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreateOnly(savedInstanceState)
        mBinding = ActivitySensorConfigBinding.inflate(layoutInflater)
        setContentView(mBinding?.root)
        defaultWifi = getCurrentSSID()
        mBinding?.viewModel = mViewModel
        wifiManager = applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        locationManager = getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        mViewModel.initialize()
        //actionbar
        val actionbar = supportActionBar
        //set back button
        actionbar?.setDisplayHomeAsUpEnabled(true)
        mViewModel.getLocationList()
        askPermissionInternet()
    }

    override fun initializeUi() {
        myLocationsListSpinner.clear()
        myLocationsListSpinner.add(getString(R.string.get_my_location))
        val adapter =
            ArrayAdapter(
                this,
                R.layout.simple_spinner_item,
                myLocationsListSpinner
            )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mBinding?.locationsSpinner?.adapter = adapter
        locationManager = getSystemService(Context.LOCATION_SERVICE) as? LocationManager

        mViewModel.wifiSSIDSpinnerVisibility(spinnerVisible = true)
        val wifiAdapter = wifiListAdapter()
        mBinding?.wifiSpinner?.adapter = wifiAdapter
        onItemsSelected()
        checkEspWifi()
        addObservers()
    }

    override fun onResume() {
        super.onResume()
        safeRun {
            NetworkListener.register(this, broadcastReceiver)
        }
    }

    override fun onDestroy() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
        safeRun {
            NetworkListener.unregister(this, broadcastReceiver)
        }
        removeObservers()
        super.onDestroy()
    }

    private fun removeObservers() {
        mViewModel.mutableSensorConnectionState.removeObservers(this)
        mViewModel.sendWifiMutableResult.removeObservers(this)
        mViewModel.sendDataButtonMutableClickedState.removeObservers(this)
        mViewModel.retryConnectionClickedState.removeObservers(this)
        mViewModel.locationListChangeState.removeObservers(this)
    }

    private fun addObservers() {
        mViewModel.mutableSensorConnectionState.observe(this) {
            when (it) {
                SensorConnectionStatus.UNKNOWN -> {}
                SensorConnectionStatus.CONNECTED -> {
                    toast(R.string.success_change_wifi)
                }

                SensorConnectionStatus.NETWORK_NOT_FOUND -> {
                    toast(R.string.wifi_not_found_esp)
                }

                else -> {
                    toast(R.string.wifi_not_possible_esp)
                    mViewModel.mutableSSIDWarningText.value =
                        getString(R.string.wifi_not_possible_esp_error)
                }
            }
        }
        mViewModel.sendWifiMutableResult.observe(this) {
            when (it) {
                is SensorConfigViewModel.SendWifiResult.SendResultEmpty -> {}
                is SensorConfigViewModel.SendWifiResult.SendResultConnected -> {
                    lifecycleScope.launch {
                        if (it.saveLocationChecked) mViewModel.saveLocation(it.simpleLocation.toNewLocationData())
                        mViewModel.checkIfSensorIsConnected(it.espStatusError)
                    }
                    toast(R.string.success_change_wifi)
                    finish()
                }

                is SensorConfigViewModel.SendWifiResult.SendResultFailure -> {
                    if (it.message == getString(R.string.enonet_exception)) {
                        toast(R.string.wrong_wifi)
                    } else {
                        toast(R.string.send_data_to_sensor_failure)
                    }
                }

                else -> {
                    toast(R.string.send_data_to_sensor_failure)
                }
            }
            mViewModel.buttonLoading(false)
        }
        mViewModel.sendDataButtonMutableClickedState.observe(this) {
            if (it) {
                sendDataClicked()
                mViewModel.sendDataButtonMutableClickedState.value = false
            }
        }
        mViewModel.retryConnectionClickedState.observe(this) {
            if (it) {
                tryConnection()
            }
        }
        mViewModel.locationListChangeState.observe(this) {
            if (it != null) {
                myLocationsList.add(it)
                myLocationsListSpinner.add(it.name)
            }
        }
    }

    private fun sendDataClicked() {
        val ssidText: EditText = findViewById(R.id.ssidText)
        val setPassword: TextInputEditText = findViewById(R.id.setPassword)
        if (mViewModel.isSpinnerVisible()) {
            mBinding?.wifiSpinner?.let {
                val selectedPosition = it.selectedItemPosition
                if (selectedPosition > 0) {
                    val selected = it.selectedItem.toString()
                    currentSelectedWifi = selected
                    currentPassword = setPassword.text.toString()
                    if (currentSelectedWifi.isEmpty()) {
                        toast(R.string.must_have_password)
                    } else {
                        sendWifiData(selected, setPassword.text.toString())
                    }
                    return
                } else{
                    toast(R.string.must_have_password)
                }
            }
            toast(R.string.select_or_type_wifi)
            return
        } else {
            if (ssidText.text.isNullOrEmpty()) {
                toast(R.string.must_have_password)
            } else {
                sendWifiData(ssidText.text.toString(), setPassword.text.toString())
            }
        }
    }

    private fun checkEspWifi() {
        sensorWifiData?.let {
            val ssid = getCurrentSSID()
            correctWifi = ssid == it.ssid
            val errorMessage =
                if (correctWifi) "" else getString(R.string.ssid_error_message, it.ssid)
            mViewModel.setWarningWrongSSID(!correctWifi, errorMessage)
            updateSendDataButton()
            mViewModel.retryConnectionWifiVisibility(!correctWifi)
        }
    }

    private fun getCurrentSSID(): String {
        val wifiManager = getSystemService(WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        return wifiInfo.ssid.replace("\"", "")
    }

    private fun updateSendDataButton() {
        mViewModel.updateBtnTextAndState(
            correctWifi,
            if (correctWifi) getString(R.string.send_to_sensor) else getString(R.string.incorrect_wifi_btn)
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            LoggerUtil.printStackTraceOnlyInDebug(e)
        }
        finish()
        return true
    }

    private fun askPermissionInternet() {
        askPermissions(
            permissions = arrayOf(
                CHANGE_WIFI_STATE,
                ACCESS_WIFI_STATE,
                PERMISSION_FINE_LOCATION,
                PERMISSION_COARSE_LOCATION,
                ACCESS_NETWORK_STATE,
                INTERNET
            ),
            INTERNET_PERMISSION_REQUEST
        )
    }

    override fun onPermissionDenied(permanently: Boolean) {
        if (permanently) longToast(R.string.permissions_internet_location)
        else toast(R.string.permissions_internet_location)
        currentSelectedWifi = ""
        currentPassword = ""
        finish()
    }

    override fun onPermissionGranted(permissions: Array<out String>, requestCode : Int? ) {
        when (requestCode) {
            INTERNET_PERMISSION_REQUEST -> {
                currentSelectedWifi = ""
                currentPassword = ""
                onInternetPermissionGranted()
            }
        }
    }

    private fun onInternetPermissionGranted() {
        initializeUi()
    }

    private fun sendWifiData(wifiSSID: String, wifiPassword: String?) {
        val dateTimeFormat = SimpleDateFormat("ss,mm,HH,u,dd,MM,yyyy", Locale.getDefault())
        val dateFormatted = dateTimeFormat.format(Date(System.currentTimeMillis()))
        val userWifi = UserWifi(wifiSSID, wifiPassword)
        val location = getUserSimpleLocation()
        val espStatusError = getString(R.string.esp8266_status_error)

        if (isValidLocation(location)) {
            mViewModel.buttonLoading(true)
            val checked = mViewModel.saveLocationCheckVisibility.value == View.VISIBLE
            lifecycleScope.launch {
                mViewModel.sendWifiData(userWifi, dateFormatted, location, espStatusError,checked)
            }
        } else {
            val selectedLocationPosition = mBinding?.locationsSpinner?.selectedItemPosition
            val selectedErrorMessage =
                if (selectedLocationPosition == null || selectedLocationPosition == 0)
                    R.string.get_location_error else R.string.broken_location_error
            mViewModel.setWarningLocation(true, getString(selectedErrorMessage))
        }
    }

    private fun isValidLocation(location: SimpleLocation): Boolean {
        return location.latitude.isNotNullOrNotEmptyOrNotBlank() && location.longitude.isNotNullOrNotEmptyOrNotBlank()
    }

    @SuppressLint("MissingPermission") //IT's only possible to reach this location with permission
    private fun getUserSimpleLocation(): SimpleLocation {
        val selectedPosition = mBinding?.locationsSpinner?.selectedItemPosition
        return if (selectedPosition != null && selectedPosition > 0) {
            val selectedLocation = myLocationsList[selectedPosition - 1]
            SimpleLocation(
                selectedLocation.uuid,
                selectedLocation.name,
                selectedLocation.latitude,
                selectedLocation.longitude
            )
        } else {
            val selectedLocation = currentLocation ?: locationManager?.getLastKnownLocation(
                LocationManager.GPS_PROVIDER
            )
            SimpleLocation(
                newLocationUuid,
                selectedLocation?.latitude.toString(),
                selectedLocation?.longitude.toString()
            )
        }
    }

    private fun onItemsSelected() {
        mBinding?.wifiSpinner?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (p2 == 1) {
                        mBinding?.wifiSpinner?.adapter = wifiListAdapter()
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

            }

        mBinding?.locationsSpinner?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    mViewModel.saveLocationCheckVisibility.postValue(if (p2 == 0) View.VISIBLE else View.GONE)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

            }
    }

    private fun tryConnection() {
        retryControl = true
        sensorWifiData?.let { sensor ->
            if (isLegacy()) {
                val success = ConnectionManager.legacyConnectWifi(sensor.ssid, sensor.password)
                if (!success) toast(R.string.wrong_wifi)
            } else {
                ConnectionManager.connectWifi(
                    sensor.ssid,
                    sensor.password,
                    connectivityManager,
                    networkCallback
                )
                retryControl = false
            }
            return
        }
    }

    private fun retryNotAvailable() {
        toast(R.string.sensor_wifi_unnavailable)
        mViewModel.retryConnectionWifiVisibility(showRetry = true)
    }

    private fun connectedToCorrectWifi() {
        mViewModel.retryConnectionWifiVisibility(showRetry = false)
        toast(R.string.wifi_connected)
    }

    private fun isCorrectWifi(): Boolean {
        return correctWifi
    }

    private fun canRetryControl(): Boolean {
        return retryControl
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            connectivityManager.bindProcessToNetwork(network)
            checkEspWifi()
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            toast(R.string.sensor_connection_lost)
            retryControl = true
            checkEspWifi()
        }
    }
}