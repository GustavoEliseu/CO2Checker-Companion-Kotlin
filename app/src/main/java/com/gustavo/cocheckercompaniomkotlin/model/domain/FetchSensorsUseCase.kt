package com.gustavo.cocheckercompaniomkotlin.model.domain

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.gustavo.cocheckercompaniomkotlin.data.remote.FirebaseSensorsDataSource
import com.gustavo.cocheckercompaniomkotlin.model.data.SensorItemList
import com.gustavo.cocheckercompaniomkotlin.utils.SAVED_DEVICES

class FetchSensorsUseCase {

    private val firebaseDataSource = FirebaseSensorsDataSource()
    val database = FirebaseDatabase.getInstance()
    val currentSensorsPage = 20

    interface UserSensorsDataListener {
        fun onChildAdded(sensor: SensorItemList)
        fun onChildChanged(sensor: SensorItemList)
        fun onChildRemoved(sensor: SensorItemList)
        fun onCancelled(error: DatabaseError)
    }

    fun fetchUserData(userListener: UserSensorsDataListener){
        firebaseDataSource.fetchUserData {usersRef->
            val espRef = usersRef.child(SAVED_DEVICES)
            espRef.orderByValue().limitToLast(currentSensorsPage)
                .addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChild: String?) {
                        val sensor = snapshot.getValue(SensorItemList::class.java)
                        sensor?.let { userListener.onChildAdded(it) }
                    }

                    override fun onChildChanged(
                        snapshot: DataSnapshot,
                        previousChildName: String?
                    ) {
                        val sensor = snapshot.getValue(SensorItemList::class.java)
                        sensor?.let { userListener.onChildChanged(it) }

                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {
                        val sensor = snapshot.getValue(SensorItemList::class.java)
                        sensor?.let { userListener.onChildRemoved(it) }

                    }

                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    }

                    override fun onCancelled(error: DatabaseError) {
                        userListener.onCancelled(error)
                    }
                })
        }
    }
}