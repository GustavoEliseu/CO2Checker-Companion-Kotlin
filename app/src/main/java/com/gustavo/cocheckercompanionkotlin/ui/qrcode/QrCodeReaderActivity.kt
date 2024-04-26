package com.gustavo.cocheckercompanionkotlin.ui.qrcode

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.IntentCompat
import androidx.databinding.DataBindingUtil
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.google.zxing.client.android.BeepManager
import com.google.zxing.client.android.Intents
import com.gustavo.cocheckercompanionkotlin.R
import com.gustavo.cocheckercompanionkotlin.base.BaseActivity
import com.gustavo.cocheckercompanionkotlin.databinding.ActivityQrReaderBinding
import com.gustavo.cocheckercompanionkotlin.model.data.NewSensorData
import com.gustavo.cocheckercompanionkotlin.model.data.SensorWifiData
import com.gustavo.cocheckercompanionkotlin.ui.custom.MyViewFinderView
import com.gustavo.cocheckercompanionkotlin.ui.qrcode.viewmodel.QRCodeReaderViewModel
import com.gustavo.cocheckercompanionkotlin.ui.sensor.sensorDetailsIntent
import com.gustavo.cocheckercompanionkotlin.ui.sensorconfig.configSensorIntent
import com.gustavo.cocheckercompanionkotlin.utils.ADD_SENSOR
import com.gustavo.cocheckercompanionkotlin.utils.EDIT_SENSOR_DATA_RESULT
import com.gustavo.cocheckercompanionkotlin.utils.LOCATION_EXTRA
import com.gustavo.cocheckercompanionkotlin.utils.NEW_SENSOR_DATA_RESULT
import com.gustavo.cocheckercompanionkotlin.utils.PERMISSION_CAMERA
import com.gustavo.cocheckercompanionkotlin.utils.QRCodeHelper
import com.gustavo.cocheckercompanionkotlin.utils.SensorOptions
import com.gustavo.cocheckercompanionkotlin.utils.WIFI_DATA
import com.gustavo.cocheckercompanionkotlin.utils.extensions.getDialog
import com.gustavo.cocheckercompanionkotlin.utils.extensions.longToast
import com.gustavo.cocheckercompanionkotlin.utils.extensions.toast
import com.gustavo.cocheckercompanionkotlin.utils.safeRun
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DefaultDecoderFactory

fun Context.QRReaderIntent(
    fromAddSensor: Boolean = false,
    newSensorData: NewSensorData? = null,
    location: Location? = null
): Intent {
    return Intent(this, QrCodeReaderActivity::class.java).apply {
        this.putExtra(WIFI_DATA, newSensorData)
        this.putExtra(ADD_SENSOR, fromAddSensor)
        this.putExtra(LOCATION_EXTRA, location)
    }
}

class QrCodeReaderActivity : BaseActivity<QRCodeReaderViewModel>() {
    override val mViewModel: QRCodeReaderViewModel by viewModels()
    private lateinit var mBinding: ActivityQrReaderBinding
    private var imageMask: ImageView? = null
    private var instructions: AppCompatTextView? = null
    private var pauseCamera = false
    private var beepManager: BeepManager? = null

    var sensorData: NewSensorData? = null

    private val fromAddSensor by lazy { intent?.getBooleanExtra(ADD_SENSOR, false) ?: false }

    private val currentLocation by lazy {
        IntentCompat.getParcelableExtra(
            intent,
            LOCATION_EXTRA,
            Location::class.java
        )
    }

    private val mySensorData by lazy {
        IntentCompat.getParcelableExtra(
            intent,
            WIFI_DATA,
            NewSensorData::class.java
        )
    }

    private val callback: BarcodeCallback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            if (pauseCamera) return
            val espWifiData = QRCodeHelper.getNewEspWifi(result.text)

            espWifiData?.let {
                beepManager?.playBeepSoundAndVibrate()
                pause()
                if (fromAddSensor) {
                    if (sensorData == null) {
                        sensorData = NewSensorData()
                    }
                    sensorData?.mac = it.mac
                    sensorData?.ssid = it.ssid
                    sensorData?.password = it.password
                    setResultAndLeave(sensorData)
                    return
                } else {
                    mViewModel.isSensorRegistered(it)
                    return
                }
            }

