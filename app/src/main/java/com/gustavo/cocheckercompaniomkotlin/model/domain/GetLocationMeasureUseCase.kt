package com.gustavo.cocheckercompaniomkotlin.model.domain

import com.gustavo.cocheckercompaniomkotlin.data.remote.firebase.FirebaseSensorDataSource
import com.gustavo.cocheckercompaniomkotlin.model.data.CustomResult
import com.gustavo.cocheckercompaniomkotlin.model.data.FirebaseResult
import com.gustavo.cocheckercompaniomkotlin.model.data.MeasureItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetLocationMeasureUseCase {
    val firebaseDataSource = FirebaseSensorDataSource()
    var currentLastItem = 0

    suspend fun fetchLocationsMeasureData(sensorUid: String): CustomResult<MutableList<MeasureItem>?> = withContext(
        Dispatchers.IO){
        when(val firebaseResult = firebaseDataSource.getCurrentSensorMeasures(sensorUid,currentLastItem)){
            is FirebaseResult.Cancelled->{
                return@withContext CustomResult.Error(firebaseResult.error.toException())
            }
            is FirebaseResult.Changed->{
                val dataSnapshot = firebaseResult.snapshot
                val list = mutableListOf<MeasureItem>()
                dataSnapshot.children.forEach { dataShot ->
                    dataShot.getValue(MeasureItem::class.java)?.let { sensor ->
                        list.add(sensor)
                    }
                }
                if (list.isNotEmpty()) {
                    currentLastItem += list.size
                    return@withContext CustomResult.Success(list)
                } else {
                    return@withContext CustomResult.Success(null)
                }
            }
        }
    }
}