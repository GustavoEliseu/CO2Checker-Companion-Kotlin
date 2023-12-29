package com.gustavo.cocheckercompaniomkotlin.ui.main.sensors

import androidx.fragment.app.viewModels
import com.gustavo.cocheckercompaniomkotlin.base.BaseFragment
import com.gustavo.cocheckercompaniomkotlin.ui.main.viewmodel.SensorsListViewModel
import com.gustavo.cocheckercompanionkotlin.BR
import com.gustavo.cocheckercompanionkotlin.R
import com.gustavo.cocheckercompanionkotlin.databinding.FragmentSensorsListBinding

class SensorsListFragment() : BaseFragment<SensorsListViewModel,FragmentSensorsListBinding>(){
    override val mViewModel: SensorsListViewModel by viewModels()
    override fun getLayoutId(): Int = R.layout.fragment_sensors_list

    override fun viewTitle(): Int = R.string.my_sensors

    override fun getBindingVariable(): Int = BR.viewModel

    override fun initializeUi() {
        mViewModel.initialize()
    }

    override fun onBackPressed(): Boolean {
        return false
    }

}