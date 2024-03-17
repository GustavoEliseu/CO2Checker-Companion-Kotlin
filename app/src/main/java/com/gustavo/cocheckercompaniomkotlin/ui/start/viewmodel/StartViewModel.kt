package com.gustavo.cocheckercompaniomkotlin.ui.start.viewmodel

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.gustavo.cocheckercompaniomkotlin.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class StartViewModel @Inject constructor(): BaseViewModel() {
    val startState = MutableLiveData(StartStateEnum.CHECKING)
    fun initialize() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser == null){
            startState.value = StartStateEnum.LOGIN
        }else{
            startState.value = StartStateEnum.ALREADY_LOGGED
        }
    }

    enum class StartStateEnum{
        ALREADY_LOGGED, LOGIN, CHECKING
    }
}