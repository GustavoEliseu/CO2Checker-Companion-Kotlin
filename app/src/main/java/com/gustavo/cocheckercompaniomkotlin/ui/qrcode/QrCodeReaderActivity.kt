package com.gustavo.cocheckercompaniomkotlin.ui.qrcode

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.google.zxing.client.android.BeepManager
import com.google.zxing.client.android.Intents
import com.google.zxing.integration.android.IntentIntegrator
import com.gustavo.cocheckercompaniomkotlin.base.BaseActivity
import com.gustavo.cocheckercompaniomkotlin.model.data.NewSensorData
import com.gustavo.cocheckercompaniomkotlin.model.data.SensorWifiData
import com.gustavo.cocheckercompaniomkotlin.ui.custom.MyViewFinderView
import com.gustavo.cocheckercompaniomkotlin.ui.qrcode.viewmodel.QRCodeReaderViewModel
import com.gustavo.cocheckercompaniomkotlin.ui.sensor.sensorDetailsIntent
import com.gustavo.cocheckercompaniomkotlin.utils.ADD_SENSOR
import com.gustavo.cocheckercompaniomkotlin.utils.LOCATION_EXTRA
import com.gustavo.cocheckercompaniomkotlin.utils.PERMISSION_CAMERA
import com.gustavo.cocheckercompaniomkotlin.utils.QRCodeHelper
import com.gustavo.cocheckercompaniomkotlin.utils.SENSOR_DATA_RESULT
import com.gustavo.cocheckercompaniomkotlin.utils.SensorOptions
import com.gustavo.cocheckercompaniomkotlin.utils.WIFI_DATA
import com.gustavo.cocheckercompaniomkotlin.utils.extensions.getDialog
import com.gustavo.cocheckercompaniomkotlin.utils.extensions.longToast
import com.gustavo.cocheckercompaniomkotlin.utils.extensions.toast
import com.gustavo.cocheckercompaniomkotlin.utils.safeRun
import com.gustavo.cocheckercompanionkotlin.R
import com.gustavo.cocheckercompanionkotlin.databinding.ActivityQrReaderBinding
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
    private var auditionInstructions: AppCompatTextView? = null
    private var pauseCamera = false
    private var beepManager: BeepManager? = null
    private var dialog: AlertDialog? = null
    private var controller: Boolean = false

    var sensorData: NewSensorData? = null

    private val mySensorData by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(WIFI_DATA, NewSensorData::class.java)
        } else{
        intent?.getSerializableExtra(WIFI_DATA) as? NewSensorData }}
    private val fromAddSensor by lazy { intent?.getBooleanExtra(ADD_SENSOR, false) ?: false }
    private val currentLocation by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(LOCATION_EXTRA,Location::class.java )
        }else{intent?.getParcelableExtra(LOCATION_EXTRA) as? Location
        } }

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
        mBinding = DataBindingUtil.setContentView(this, getLayoutId())
        initScan()
    }

    private fun initScan() {
        mBinding.barcodeScanner.findViewById<AppCompatImageView>(R.id.close)?.setOnClickListener { finish() }
        imageMask = findViewById(R.id.cameraMask)
        auditionInstructions = findViewById(R.id.auditionInstructions)
        updateMaskPosition()
        val formats: Collection<BarcodeFormat> = listOf(BarcodeFormat.QR_CODE)
        mBinding.barcodeScanner.barcodeView?.decoderFactory = DefaultDecoderFactory(formats)
        intent.putExtra(Intents.Scan.SCAN_TYPE, Intents.Scan.MIXED_SCAN)
        mBinding.barcodeScanner.initializeFromIntent(intent)
        mBinding.barcodeScanner.decodeContinuous(callback)

        mBinding.barcodeScanner.resume()

        mViewModel.scannedQRcodeState.observe(this) {
            if(it != null){
                openEspDialog(it.sensorWifiData,it.exists,it.failure)
            }
        }
    }

    private fun updateMaskPosition() {
        (mBinding.barcodeScanner.viewFinder as? MyViewFinderView)?.setCameraMaskPaddingTop(0F)
        (mBinding.barcodeScanner.viewFinder as? MyViewFinderView)?.setCameraMaskPaddingBottom(0F)
        (imageMask?.layoutParams as? FrameLayout.LayoutParams)?.setMargins(0, 0, 0, 0)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        sensorData = mySensorData
        if (sensorData == null) {
            sensorData = NewSensorData()
        }
        if (result != null) {
            if (result.contents == null) {
                toast(R.string.canceled)
            } else {
                startActivityFromEsp(result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onPermissionDenied(permanently: Boolean) {
        if (permanently) longToast(R.string.permissions_camera_settings)
        else toast(R.string.permissions_camera)
        finish()
    }

    override fun onPermissionGranted(permissions: Array<out String>) {
        initializeUi()
    }

    fun startActivityFromEsp(contents: String) {
        val json = QRCodeHelper.getNewEspWifi(contents)

        json?.let {
            if (!controller) {
                beepManager?.playBeepSoundAndVibrate()
                pause()
                mViewModel.isSensorRegistered(it)
                return
            }
        }
    }

    fun openEspDialog(sensorWifiData: SensorWifiData, exists: Boolean, failure: Boolean) {
        // open options
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
                            //startActivity(configSensorIntent(sensorWifiData, currentLocation))
                            toast("detectou")
                        }
                    }
                    SensorOptions.ADD_SENSOR -> {
                        setResultAndLeave(sensorWifiData.toNewSensorData())
                    }
                    SensorOptions.VIEW_SENSOR -> {
                        safeRun {
                            startActivity(sensorDetailsIntent(sensorWifiData.mac))
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
        setResult(SENSOR_DATA_RESULT, intent)
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