package com.gustavo.cocheckercompanionkotlin.ui.main.sensors

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.gustavo.cocheckercompanionkotlin.base.BaseFragment
import com.gustavo.cocheckercompanionkotlin.ui.main.MainActivity
import com.gustavo.cocheckercompanionkotlin.ui.main.viewmodel.SensorsListViewModel
import com.gustavo.cocheckercompanionkotlin.BR
import com.gustavo.cocheckercompanionkotlin.R
import com.gustavo.cocheckercompanionkotlin.databinding.FragmentSensorsListBinding

class SensorsListFragment : BaseFragment<SensorsListViewModel,FragmentSensorsListBinding>(){
    override val mViewModel: SensorsListViewModel by viewModels()
    override fun getLayoutId(): Int = R.layout.fragment_sensors_list

    override fun viewTitle(): Int = R.string.my_sensors

    override fun getBindingVariable(): Int = BR.viewModel

    override fun initializeUi() {
        mViewModel.initialize()
        mViewModel.mutableSensorClick.observe(this, Observer{
            if(it!=null){
                (activity as? MainActivity)?.openSensor(it.mac)
                mViewModel.mutableSensorClick.value = null
            }
        })
        mViewModel.fabAddSensorClick.observe(this, Observer{
            if(it == true){
                addSensor()
                mViewModel.fabAddSensorClick.value = false
            }
        })
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun onStop() {
        mViewModel.mutableSensorClick.removeObservers(this)
        super.onStop()
    }

    fun addSensor(){
        (activity as? MainActivity)?.addSensor(null)
    }
}