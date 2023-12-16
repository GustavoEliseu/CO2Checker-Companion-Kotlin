package com.gustavo.cocheckercompaniomkotlin.ui.login

import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.gustavo.cocheckercompaniomkotlin.base.BaseActivity
import com.gustavo.cocheckercompaniomkotlin.ui.login.viewmodel.LoginViewModel
import com.gustavo.cocheckercompanionkotlin.R
import com.gustavo.cocheckercompanionkotlin.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity<LoginViewModel>() {
    private lateinit var mBinding: ActivityLoginBinding
    override val mViewModel: LoginViewModel by viewModels()
    override fun getLayoutId(): Int = R.layout.activity_login

    override fun initializeUi() {
        mBinding = DataBindingUtil.setContentView(this, getLayoutId())
        mBinding.viewModel = mViewModel

        setContentView(mBinding.root)
    }

    fun initClicks(){

    }
}