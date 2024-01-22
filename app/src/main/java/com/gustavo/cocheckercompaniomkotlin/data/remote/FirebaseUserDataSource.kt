package com.gustavo.cocheckercompaniomkotlin.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.gustavo.cocheckercompaniomkotlin.model.data.LocationItemList
import com.gustavo.cocheckercompaniomkotlin.model.data.NewSensorData
import com.gustavo.cocheckercompaniomkotlin.model.data.SensorItemList
import com.gustavo.cocheckercompaniomkotlin.model.domain.AddSensorUseCase
import com.gustavo.cocheckercompaniomkotlin.model.domain.FetchLocationsUseCase
import com.gustavo.cocheckercompaniomkotlin.model.domain.FetchSensorsUseCase
import com.gustavo.cocheckercompaniomkotlin.utils.DEVICES
import com.gustavo.cocheckercompaniomkotlin.utils.LOCATIONS
import com.gustavo.cocheckercompaniomkotlin.utils.OWNERS
import com.gustavo.cocheckercompaniomkotlin.utils.SAVED_DEVICES
import com.gustavo.cocheckercompaniomkotlin.utils.SAVED_LOCATIONS
import com.gustavo.cocheckercompanionkotlin.R


class FirebaseUserDataSource {
    private var userRef: DatabaseReference? = FirebaseDatabaseManager.getCurrentUserReference()
    private var sensorsRef: DatabaseReference? = FirebaseDatabaseManager.getFireBaseDatabaseSensors()

    fun fetchUserData(onDataFetched: (DatabaseReference) -> Unit) {
        if (userRef == null) {
            userRef = FirebaseDatabaseManager.getCurrentUserReference()
        }
        userRef?.let {
            onDataFetched(it)
        }
        if (userRef == null) {
            //TODO - (IMPLEMENT NULL SAFETY CASE)
        }
    }

    fun fetchUserDataInitialSensors(result: (List<SensorItemList>?) -> Unit) {
        fetchUserData { user ->
            val espRef = user.child(SAVED_DEVICES)
            espRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val list = mutableListOf<SensorItemList>()
                    dataSnapshot.children.forEach { dataShot ->
                        dataShot.getValue(SensorItemList::class.java)?.let { sensor ->
                            list.add(sensor)
                        }
                    }

                    if (list.isNotEmpty()) {
                        result(list)
                    } else {
                        result(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    result(null)
                }
            })
        }
    }

    fun fetchUserDataSensors(
        currentSensorsPage: Int = 20,
        userListener: FetchSensorsUseCase.UserSensorsDataListener
    ) {
        fetchUserData { user ->
            val espRef = user.child(SAVED_DEVICES)
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

    fun fetchUserDataInitialLocations(result: (List<LocationItemList>?) -> Unit) {
        fetchUserData { user ->
            val locationRef = user.child(SAVED_LOCATIONS)
            locationRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val list = mutableListOf<LocationItemList>()
                    dataSnapshot.children.forEach { dataShot ->
                        dataShot.getValue(LocationItemList::class.java)?.let { sensor ->
                            list.add(sensor)
                        }
                    }

                    if (list.isNotEmpty()) {
                        result(list)
                    } else {
                        result(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    result(null)
                }
            })
        }
    }

    fun fetchUserDataLocations(
        currentLocationsPage: Int,
        userLocationListener: FetchLocationsUseCase.UserLocationsDataListener
    ) {
        fetchUserData { user ->
            val locationRef = user.child(SAVED_LOCATIONS)
            locationRef.orderByValue().limitToLast(currentLocationsPage)
                .addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChild: String?) {
                        val location = snapshot.getValue(LocationItemList::class.java)
                        location?.let { userLocationListener.onChildAdded(it) }
                    }

                    override fun onChildChanged(
                        snapshot: DataSnapshot,
                        previousChildName: String?
                    ) {
                        val location = snapshot.getValue(LocationItemList::class.java)
                        location?.let { userLocationListener.onChildChanged(it) }

                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {
                        val location = snapshot.getValue(LocationItemList::class.java)
                        location?.let { userLocationListener.onChildRemoved(it) }
                    }

                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    }

                    override fun onCancelled(error: DatabaseError) {
                        userLocationListener.onCancelled(error)
                    }
                })
        }
    }

    fun addSensorToUser(sensor: NewSensorData,addSensorListener: AddSensorUseCase.UserAddSensorDataListener){
        sensorsRef?.child(sensor.mac)
                ?.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            createSensorOwners(sensor,addSensorListener)
                        } else {
                            executeCreationSensor(sensor,addSensorListener)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        addSensorListener.onCancelled(error)
                    }
                })
    }

    fun createSensorOwners(sensor: NewSensorData,addSensorListener: AddSensorUseCase.UserAddSensorDataListener) {
        FirebaseAuth.getInstance().currentUser?.uid?.let { uid ->
            sensor.mac.let { myMac ->
                sensorsRef?.child(myMac)?.child(OWNERS)
                    ?.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val oldValue = snapshot.value as String
                                val newValue = "$oldValue,${uid}"
                                if (!oldValue.contains(uid)) {
                                    addSensorNewOwner(sensor, newValue,addSensorListener)
                                } else {
                                    addUserSavedSensor(sensor,addSensorListener)
                                }
                            } else {
                                addSensorFirstOwner(sensor,addSensorListener)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            addSensorListener.onCancelled(error)
                        }
                    })
            }
        }
    }

    fun addSensorNewOwner(sensor: NewSensorData, newValue: String,addSensorListener: AddSensorUseCase.UserAddSensorDataListener) {
        sensorsRef?.child(sensor.mac)?.updateChildren(mapOf(Pair(OWNERS, newValue)))?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                addUserSavedSensor(sensor,addSensorListener)
            }
        }
    }

    private fun addUserSavedSensor(sensor: NewSensorData,addSensorListener: AddSensorUseCase.UserAddSensorDataListener) {
        FirebaseAuth.getInstance().currentUser?.uid?.let { uid ->
            userRef?.child(uid)
                ?.child(SAVED_DEVICES)?.child(sensor.mac)?.setValue(sensor) { error, ref ->
                    if (error != null) {
                        addSensorListener.onCancelled(error)
                    } else {
                        addSensorListener.onChildAdded(sensor)
                    }
                }
        }
    }

    fun addSensorFirstOwner(sensor: NewSensorData,addSensorListener: AddSensorUseCase.UserAddSensorDataListener) {
        FirebaseAuth.getInstance().currentUser?.uid?.let {  userId->
                sensorsRef?.child(sensor.mac)?.child(OWNERS)?.setValue(userId)?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            addUserSavedSensor(sensor,addSensorListener)
                        }
                    }
        }
    }
    fun executeCreationSensor(sensor: NewSensorData,addSensorListener: AddSensorUseCase.UserAddSensorDataListener) {
            sensorsRef?.child(sensor.mac)?.setValue(sensor)?.addOnCompleteListener {
                if (it.isSuccessful) {
                    createSensorOwners(sensor,addSensorListener)
                }
            }
    }
}