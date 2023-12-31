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

//    fun login(
//        email: String,
//        password: String,
//        onFinish: (success: Boolean, user: FirebaseUser?, e: Exception?) -> Unit
//    ) {
//        auth?.signInWithEmailAndPassword(email, password)
//            ?.addOnCompleteListener { task ->
//                changeLoadingVisibility(false)
//                onFinish(task.isSuccessful, auth?.currentUser, task.exception)
//            }
//    }

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

//    fun register(
//        email: String,
//        password: String,
//        onFinish: (success: Boolean, user: FirebaseUser?, e: Exception?) -> Unit
//    ) {
//        auth?.createUserWithEmailAndPassword(
//            email,
//            password
//        )?.addOnCompleteListener { task ->
//            val user = auth?.currentUser
//            onFinish(task.isSuccessful, user, task.exception)
//            changeLoadingVisibility(false)
//        }
//    }
suspend fun  forgot(email: String): Result<Any?>{
    return try {
        val authResult = auth?.sendPasswordResetEmailSuspend(email)
        Result.Success(authResult)
    } catch (e: Exception) {
        Result.Error(e)
    }
}
//    fun forgot(email: String, onFinish: (success: Boolean, e: Exception?) -> Unit) {
//        auth?.let { }
//        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
//            .addOnCompleteListener { task ->
//                onFinish(task.isSuccessful, task.exception)
//                changeLoadingVisibility(false)
//            }
//    }

    fun changeLoadingVisibility(showLoad: Boolean) {
        mutableBtnVisibility.value = if (showLoad) View.GONE else View.VISIBLE
        mutableLoadVisibility.value = if (showLoad) View.VISIBLE else View.GONE
    }

    fun changeErrorVisibility(showError: Boolean) {
        errorVisibility.value = if (showError) View.VISIBLE else View.GONE
    }

}