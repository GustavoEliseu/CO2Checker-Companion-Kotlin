package com.gustavo.cocheckercompaniomkotlin.ui.main.locations

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.gustavo.cocheckercompaniomkotlin.base.BaseFragment
import com.gustavo.cocheckercompaniomkotlin.ui.main.MainActivity
import com.gustavo.cocheckercompaniomkotlin.ui.main.viewmodel.LocationsListViewModel
import com.gustavo.cocheckercompaniomkotlin.utils.getSafeMapUrlString
import com.gustavo.cocheckercompanionkotlin.BR
import com.gustavo.cocheckercompanionkotlin.R
import com.gustavo.cocheckercompanionkotlin.databinding.FragmentLocationsListBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LocationsListFragment: BaseFragment<LocationsListViewModel, FragmentLocationsListBinding>(){

    override val mViewModel: LocationsListViewModel by viewModels()
    override fun getLayoutId(): Int = R.layout.fragment_locations_list

    override fun viewTitle(): Int = R.string.title_locations

    override fun getBindingVariable(): Int = BR.viewModel

    override fun initializeUi() {
        mViewModel.initialize()
        mViewModel.mutableClickedLocation.observe(this) {
            if (it != null) {
                (activity as? MainActivity)?.openLocation(it.locationId, it.locationName,
                    getSafeMapUrlString(
                    it.latitude.toString(),
                    it.longitude.toString()
                )
                )
                mViewModel.mutableClickedLocation.value = null
            }
        }

        mViewModel.fabAddLocationsClick.observe(this) {
            if (it == true) {
                addLocation()
                mViewModel.fabAddLocationsClick.value = false
            }
        }
    }

    override fun onDestroy() {
        mViewModel.mutableClickedLocation.removeObservers(this)
        mViewModel.fabAddLocationsClick.removeObservers(this)
        super.onDestroy()
    }

    override fun onBackPressed(): Boolean {

        return false
    }

    private fun addLocation(){
        (activity as? MainActivity)?.addLocation()
    }
}