package com.gustavo.cocheckercompanionkotlin.model.domain

import com.gustavo.cocheckercompanionkotlin.data.remote.sensor.SensorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import javax.inject.Inject

class CheckIfSensorConnectionUseCase @Inject constructor(val sensorRepository: SensorRepository) {

    suspend fun checkIfSensorIsConnected(): Flow<Result<ResponseBody>> {
        return flow {
            val response =  sensorRepository.checkIfConnected()
            emit(Result.success(response))
        }.catch {
            emit(Result.failure(it))
        }
    }
}