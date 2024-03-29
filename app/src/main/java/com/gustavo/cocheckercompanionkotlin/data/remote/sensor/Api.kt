package com.gustavo.cocheckercompanionkotlin.data.remote.sensor

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface Api {

    @POST
    suspend fun sendWifiData(
        @Url url: String
    ): ResponseBody

    @GET
    suspend fun getConnectionStatus(
        @Url url: String
    ): ResponseBody
}