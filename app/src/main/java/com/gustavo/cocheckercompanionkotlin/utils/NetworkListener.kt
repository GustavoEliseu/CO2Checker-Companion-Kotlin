package com.gustavo.cocheckercompanionkotlin.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities


object NetworkListener {
    fun create(onNetworkUp: () -> Unit, onNetworkDown: () -> Unit): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val cm =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                if (isNetworkAvailable(cm)) {
                    onNetworkUp()
                } else {
                    onNetworkDown()
                }
            }
        }
    }

    private fun isNetworkAvailable(cm: ConnectivityManager?): Boolean {
        return hasAnyNetworkAvailable(cm)
    }

    private fun hasAnyNetworkAvailable(cm: ConnectivityManager?): Boolean {
        return try {
            val currentActiveNetwork = when {
                cm?.activeNetwork != null -> cm.activeNetwork
                cm?.allNetworks?.isNotEmpty() == true -> cm.allNetworks.first()
                else -> null
            }

            val networkCapabilities =
                cm?.getNetworkCapabilities(currentActiveNetwork)?.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_INTERNET
                )
            networkCapabilities ?: false
        } catch (e: Exception) {
            LoggerUtil.printStackTraceOnlyInDebug(e)
            true
        }
    }

    fun register(context: Context, broadcastReceiver: BroadcastReceiver) {
        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(broadcastReceiver, intentFilter)
    }

    fun unregister(context: Context, broadcastReceiver: BroadcastReceiver) {
        context.unregisterReceiver(broadcastReceiver)
    }

}