package com.gustavo.cocheckercompaniomkotlin.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.gustavo.cocheckercompanionkotlin.BuildConfig
import com.gustavo.cocheckercompanionkotlin.R

fun safeRun(run: () -> Unit) {
    try {
        run()
    } catch (e: Exception) {
        LoggerUtil.printStackTraceOnlyInDebug(e)
    }
}

fun runOnUiThread(r: () -> Unit) {
    Handler(Looper.getMainLooper()).post(r)
}

fun safeRunOnUiThread(r: () -> Unit) {
    runOnUiThread(r = {
        safeRun(r)
    })
}

fun getSafeMapUrlString(context: Context, latitude: String?, longitude: String?, height: Int=600, width: Int=500, zoom:Int = 14): String? {
    if(latitude.isNullOrEmptyOrBlank() || longitude.isNullOrEmptyOrBlank())  return null
    return context.getString(R.string.glide_url_request, latitude, longitude,zoom.toString(),height.toString(),width.toString(), latitude, longitude, BuildConfig.MAPS_API_KEY)
}