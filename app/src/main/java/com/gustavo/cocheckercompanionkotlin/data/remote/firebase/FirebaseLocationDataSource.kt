package com.gustavo.cocheckercompanionkotlin.data.remote.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.gustavo.cocheckercompanionkotlin.model.data.FirebaseResult
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseLocationDataSource {
    private var locationsBaseRef: DatabaseReference? = FirebaseDatabaseManager.getFireBaseDatabaseLocations()

    suspend fun getCurrentLocationDetails(locationUid:String): FirebaseResult = suspendCoroutine { continuation ->
        val valueEventListener = object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                continuation.resume(FirebaseResult.Cancelled(error))
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                continuation.resume(FirebaseResult.Changed(snapshot))
            }
        }
        locationsBaseRef?.child(locationUid)?.addListenerForSingleValueEvent(valueEventListener)
    }
//
//    suspend fun getCurrentLocationMeasures(locationUid:String,currentLastItem:String?): FirebaseResult = suspendCoroutine { continuation ->
//        val valueEventListener = object: ValueEventListener {
//            override fun onCancelled(error: DatabaseError) {
//                continuation.resume(FirebaseResult.Cancelled(error))
//            }
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//                continuation.resume(FirebaseResult.Changed(snapshot))
//            }
//        }
//        if(currentLastItem.isNotNullOrNotEmptyOrNotBlank()){
//            locationsBaseRef?.child(locationUid)?.child(MEASURES)?.orderByChild(TIME_STAMP)?.startAt(currentLastItem)?.limitToLast(20)?.addListenerForSingleValueEvent(valueEventListener)
//        }else{
//            locationsBaseRef?.child(locationUid)?.child(MEASURES)?.orderByChild(TIME_STAMP)?.limitToLast(20)?.addListenerForSingleValueEvent(valueEventListener)
//        }
//    }
}