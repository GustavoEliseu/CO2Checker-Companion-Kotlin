package com.gustavo.cocheckercompaniomkotlin.ui.login.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.gustavo.cocheckercompaniomkotlin.base.BaseViewModel
import javax.inject.Inject

class LoginViewModel @Inject constructor() : BaseViewModel() {

    val mutableBtnVisibility = MutableLiveData<Int>()
    val mutableLoadVisibility = MutableLiveData<Int>()
    val errorVisibility = MutableLiveData<Int>()
    private var auth: FirebaseAuth? = null

    override fun initialize() {
        auth = FirebaseAuth.getInstance()
    }

    fun login(
        email: String,
        password: String,
        onFinish: (success: Boolean, user: FirebaseUser?, e: Exception?) -> Unit
    ) {
        auth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener { task ->
                changeLoadingVisibility(false)
                onFinish(task.isSuccessful, auth?.currentUser, task.exception)
            }?.addOnCanceledListener {
                changeLoadingVisibility(false)
                onFinish(false, auth?.currentUser, null)
            }?.addOnFailureListener {
                changeLoadingVisibility(false)
                onFinish(false, null, it)
            }
    }

    fun register(
        email: String,
        password: String,
        onFinish: (success: Boolean, user: FirebaseUser?, e: Exception?) -> Unit
    ) {
        auth?.createUserWithEmailAndPassword(
            email,
            password
        )?.addOnCompleteListener { task ->
            val user = auth?.currentUser
            onFinish(task.isSuccessful, user, task.exception)
            changeLoadingVisibility(false)
        }?.addOnCanceledListener {
            changeLoadingVisibility(false)
            onFinish(false, auth?.currentUser, null)
        }?.addOnFailureListener {
            changeLoadingVisibility(false)
            onFinish(false, null, it)
        }
    }

    fun forgot(email: String, onFinish: (success: Boolean, e: Exception?) -> Unit) {
        auth?.let { }
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                onFinish(task.isSuccessful, task.exception)
                changeLoadingVisibility(false)
            }.addOnCanceledListener {
                changeLoadingVisibility(false)
                onFinish(false,null)
            }.addOnFailureListener {
                changeLoadingVisibility(false)
                onFinish(false,  it)
            }
    }

    fun changeLoadingVisibility(showLoad: Boolean) {
        mutableBtnVisibility.value = if (showLoad) View.GONE else View.VISIBLE
        mutableLoadVisibility.value = if (showLoad) View.VISIBLE else View.GONE
    }

    fun changeErrorVisibility(showError: Boolean) {
        errorVisibility.value = if (showError) View.VISIBLE else View.GONE
    }

}