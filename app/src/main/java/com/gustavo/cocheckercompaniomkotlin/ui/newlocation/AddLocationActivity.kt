package com.gustavo.cocheckercompaniomkotlin.ui.newlocation

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.compose.ui.unit.dp
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.gustavo.cocheckercompaniomkotlin.base.BaseActivity
import com.gustavo.cocheckercompaniomkotlin.model.data.NewLocationData
import com.gustavo.cocheckercompaniomkotlin.ui.newlocation.custom.SearchLocationAdapter
import com.gustavo.cocheckercompaniomkotlin.ui.newlocation.viewmodel.AddLocationViewModel
import com.gustavo.cocheckercompaniomkotlin.utils.LOCATION_DATA_EXTRA
import com.gustavo.cocheckercompaniomkotlin.utils.LOCATION_EXTRA
import com.gustavo.cocheckercompaniomkotlin.utils.LoggerUtil
import com.gustavo.cocheckercompanionkotlin.BuildConfig.MAPS_API_KEY
import com.gustavo.cocheckercompanionkotlin.R
import com.gustavo.cocheckercompanionkotlin.databinding.ActivityAddLocationBinding
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


fun Context.configNewLocationIntent(
    tempLocationData: Location?,
    currentEditLocation: NewLocationData?
): Intent {
    return Intent(this, AddLocationActivity::class.java).apply {
        this.putExtra(LOCATION_EXTRA, tempLocationData)
        this.putExtra(LOCATION_DATA_EXTRA, currentEditLocation)
    }
}

class AddLocationActivity : BaseActivity<AddLocationViewModel>(),
    OnMapReadyCallback {
    private lateinit var mBinding: ActivityAddLocationBinding
    private val defaultZoomLevel = 16.0f
    private var mGoogleMap: GoogleMap? = null
    private lateinit var placesClient: PlacesClient

    private val userCurrentLocation by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(LOCATION_EXTRA, Location::class.java)
        } else {
            intent.getParcelableExtra(LOCATION_EXTRA) as? Location
        }
    }
    val userEditLocation by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(LOCATION_DATA_EXTRA, NewLocationData::class.java)
        } else {
            intent.getSerializableExtra(LOCATION_DATA_EXTRA) as? NewLocationData
        }
    }

    var currentPosition: LatLng? = null

    override val mViewModel: AddLocationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        placesClient = Places.createClient(this)
    }

    override fun getLayoutId(): Int = R.layout.activity_add_location

    override fun initializeUi() {
        mBinding = DataBindingUtil.setContentView(this, getLayoutId())
        mBinding.viewModel = mViewModel
        mViewModel.initialize()

        Places.initialize(applicationContext, MAPS_API_KEY, Locale.getDefault())
        mBinding.map.getFragment<SupportMapFragment>().getMapAsync(this)
        if(!this::placesClient.isInitialized ){
            placesClient = Places.createClient(this)
        }
        val adapter = SearchLocationAdapter(placesClient) {
            mViewModel.predictionsMutableVisibility.value = View.GONE
            mBinding.autoCompleteEditText.text?.clear()
            lifecycleScope.launch {
                mViewModel.selectedLatLng.value = getPlaceCoordinatesById(it.placeId)
            }
        }
        mBinding.addressSuggestionRecycler.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        mBinding.addressSuggestionRecycler.adapter = adapter
        mBinding.autoCompleteEditText.doOnTextChanged { text, _, _, _ ->
            if(text.isNullOrEmpty()){
                mViewModel.predictionsMutableVisibility.value = View.GONE
            }else{
                mViewModel.predictionsMutableVisibility.value = View.VISIBLE
                lifecycleScope.launch {
                    try {
                        adapter.filterWithPredictions(text.toString())
                    } catch (e:Exception){
                        LoggerUtil.printStackTraceOnlyInDebug(e)
                    }
                }
            }
        }


//        val autoCompleteFragment = mBinding.mapSearchView.getFragment<AutocompleteSupportFragment>()
//        autoCompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
//
//        autoCompleteFragment.setOnPlaceSelectedListener(object: PlaceSelectionListener {
//            override fun onError(p0: Status) {
//
//            }
//
//            override fun onPlaceSelected(p0: Place) {
//                mViewModel.selectedLatLng.value = p0.latLng
//            }
//        })

        addObservers()
        mViewModel.markerDescriptionVisibility.value = View.GONE
    }

    suspend fun getPlaceCoordinatesById(placeId: String): LatLng? =
        suspendCoroutine { continuation ->
            placesClient.fetchPlace(
                FetchPlaceRequest.newInstance(placeId, listOf(Place.Field.LAT_LNG))
            ).addOnSuccessListener {
                continuation.resume(it.place.latLng)
            }.addOnFailureListener {
                continuation.resume(null)
            }
        }

    private fun addObservers() {
        mViewModel.selectedLatLng.observe(this) {
            if (it == null) {
                userCurrentLocation?.let { default ->
                    mGoogleMap?.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(default.latitude, default.longitude),
                            defaultZoomLevel
                        )
                    )
                }
            } else {
                mGoogleMap?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        it,
                        defaultZoomLevel
                    )
                )
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        mGoogleMap?.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.custom_map_style))

        mGoogleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL

        mGoogleMap?.uiSettings?.isZoomControlsEnabled = true
        mGoogleMap?.uiSettings?.isCompassEnabled = true
        mGoogleMap?.uiSettings?.isMyLocationButtonEnabled = true
        mGoogleMap?.uiSettings?.isZoomGesturesEnabled = true
        mGoogleMap?.uiSettings?.isRotateGesturesEnabled = true
        mGoogleMap?.setPadding(15, 15, 15, 115)

        mGoogleMap?.setOnMarkerClickListener {
            lifecycleScope.launch {
                mViewModel.selectedLatLng.value = getPlaceCoordinatesById(it.id)
            }
            true
        }
        userCurrentLocation?.let {
            currentPosition = LatLng(it.latitude, it.longitude)
            currentPosition?.let { pos ->
                val localMarker = googleMap.addMarker(
                    MarkerOptions().position(pos)
                        .draggable(true)
                        .visible(false)
                )
                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        pos,
                        defaultZoomLevel
                    )
                )
                googleMap.setOnCameraMoveListener {
                    currentPosition = googleMap.cameraPosition.target
                    localMarker?.position = googleMap.cameraPosition.target
                }
            }
        }
    }

    override fun onStop() {
        mViewModel.selectedLatLng.removeObservers(this)
        super.onStop()
    }
}