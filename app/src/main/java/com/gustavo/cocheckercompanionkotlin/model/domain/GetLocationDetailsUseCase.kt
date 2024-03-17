package com.gustavo.cocheckercompanionkotlin.model.domain

import com.gustavo.cocheckercompanionkotlin.data.remote.firebase.FirebaseLocationDataSource
import com.gustavo.cocheckercompanionkotlin.model.data.CustomResult
import com.gustavo.cocheckercompanionkotlin.model.data.FirebaseResult
import com.gustavo.cocheckercompanionkotlin.model.data.LocationItemList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetLocationDetailsUseCase {
    val firebaseLocationDataSource = FirebaseLocationDataSource()


    suspend fun fetchUserLocationDetailsData(locationUid: String): CustomResult<LocationItemList?> = withContext(
        Dispatchers.IO){
        when(val firebaseResult = firebaseLocationDataSource.getCurrentLocationDetails(locationUid)){
            is FirebaseResult.Cancelled->{
                return@withContext CustomResult.Error(firebaseResult.error.toException())
            }
            is FirebaseResult.Changed->{
                val dataSnapshot = firebaseResult.snapshot

                val result = dataSnapshot.getValue(LocationItemList::class.java)
                if (result!= null) {
                    result.uuid = locationUid
                    return@withContext CustomResult.Success(result)
                } else {
                    return@withContext CustomResult.Success(null)
                }
            }
        }
    }
}