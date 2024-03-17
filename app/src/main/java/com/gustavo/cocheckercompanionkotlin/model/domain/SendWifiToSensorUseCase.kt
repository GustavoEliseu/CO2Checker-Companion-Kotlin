package com.gustavo.cocheckercompanionkotlin.model.domain

import com.gustavo.cocheckercompanionkotlin.data.remote.sensor.SensorRepository
import com.gustavo.cocheckercompanionkotlin.model.data.SimpleLocation
import com.gustavo.cocheckercompanionkotlin.model.data.UserWifi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import javax.inject.Inject

class SendWifiToSensorUseCase @Inject constructor(val sensorRepository: SensorRepository) {

    suspend fun sendWifiDataToSensor(wifi: UserWifi, dateFormatted: String, simpleLocation: SimpleLocation): Flow<Result<ResponseBody>> {
        return flow {
            val response =  sensorRepository.sendWifiData(wifi,dateFormatted,simpleLocation)
            emit(Result.success(response))
        }.catch {
            emit(Result.failure(it))
        }
    }
}