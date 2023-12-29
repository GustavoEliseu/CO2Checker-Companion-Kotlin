package com.gustavo.cocheckercompaniomkotlin.ui.main.locations

import androidx.fragment.app.viewModels
import com.gustavo.cocheckercompaniomkotlin.base.BaseFragment
import com.gustavo.cocheckercompaniomkotlin.ui.main.viewmodel.LocationsListViewModel
import com.gustavo.cocheckercompanionkotlin.databinding.FragmentLocationsListBinding

class LocationsListFragment: BaseFragment<LocationsListViewModel, FragmentLocationsListBinding>(){

    override val mViewModel: LocationsListViewModel by viewModels()
    override fun getLayoutId(): Int {
        TODO("Not yet implemented")
    }

    override fun viewTitle(): Int? {
        TODO("Not yet implemented")
    }

    override fun getBindingVariable(): Int {
        TODO("Not yet implemented")
    }

    override fun initializeUi() {
        TODO("Not yet implemented")
    }

    override fun onBackPressed(): Boolean {
        TODO("Not yet implemented")
    }

}