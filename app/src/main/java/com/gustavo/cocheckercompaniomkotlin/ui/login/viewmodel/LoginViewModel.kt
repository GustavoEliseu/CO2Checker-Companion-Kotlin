package com.gustavo.cocheckercompaniomkotlin.ui.login.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.gustavo.cocheckercompaniomkotlin.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.gustavo.cocheckercompaniomkotlin.model.domain.Result
import com.gustavo.cocheckercompaniomkotlin.model.domain.creteUserWithEmailAndPasswordSuspend
import com.gustavo.cocheckercompaniomkotlin.model.domain.sendPasswordResetEmailSuspend
import com.gustavo.cocheckercompaniomkotlin.model.domain.signInWithEmailAndPasswordSuspend
import javax.inject.Inject

class LoginViewModel @Inject constructor() : BaseViewModel() {

    val mutableBtnVisibility = MutableLiveData<Int>()
    val mutableLoadVisibility = MutableLiveData<Int>()
    val errorVisibility = MutableLiveData<Int>()
    private var auth: FirebaseAuth? = null

    override fun initialize() {
        auth = FirebaseAuth.getInstance()
    }

    suspend fun login(email: String, password: String): Result<FirebaseUser?> {
        return try {
            val authResult = auth?.signInWithEmailAndPasswordSuspend(email, password)
            Result.Success(authResult?.user)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun  register(email: String, password:String): Result<FirebaseUser?>{
        return try {
            val authResult = auth?.creteUserWithEmailAndPasswordSuspend(email, password)
            Result.Success(authResult?.user)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

suspend fun  forgot(email: String): Result<Any?>{
    return try {
        val authResult = auth?.sendPasswordResetEmailSuspend(email)
        Result.Success(authResult)
    } catch (e: Exception) {
        Result.Error(e)
    }
}
    fun changeLoadingVisibility(showLoad: Boolean) {
        mutableBtnVisibility.value = if (showLoad) View.GONE else View.VISIBLE
        mutableLoadVisibility.value = if (showLoad) View.VISIBLE else View.GONE
    }
}