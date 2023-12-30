package com.gustavo.cocheckercompaniomkotlin.ui.main.locations

import androidx.fragment.app.viewModels
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
    }

    override fun onBackPressed(): Boolean {

        return false
    }

}