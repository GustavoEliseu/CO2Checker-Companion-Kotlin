package com.gustavo.cocheckercompaniomkotlin.model.domain

import com.gustavo.cocheckercompaniomkotlin.data.remote.firebase.FirebaseLocationDataSource
import com.gustavo.cocheckercompaniomkotlin.data.remote.firebase.FirebaseSensorDataSource
import com.gustavo.cocheckercompaniomkotlin.data.remote.firebase.FirebaseUserLocationDataSource
import com.gustavo.cocheckercompaniomkotlin.model.data.CustomResult
import com.gustavo.cocheckercompaniomkotlin.model.data.FirebaseResult
import com.gustavo.cocheckercompaniomkotlin.model.data.LocationItemList
import com.gustavo.cocheckercompaniomkotlin.model.data.MeasureItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetLocationDetailsUseCase {
    val firebaseLocationDataSource = FirebaseLocationDataSource()


    suspend fun fetchUserLocationDetailsData(sensorUid: String): CustomResult<LocationItemList?> = withContext(
        Dispatchers.IO){
        when(val firebaseResult = firebaseLocationDataSource.getCurrentLocationDetails(sensorUid)){
            is FirebaseResult.Cancelled->{
                return@withContext CustomResult.Error(firebaseResult.error.toException())
            }
            is FirebaseResult.Changed->{
                val dataSnapshot = firebaseResult.snapshot

                val result = dataSnapshot.getValue(LocationItemList::class.java)
                if (result!= null) {
                    return@withContext CustomResult.Success(result)
                } else {
                    return@withContext CustomResult.Success(null)
                }
            }
        }
    }
}