package com.gustavo.cocheckercompanionkotlin.ui.location

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.gustavo.cocheckercompanionkotlin.base.BaseActivity
import com.gustavo.cocheckercompanionkotlin.ui.location.viewmodel.LocationDetailsViewModel
import com.gustavo.cocheckercompanionkotlin.utils.LOCATION_NAME
import com.gustavo.cocheckercompanionkotlin.utils.LOCATION_UID
import com.gustavo.cocheckercompanionkotlin.utils.LOCATION_URI
import com.gustavo.cocheckercompanionkotlin.utils.extensions.isNotNullOrNotEmptyOrNotBlank
import com.gustavo.cocheckercompanionkotlin.utils.extensions.toast
import com.gustavo.cocheckercompanionkotlin.R
import com.gustavo.cocheckercompanionkotlin.databinding.ActivityLocationDetailsBinding
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
            mViewModel.toastMessageState.observe(this) {
                it?.let {
                    toast(it)
                    mViewModel.toastMessageState.value = null
                }
            }
        }
    }
}