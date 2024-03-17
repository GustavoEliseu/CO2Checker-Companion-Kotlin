package com.gustavo.cocheckercompanionkotlin.data.remote.sensor

import com.gustavo.cocheckercompanionkotlin.model.data.SimpleLocation
import com.gustavo.cocheckercompanionkotlin.model.data.UserWifi
import com.gustavo.cocheckercompanionkotlin.utils.extensions.isNotNullOrNotEmptyOrNotBlank
import okhttp3.ResponseBody
import javax.inject.Inject

class SensorRepository @Inject constructor(
    private val api: Api
) {

    suspend fun sendWifiData(wifi: UserWifi, dateFormatted: String, simpleLocation: SimpleLocation): ResponseBody {
        val password =
            if (wifi.password.isNotNullOrNotEmptyOrNotBlank()) "&password=${wifi.password}" else ""
        val localUUID = if (simpleLocation.locationId.isNotNullOrNotEmptyOrNotBlank())"&localuuid=${simpleLocation.locationId}" else ""
        val local = if(simpleLocation.latitude.isNotNullOrNotEmptyOrNotBlank() && simpleLocation.longitude.isNotNullOrNotEmptyOrNotBlank())
            "&local=${simpleLocation.latitude}"+","+"${simpleLocation.longitude}" else ""
        return api.sendWifiData("/action_new_connection?ssid=${wifi.ssid}" + password + "&time=${dateFormatted}"+local+localUUID)
    }

    suspend fun sendPreviousWifiRequest(): ResponseBody {
        return api.sendWifiData("/action_previous_connection")
    }

    suspend fun checkIfConnected(): ResponseBody {
        return api.getConnectionStatus("/connection_status")
    }
}