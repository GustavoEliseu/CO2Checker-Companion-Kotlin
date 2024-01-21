package com.gustavo.cocheckercompaniomkotlin.ui.location

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.viewModels
import com.gustavo.cocheckercompaniomkotlin.base.BaseActivity
import com.gustavo.cocheckercompaniomkotlin.ui.location.viewmodel.LocationDetailsViewModel
import com.gustavo.cocheckercompaniomkotlin.utils.LOCATION_NAME
import com.gustavo.cocheckercompaniomkotlin.utils.LOCATION_UID
import com.gustavo.cocheckercompanionkotlin.R

fun Context.locationDetailsIntent(locationUid: String, locationName: String): Intent {
    return Intent(this, LocationDetailsActivity::class.java).apply {
        this.putExtra(LOCATION_UID, locationUid)
        this.putExtra(LOCATION_NAME, locationName)
    }
}

class LocationDetailsActivity: BaseActivity<LocationDetailsViewModel>() {
    override val mViewModel: LocationDetailsViewModel by viewModels()

    override fun getLayoutId(): Int =  R.layout.activity_location_details

    override fun initializeUi() {
        val uid = intent.getStringExtra(LOCATION_UID)

        if(uid != null){
            Toast.makeText(this,"Sucesso ${uid}", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this,"Ocorreu um erro", Toast.LENGTH_SHORT).show()
        }
    }
}