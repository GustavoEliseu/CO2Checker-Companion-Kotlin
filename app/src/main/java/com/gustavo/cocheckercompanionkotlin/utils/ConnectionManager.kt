package com.gustavo.cocheckercompanionkotlin.utils

import android.Manifest.permission
import android.content.BroadcastReceiver
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.SupplicantState
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import com.gustavo.cocheckercompanionkotlin.MyApplication
import com.gustavo.cocheckercompanionkotlin.model.data.SensorWifiData
import com.gustavo.cocheckercompanionkotlin.utils.extensions.isNotNullOrNotEmptyOrNotBlank

object ConnectionManager {

    @Suppress("DEPRECATION")
    fun espNetworkAutoConnectionCreater(
        applicationContext: Context,
        sensorWifiData: SensorWifiData?,
        checkEspWifi: () -> Unit,
        isCorrectWifi: () -> Boolean,
        getRetryControl: () -> Boolean,
        tryConnection: () -> Unit,
        retryNotAvailable: () -> Unit,
        connectedToCorrectWifi: () -> Unit
    ): BroadcastReceiver {
        return NetworkListener.create({
            val wifiManager =
                applicationContext.getSystemService(AppCompatActivity.WIFI_SERVICE) as WifiManager
            val wifiInfo: WifiInfo = wifiManager.connectionInfo
            if (wifiInfo.supplicantState == SupplicantState.COMPLETED) {
                sensorWifiData?.let {
                    checkEspWifi()
                    if (!isCorrectWifi()) {
                        if (getRetryControl()) {
                            tryConnection()
                        } else {
                            retryNotAvailable()
                        }
                    }
                    if (isCorrectWifi()) {
                        connectedToCorrectWifi()
                    }
                }
            }
        }, {
            checkEspWifi()
        })
    }

    @RequiresPermission(allOf = [permission.ACCESS_WIFI_STATE, permission.ACCESS_FINE_LOCATION])
    fun getWifi24(wifiManager: WifiManager): ArrayList<String> {
        val results = wifiManager.scanResults
        val wifi24Only = ArrayList<String>()
        for (wifi in results) {
            if (wifi.frequency in 2400..2500 && wifi.SSID.isNotNullOrNotEmptyOrNotBlank()) {
                wifi24Only.add(wifi.SSID)
            }
        }
        return wifi24Only
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun connectWifi(ssid: String, password: String, connectivityManager: ConnectivityManager, networkCallback: ConnectivityManager.NetworkCallback) {
        val wifiNetworkSpecifier = WifiNetworkSpecifier.Builder()
            .setSsid(ssid)
            .setWpa2Passphrase(password)
            .build()

        val networkRequestBuilder1: NetworkRequest.Builder = NetworkRequest.Builder()
        networkRequestBuilder1.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        networkRequestBuilder1.setNetworkSpecifier(wifiNetworkSpecifier)

        val nr: NetworkRequest = networkRequestBuilder1.build()
        connectivityManager.requestNetwork(nr, networkCallback)
    }

    @Suppress("DEPRECATION")
    fun legacyConnectWifi(ssid: String, password: String): Boolean {
        val wifiConfig = WifiConfiguration()
        wifiConfig.SSID = String.format("\"%s\"", ssid)
        wifiConfig.preSharedKey = String.format("\"%s\"", password)
        val wifiManager = MyApplication.weakContext?.get()?.getSystemService(AppCompatActivity.WIFI_SERVICE) as WifiManager
        val netId = wifiManager.addNetwork(wifiConfig)
        wifiManager.disconnect()
        wifiManager.enableNetwork(netId, true)
        return wifiManager.reconnect()
    }
}