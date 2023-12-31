package com.gustavo.cocheckercompaniomkotlin.ui.main.locations

import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.gustavo.cocheckercompaniomkotlin.base.BaseFragment
import com.gustavo.cocheckercompaniomkotlin.ui.main.viewmodel.LocationsListViewModel
import com.gustavo.cocheckercompanionkotlin.BR
import com.gustavo.cocheckercompanionkotlin.R
import com.gustavo.cocheckercompanionkotlin.databinding.FragmentLocationsListBinding

class LocationsListFragment: BaseFragment<LocationsListViewModel, FragmentLocationsListBinding>(){

    override val mViewModel: LocationsListViewModel by viewModels()
    override fun getLayoutId(): Int = R.layout.fragment_locations_list

    override fun viewTitle(): Int = R.string.title_locations

    override fun getBindingVariable(): Int = BR.viewModel

    override fun initializeUi() {
        mViewModel.initialize()
        mViewModel.mutableClickedLocation.observe(this, Observer{
            if(it != null){
                Toast.makeText(context, it.locationName ?: "emptyName", Toast.LENGTH_SHORT).show()
                mViewModel.mutableClickedLocation.value = null
            }
        })
    }

    override fun onStop() {
        mViewModel.mutableClickedLocation.removeObservers(this)
        super.onStop()
    }

    override fun onBackPressed(): Boolean {

        return false
    }

}