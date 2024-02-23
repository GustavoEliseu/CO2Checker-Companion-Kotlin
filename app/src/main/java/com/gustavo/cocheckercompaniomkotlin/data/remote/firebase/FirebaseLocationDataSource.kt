package com.gustavo.cocheckercompaniomkotlin.data.remote.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.gustavo.cocheckercompaniomkotlin.model.data.FirebaseResult
import com.gustavo.cocheckercompaniomkotlin.utils.MEASURES
import com.gustavo.cocheckercompaniomkotlin.utils.TIMEDATE
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseLocationDataSource {
    private var locationsBaseRef: DatabaseReference? = FirebaseDatabaseManager.getFireBaseDatabaseSensors()

    suspend fun getCurrentLocationDetails(locationUid:String): FirebaseResult = suspendCoroutine { continuation ->
        val valueEventListener = object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                continuation.resume(FirebaseResult.Cancelled(error))
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                continuation.resume(FirebaseResult.Changed(snapshot))
            }
        }
        locationsBaseRef?.child(locationUid)?.orderByChild(TIMEDATE)?.limitToFirst(1)?.addListenerForSingleValueEvent(valueEventListener)
    }
    suspend fun getCurrentLocationMeasures(locationUid:String,currentLastItem:Int): FirebaseResult = suspendCoroutine { continuation ->
        val valueEventListener = object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                continuation.resume(FirebaseResult.Cancelled(error))
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                continuation.resume(FirebaseResult.Changed(snapshot))
            }
        }
        locationsBaseRef?.child(locationUid)?.child(MEASURES)?.orderByChild(TIMEDATE)?.limitToFirst(currentLastItem)?.limitToLast(currentLastItem+20)?.addListenerForSingleValueEvent(valueEventListener)
    }
}