package com.gustavo.cocheckercompaniomkotlin.ui.login

import androidx.activity.viewModels
import com.gustavo.cocheckercompaniomkotlin.base.BaseActivity
import com.gustavo.cocheckercompaniomkotlin.ui.login.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity() : BaseActivity<LoginViewModel>() {
    override val mViewModel: LoginViewModel by viewModels()
    override fun getLayoutId(): Int = 0

    override fun initializeUi() {

    }

    fun initClicks(){

    }
}