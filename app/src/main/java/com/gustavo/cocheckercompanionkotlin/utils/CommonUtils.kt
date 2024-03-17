package com.gustavo.cocheckercompanionkotlin.utils

import android.os.Handler
import android.os.Looper
import androidx.annotation.StringRes
import com.gustavo.cocheckercompanionkotlin.MyApplication
import com.gustavo.cocheckercompanionkotlin.utils.extensions.isNullOrEmptyOrBlank
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

fun getStringNonNullable(@StringRes resString: Int): String {
    return MyApplication.weakContext?.get()?.getString(resString).getValueOrEmpty()
}

fun getStringWithExtrasNonNullable(@StringRes resString: Int,  vararg formatArgs:Any): String {
    return MyApplication.weakContext?.get()?.getString(resString,formatArgs).getValueOrEmpty()
}

fun String?.getValueOrEmpty(): String {
    return this ?: ""
}

fun getSafeMapUrlString(
    latitude: String?,
    longitude: String?,
    height: Int = 600,
    width: Int = 500,
    zoom: Int = 14
): String? {
    if (latitude.isNullOrEmptyOrBlank() || longitude.isNullOrEmptyOrBlank()) return null
    return MyApplication.weakContext?.get()?.getString(
        R.string.glide_url_request,
        latitude,
        longitude,
        zoom.toString(),
        height.toString(),
        width.toString(),
        latitude,
        longitude,
        BuildConfig.MAPS_API_KEY
    )
}