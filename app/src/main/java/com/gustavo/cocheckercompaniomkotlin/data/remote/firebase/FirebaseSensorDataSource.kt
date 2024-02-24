package com.gustavo.cocheckercompaniomkotlin.data.remote.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.gustavo.cocheckercompaniomkotlin.model.data.FirebaseResult
import com.gustavo.cocheckercompaniomkotlin.utils.MEASURES
import com.gustavo.cocheckercompaniomkotlin.utils.TIME_STAMP
import com.gustavo.cocheckercompaniomkotlin.utils.extensions.isNotNullOrNotEmptyOrNotBlank
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseSensorDataSource {
    private var devicesBaseRef: DatabaseReference? = FirebaseDatabaseManager.getFireBaseDatabaseSensors()

    suspend fun getCurrentSensorMeasures(mac:String,currentLastItem:String?,currentPage: Int): FirebaseResult = suspendCoroutine { continuation ->
        val valueEventListener = object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                continuation.resume(FirebaseResult.Cancelled(error))
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                continuation.resume(FirebaseResult.Changed(snapshot))
            }
        }
        val query = devicesBaseRef?.child(mac)?.child(MEASURES)?.orderByChild(TIME_STAMP)
        if(currentLastItem.isNotNullOrNotEmptyOrNotBlank()){
            query?.startAt(currentLastItem)?.addListenerForSingleValueEvent(valueEventListener)
        }else{
            query?.addListenerForSingleValueEvent(valueEventListener)
        }
    }
}