package com.gustavo.cocheckercompanionkotlin.ui.main.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DatabaseError
import com.gustavo.cocheckercompanionkotlin.base.BaseViewModel
import com.gustavo.cocheckercompanionkotlin.model.data.LocationItemList
import com.gustavo.cocheckercompanionkotlin.model.data.SimpleLocation
import com.gustavo.cocheckercompanionkotlin.model.domain.FetchLocationsUseCase
import com.gustavo.cocheckercompanionkotlin.ui.main.adapters.LocationsListAdapter
import com.gustavo.cocheckercompanionkotlin.utils.LoggerUtil

class LocationsListViewModel: BaseViewModel() {
    val myLocationsListAdapter: LocationsListAdapter =
        LocationsListAdapter(::onClickLocation)
    private val fetchLocationsUseCase = FetchLocationsUseCase()

    val mutableClickedLocation = MutableLiveData<SimpleLocation?>(null)


    val emptyLocationsListMessageVisibility = MutableLiveData<Int>()
    val fabAddLocationsClick = MutableLiveData<Boolean>()


    fun initialize() {
        myLocationsListAdapter.clear()
        fetchLocationsUseCase.fetchUserLocationsData(object : FetchLocationsUseCase.UserLocationsDataListener {
            override fun onChildAdded(locationItemList: LocationItemList) {
                emptyLocationsListMessageVisibility.postValue(View.GONE)
                myLocationsListAdapter.addLocation(locationItemList)
            }

            override fun onChildChanged(locationItemList: LocationItemList) {
                myLocationsListAdapter.removeLocation(locationItemList)
            }

            override fun onChildRemoved(locationItemList: LocationItemList) {
                myLocationsListAdapter.removeLocation(locationItemList)
                if (myLocationsListAdapter.itemCount == 0) {
                    emptyLocationsListMessageVisibility.postValue(View.VISIBLE)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                LoggerUtil.printStackTraceOnlyInDebug(error.toException())
            }
        })
    }

    private fun onClickLocation(locationId: String, locationName:String) {
        val simple = SimpleLocation(locationId,locationName)
        mutableClickedLocation.value = simple
    }

    fun fabClick(){
        fabAddLocationsClick.value = true
    }
}