package com.gustavo.cocheckercompaniomkotlin.ui.main.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.gustavo.cocheckercompaniomkotlin.base.BaseViewModel
import com.gustavo.cocheckercompaniomkotlin.model.SensorItemList
import com.gustavo.cocheckercompaniomkotlin.ui.main.adapters.SensorListAdapter
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class MainViewModel:BaseViewModel() {
    var auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance()
    val mySensorListAdapter: SensorListAdapter =
        SensorListAdapter(::onClickSensor)

    val emptySensorListMessageVisibility = MutableLiveData<Int>()
    val emptyLocationsListMessageVisibility = MutableLiveData<Int>()

    private fun onClickSensor(sensorMac: String) {
        openSensor(sensorMac)
    }

    private fun onClickLocation(locationUuid: String,locationName: String) {
        openLocation(locationUuid, locationName)
    }

    override fun initialize() {
        auth.currentUser?.let {
            val usersRef = database.getReference("Users").child(it.uid)
            usersRef.keepSynced(true)
            usersRef.orderByValue().limitToLast(20)
            val espRef = database.getReference("Users").child("SavedSensors")

            espRef.orderByValue().limitToLast(20)
                .addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChild: String?) {
                        emptySensorListMessageVisibility.postValue(View.GONE)
                    }

                    override fun onChildChanged(
                        snapshot: DataSnapshot,
                        previousChildName: String?
                    ) {
                        snapshot.children.forEach { dataShot ->
                            dataShot.getValue(SensorItemList::class.java)?.let { sensor ->
                                mySensorListAdapter.removeSensor(sensor)
                            }
                        }
                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {
                        snapshot.children.forEach { dataShot ->
                            dataShot.getValue(SensorItemList::class.java)?.let { sensor ->
                                mySensorListAdapter.removeSensor(sensor)
                            }
                        }
                        if (mySensorListAdapter.itemCount == 0) {
                            emptySensorListMessageVisibility.postValue(View.VISIBLE)
                        }
                    }

                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })

        }
    }
}