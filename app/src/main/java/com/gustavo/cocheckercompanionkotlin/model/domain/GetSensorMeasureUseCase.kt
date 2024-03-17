package com.gustavo.cocheckercompanionkotlin.model.domain

import com.gustavo.cocheckercompanionkotlin.data.remote.firebase.FirebaseSensorDataSource
import com.gustavo.cocheckercompanionkotlin.model.data.CustomResult
import com.gustavo.cocheckercompanionkotlin.model.data.FirebaseResult
import com.gustavo.cocheckercompanionkotlin.model.data.MeasureItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetSensorMeasureUseCase() {
    val firebaseDataSource = FirebaseSensorDataSource()
    var currentLastItem:String? = null
    var currentPage = 20


     suspend fun fetchUserSensorsData(sensorUid: String): CustomResult<MutableList<MeasureItem>?> = withContext(Dispatchers.IO){
        when(val firebaseResult = firebaseDataSource.getCurrentSensorMeasures(sensorUid,currentLastItem,currentPage)){
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
                    currentLastItem = list.last().TimeStamp
                    return@withContext CustomResult.Success(list)
                } else {
                    return@withContext CustomResult.Success(null)
                }
            }
        }
    }
}