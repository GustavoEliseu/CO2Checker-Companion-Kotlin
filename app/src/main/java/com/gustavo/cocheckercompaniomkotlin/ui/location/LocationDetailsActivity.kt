package com.gustavo.cocheckercompaniomkotlin.ui.location

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.gustavo.cocheckercompaniomkotlin.base.BaseActivity
import com.gustavo.cocheckercompaniomkotlin.model.data.NewSensorData
import com.gustavo.cocheckercompaniomkotlin.ui.location.viewmodel.LocationDetailsViewModel
import com.gustavo.cocheckercompaniomkotlin.utils.LOCATION_EXTRA
import com.gustavo.cocheckercompaniomkotlin.utils.LOCATION_NAME
import com.gustavo.cocheckercompaniomkotlin.utils.LOCATION_UID
import com.gustavo.cocheckercompaniomkotlin.utils.LOCATION_URI
import com.gustavo.cocheckercompaniomkotlin.utils.WIFI_DATA
import com.gustavo.cocheckercompaniomkotlin.utils.extensions.isNotNullOrNotEmptyOrNotBlank
import com.gustavo.cocheckercompanionkotlin.R
import com.gustavo.cocheckercompanionkotlin.databinding.ActivityLocationDetailsBinding
import com.gustavo.cocheckercompanionkotlin.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

fun Context.locationDetailsIntent(locationUid: String, locationName: String, locationUri: String?): Intent {
    return Intent(this, LocationDetailsActivity::class.java).apply {
        this.putExtra(LOCATION_UID, locationUid)
        this.putExtra(LOCATION_NAME, locationName)
        this.putExtra(LOCATION_URI, locationUri)
    }
}

class LocationDetailsActivity: BaseActivity<LocationDetailsViewModel>() {
    override val mViewModel: LocationDetailsViewModel by viewModels()
    private lateinit var mBinding: ActivityLocationDetailsBinding
    private val locationName by lazy { intent?.getStringExtra(LOCATION_NAME) }
    private val locationUri by lazy { intent.getStringExtra(LOCATION_URI) }
    private val locationUid by lazy {intent?.getStringExtra(LOCATION_UID)}

    override fun getLayoutId(): Int =  R.layout.activity_location_details

    override fun initializeUi() {
        mBinding = DataBindingUtil.setContentView(this, getLayoutId())
        mBinding.viewModel = mViewModel
        if(locationUid == null){
            Toast.makeText(this,"Falha ao identificar o local", Toast.LENGTH_SHORT).show()
            finish()
            return
        }else {
            if (locationUri.isNotNullOrNotEmptyOrNotBlank()) {
                mViewModel.locationUriMutableText.value = locationUri
            }

            if(locationName.isNotNullOrNotEmptyOrNotBlank()){
                mViewModel.locationNameMutableText.value = locationName
            }

            lifecycleScope.launch {
                mViewModel.getLocationDetails(locationUid!!)
            }
        }
    }
}