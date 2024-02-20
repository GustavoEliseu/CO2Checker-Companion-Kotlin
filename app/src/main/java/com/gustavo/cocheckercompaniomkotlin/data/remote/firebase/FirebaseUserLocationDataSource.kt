package com.gustavo.cocheckercompaniomkotlin.data.remote.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.gustavo.cocheckercompaniomkotlin.model.data.NewLocationData
import com.gustavo.cocheckercompaniomkotlin.model.domain.AddLocationUseCase
import com.gustavo.cocheckercompaniomkotlin.utils.OWNERS
import com.gustavo.cocheckercompaniomkotlin.utils.SAVED_LOCATIONS

class FirebaseUserLocationDataSource {
    val firebaseUserDataSource = FirebaseUserDataSource()
    private var locationsRef: DatabaseReference? =
        FirebaseDatabaseManager.getFireBaseDatabaseLocations()

    fun createLocation(
        locationData: NewLocationData,
        userAddLocationListener: AddLocationUseCase.UserAddLocationDataListener
    ) {
        firebaseUserDataSource.fetchUserData {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            userId?.let {
                locationData.Owners = userId
                locationData.uuid.let {
                    locationsRef?.child(it)
                        ?.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    createLocationOwners(
                                        locationData,
                                        userId,
                                        userAddLocationListener
                                    )
                                } else {
                                    executeCreationLocation(
                                        locationData,
                                        userId,
                                        userAddLocationListener
                                    )
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                userAddLocationListener.onCancelled(error)
                            }
                        })
                }
            }
        }

    }

    fun createLocationOwners(
        locationData: NewLocationData,
        userId: String,
        userAddLocationListener: AddLocationUseCase.UserAddLocationDataListener
    ) {
        locationsRef?.child(locationData.uuid)?.child(OWNERS)
            ?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val oldValue = snapshot.value as String
                        if (!oldValue.contains(userId)) {
                            val newValue = "$oldValue,${userId}"
                            addNewOwnerLocation(locationData, newValue, userAddLocationListener)
                        } else {
                            addUserSavedLocation(locationData, userAddLocationListener)
                        }
                    } else {
                        addFirstOwnerLocation(locationData, userId, userAddLocationListener)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    userAddLocationListener.onCancelled(error)
                }
            })
    }

    fun executeCreationLocation(
        location: NewLocationData,
        userId: String,
        userAddLocationListener: AddLocationUseCase.UserAddLocationDataListener
    ) {
        locationsRef?.child(location.uuid)?.setValue(location)?.addOnCompleteListener {
            if (it.isSuccessful) {
                createLocationOwners(location, userId, userAddLocationListener)
            }
        }
    }

    fun addNewOwnerLocation(
        location: NewLocationData, newValue: String,
        userAddLocationListener: AddLocationUseCase.UserAddLocationDataListener
    ) {
        locationsRef?.child(location.uuid)
            ?.updateChildren(mapOf(Pair(OWNERS, newValue)))?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    addUserSavedLocation(location, userAddLocationListener)
                }
            }
    }

    fun addFirstOwnerLocation(
        location: NewLocationData, userId: String,
        userAddLocationListener: AddLocationUseCase.UserAddLocationDataListener
    ) {
        locationsRef?.child(location.uuid)?.child(OWNERS)?.setValue(userId)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    addUserSavedLocation(location, userAddLocationListener)
                }
            }


    }

    private fun addUserSavedLocation(
        location: NewLocationData,
        userAddLocationListener: AddLocationUseCase.UserAddLocationDataListener
    ) {
        firebaseUserDataSource.fetchUserData { userRef ->
            userRef.child(SAVED_LOCATIONS).child(location.uuid).setValue(location) { error, ref ->
                if (error != null) {
                    userAddLocationListener.onCancelled(error)
                } else {
                    userAddLocationListener.onChildAdded(location)
                }
            }
        }
    }
}