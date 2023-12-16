package com.gustavo.cocheckercompaniomkotlin.ui.login.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.gustavo.cocheckercompaniomkotlin.base.BaseViewModel
import javax.inject.Inject

class LoginViewModel @Inject constructor(): BaseViewModel() {

    val mutableBtnVisibility = MutableLiveData<Int>()
    val mutableLoadVisibility = MutableLiveData<Int>()

    fun changeLoadingVisibility(showLoad: Boolean){
        mutableBtnVisibility.value = if(showLoad) View.GONE else View.VISIBLE
        mutableLoadVisibility.value = if(showLoad) View.VISIBLE else View.GONE
    }

}