            toast(R.string.invalid_qr_code)
            beepManager?.playBeepSoundAndVibrate()
        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
    }

    override fun getLayoutId(): Int = R.layout.activity_qr_reader

    override fun initializeUi() {
        beepManager = BeepManager(this)
        initScan()
    }

    private fun initScan() {
        mBinding.barcodeScanner.findViewById<AppCompatImageView>(R.id.close)
            ?.setOnClickListener { finish() }
        imageMask = findViewById(R.id.cameraMask)
        instructions = findViewById(R.id.Instructions)
        updateMaskPosition()
        val formats: Collection<BarcodeFormat> = listOf(BarcodeFormat.QR_CODE)
        mBinding.barcodeScanner.barcodeView?.decoderFactory = DefaultDecoderFactory(formats)
        intent.putExtra(Intents.Scan.SCAN_TYPE, Intents.Scan.MIXED_SCAN)
        mBinding.barcodeScanner.initializeFromIntent(intent)
        mBinding.barcodeScanner.decodeContinuous(callback)

        mBinding.barcodeScanner.resume()

        mViewModel.scannedQRcodeState.observe(this) {
            if (it != null) {
                openEspDialog(it.sensorWifiData, it.exists, it.failure)
            }
        }
    }

    private fun updateMaskPosition() {
        (mBinding.barcodeScanner.viewFinder as? MyViewFinderView)?.setCameraMaskPaddingTop(0F)
        (mBinding.barcodeScanner.viewFinder as? MyViewFinderView)?.setCameraMaskPaddingBottom(0F)
        (imageMask?.layoutParams as? FrameLayout.LayoutParams)?.setMargins(0, 0, 0, 0)
    }

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreateOnly(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, getLayoutId())
        setContentView(mBinding.root)
        mBinding.viewModel = mViewModel
        sensorData = mySensorData
        if (sensorData == null) {
            sensorData = NewSensorData()
        }
        askPermissions(permissions = arrayOf(PERMISSION_CAMERA))
    }

    override fun onStart() {
        super.onStart()
        resume()
    }

    override fun onStop() {
        super.onStop()
        pause()
    }

    override fun onPermissionDenied(permanently: Boolean) {
        if (permanently) longToast(R.string.permissions_camera_settings)
        else toast(R.string.permissions_camera)
        finish()
    }

    override fun onPermissionGranted(permissions: Array<out String>, requestCode: Int?) {
        initializeUi()
    }

    fun openEspDialog(sensorWifiData: SensorWifiData, exists: Boolean, failure: Boolean) {
        val dialog = getDialog(
            titleString = getString(R.string.sensor_options),
            elements = SensorOptions.array(
                alreadyAdded = exists,
                failure = failure
            ),
            response = {
                when (it) {
                    SensorOptions.CONFIGURE_SENSOR -> {
                        safeRun {
                            startActivity(configSensorIntent(sensorWifiData, currentLocation))
                            finish()
                        }
                    }

                    SensorOptions.ADD_SENSOR -> {
                        setResultAndLeave(sensorWifiData.toNewSensorData())
                    }

                    SensorOptions.VIEW_SENSOR -> {
                        safeRun {
                            startActivity(sensorDetailsIntent(sensorWifiData.mac))
                            finish()
                        }
                    }
                }
            },
            onCancel = {
                resume()
            }
        )
        dialog.setOnDismissListener {
            resume()
        }
        dialog.show()
        return
    }

    fun setResultAndLeave(mySensorData: NewSensorData?) {
        val intent = Intent()
        intent.putExtra(WIFI_DATA, mySensorData)
        setResult(if (fromAddSensor) EDIT_SENSOR_DATA_RESULT else NEW_SENSOR_DATA_RESULT, intent)
        finish()
    }

    fun pause() {
        mBinding.barcodeScanner.pause()
        pauseCamera = true
    }

    private fun resume() {
        mBinding.barcodeScanner.resume()
        pauseCamera = false
    }
}