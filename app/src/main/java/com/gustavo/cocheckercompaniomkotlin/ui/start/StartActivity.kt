package com.gustavo.cocheckercompaniomkotlin.ui.start

import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.gustavo.cocheckercompaniomkotlin.base.BaseActivity
import com.gustavo.cocheckercompaniomkotlin.ui.login.loginIntent
import com.gustavo.cocheckercompaniomkotlin.ui.main.mainIntent
import com.gustavo.cocheckercompaniomkotlin.ui.start.viewmodel.StartViewModel
import com.gustavo.cocheckercompanionkotlin.R
import com.gustavo.cocheckercompanionkotlin.databinding.ActivityLoginBinding
import com.gustavo.cocheckercompanionkotlin.databinding.ActivityStartBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartActivity : BaseActivity<StartViewModel>() {
    private lateinit var mBinding: ActivityStartBinding
    override val mViewModel:StartViewModel by viewModels()

    override fun getLayoutId(): Int = R.layout.activity_start

    override fun initializeUi() {
        mBinding = DataBindingUtil.setContentView(this, getLayoutId())
        mBinding.viewModel = mViewModel
        mViewModel.initialize()
        mViewModel.startState.observe(this,Observer{
            when(it){
                StartViewModel.StartStateEnum.ALREADY_LOGGED->{
                    startActivity(mainIntent())
                    finish()
                }
                StartViewModel.StartStateEnum.LOGIN->{
                    startActivity(loginIntent())
                    finish()
                }
                else ->{}
            }
        })
    }
}