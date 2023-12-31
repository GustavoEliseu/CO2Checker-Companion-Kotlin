package com.gustavo.cocheckercompaniomkotlin.ui.main.sensors

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.gustavo.cocheckercompaniomkotlin.base.BaseFragment
import com.gustavo.cocheckercompaniomkotlin.ui.main.viewmodel.SensorsListViewModel
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
                Toast.makeText(context,it.mac,Toast.LENGTH_SHORT).show()
                mViewModel.mutableSensorClick.value = null
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

}