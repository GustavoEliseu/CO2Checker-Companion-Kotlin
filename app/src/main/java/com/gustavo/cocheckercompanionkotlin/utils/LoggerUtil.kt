package com.gustavo.cocheckercompanionkotlin.utils

import com.gustavo.cocheckercompanionkotlin.BuildConfig
object LoggerUtil {

    fun printStackTraceOnlyInDebug(throwable: Throwable){
        if(BuildConfig.DEBUG){
            throwable.printStackTrace()
        }
    }

    fun printlnOnlyInDebug(message: String){
        if(BuildConfig.DEBUG){
            println(message)
        }
    }
}