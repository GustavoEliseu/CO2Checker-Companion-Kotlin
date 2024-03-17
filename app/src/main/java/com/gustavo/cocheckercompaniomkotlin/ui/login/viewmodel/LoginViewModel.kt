package com.gustavo.cocheckercompaniomkotlin.ui.login.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.gustavo.cocheckercompaniomkotlin.base.BaseViewModel
import com.gustavo.cocheckercompaniomkotlin.model.data.CustomResult
import com.gustavo.cocheckercompaniomkotlin.model.domain.creteUserWithEmailAndPasswordSuspend
import com.gustavo.cocheckercompaniomkotlin.model.domain.sendPasswordResetEmailSuspend
import com.gustavo.cocheckercompaniomkotlin.model.domain.signInWithEmailAndPasswordSuspend
import javax.inject.Inject

class LoginViewModel @Inject constructor() : BaseViewModel() {

    val mutableBtnVisibility = MutableLiveData<Int>()
    val mutableLoadVisibility = MutableLiveData<Int>()
    val errorVisibility = MutableLiveData<Int>()
    private var auth: FirebaseAuth? = null

    fun initialize() {
        auth = FirebaseAuth.getInstance()
    }

    suspend fun login(email: String, password: String): CustomResult<FirebaseUser?> {
        return try {
            val authResult = auth?.signInWithEmailAndPasswordSuspend(email, password)
            CustomResult.Success(authResult?.user)
        } catch (e: Exception) {
            CustomResult.Error(e)
        }
    }

    suspend fun register(email: String, password: String): CustomResult<FirebaseUser?> {
        return try {
            val authResult = auth?.creteUserWithEmailAndPasswordSuspend(email, password)
            CustomResult.Success(authResult?.user)
        } catch (e: Exception) {
            CustomResult.Error(e)
        }
    }

    suspend fun forgot(email: String): CustomResult<Any?> {
        return try {
            val authResult = auth?.sendPasswordResetEmailSuspend(email)
            CustomResult.Success(authResult)
        } catch (e: Exception) {
            CustomResult.Error(e)
        }
    }

    fun changeLoadingVisibility(showLoad: Boolean) {
        mutableBtnVisibility.value = if (showLoad) View.GONE else View.VISIBLE
        mutableLoadVisibility.value = if (showLoad) View.VISIBLE else View.GONE
    }
}