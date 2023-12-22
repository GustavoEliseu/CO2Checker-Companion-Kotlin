package com.gustavo.cocheckercompaniomkotlin.ui.login

import android.text.Editable
import android.util.Patterns
import android.widget.TextView
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase
import com.gustavo.cocheckercompaniomkotlin.base.BaseActivity
import com.gustavo.cocheckercompaniomkotlin.data.RegisterUser
import com.gustavo.cocheckercompaniomkotlin.ui.login.viewmodel.LoginViewModel
import com.gustavo.cocheckercompaniomkotlin.ui.main.mainIntent
import com.gustavo.cocheckercompaniomkotlin.utils.containsDigit
import com.gustavo.cocheckercompaniomkotlin.utils.isNullOrEmptyOrBlank
import com.gustavo.cocheckercompaniomkotlin.utils.isPasswordValid
import com.gustavo.cocheckercompaniomkotlin.utils.isValidEmail
import com.gustavo.cocheckercompaniomkotlin.utils.longToast
import com.gustavo.cocheckercompaniomkotlin.utils.toast
import com.gustavo.cocheckercompanionkotlin.R
import com.gustavo.cocheckercompanionkotlin.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class LoginActivity : BaseActivity<LoginViewModel>() {
    private lateinit var mBinding: ActivityLoginBinding
    override val mViewModel: LoginViewModel by viewModels()
    private var auth: FirebaseAuth? = null
    val database = FirebaseDatabase.getInstance()
    override fun getLayoutId(): Int = R.layout.activity_login

    override fun initializeUi() {
        auth = FirebaseAuth.getInstance()
        mBinding = DataBindingUtil.setContentView(this, getLayoutId())
        mBinding.viewModel = mViewModel

        setContentView(mBinding.root)
        initClicks()
    }

    private fun initClicks() {
        mBinding.registerButton.setOnClickListener {
            registerClick()
        }
        mBinding.nextButton.setOnClickListener {
            loginClick()
        }
        mBinding.forgotButton.setOnClickListener {
            forgotClick()
        }
    }

    private fun loginClick() {
        mViewModel.changeLoadingVisibility(true)
        if (!mBinding.passwordEditText.text.isPasswordValid()) {
            mBinding.passwordErrorMessage.text = getString(R.string.error_password)
            mViewModel.changeLoadingVisibility(false)
        } else if (!mBinding.emailEditText.text.isValidEmail()) {
            mBinding.emailTextInput.error = getString(R.string.error_email)
            mViewModel.changeLoadingVisibility(false)
        } else {
            mBinding.passwordErrorMessage.text = null
            val email: String = mBinding.emailEditText.text.toString()
            auth?.signInWithEmailAndPassword(email, mBinding.passwordEditText.text.toString())
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth?.currentUser
                        mViewModel.changeLoadingVisibility(false)
                        startActivity(mainIntent())
                        finish()
                    } else {
                        when(task.exception){
                            is FirebaseAuthInvalidCredentialsException -> {
                                longToast(R.string.registerErrorAlreadyExists)
                            }
                            else -> {
                                longToast(R.string.registerError)
                                Timber.i(task.result.toString())
                            }
                        }
                        mViewModel.changeLoadingVisibility(false)
                    }
                }
        }
    }

    private fun registerClick() {
        mBinding.passwordErrorMessage.text = ""
        if (!mBinding.passwordEditText.text.isPasswordValid()) {
            mBinding.passwordErrorMessage.text = getString(R.string.error_password)
        } else if (!mBinding.emailEditText.text.isValidEmail()) {
            mBinding.emailTextInput.error = getString(R.string.error_email)
        } else {
            mBinding.passwordErrorMessage.text = null
            mBinding.emailTextInput.error = null
            mViewModel.changeLoadingVisibility(true)
            auth?.createUserWithEmailAndPassword(
                mBinding.emailEditText.text.toString(),
                mBinding.passwordEditText.text.toString()
            )?.addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    val user = auth?.currentUser
                    if (user != null) {
                        val myRef = database.getReference("Users")
                        val registerUser = RegisterUser(user.email)
                        myRef.child(user.uid).setValue(
                            registerUser
                        ) { error, ref ->
                            if (error != null) {
                                toast("Não foi possível registrar o usuário!")
                            }
                        }
                        mViewModel.changeLoadingVisibility(false)
                        startActivity(mainIntent())
                        finish()
                    }
                } else {
                    when(task.exception){
                        is FirebaseAuthUserCollisionException -> {
                            longToast(R.string.registerErrorAlreadyExists)
                        }
                        is FirebaseAuthInvalidCredentialsException -> {
                            longToast(R.string.registerErrorAlreadyExists)
                        }
                        else -> {
                            longToast(R.string.registerError)
                            Timber.i(task.result.toString())
                        }
                    }
                    mViewModel.changeLoadingVisibility(false)
                }
            }
        }
    }

    private fun forgotClick() {
        val email = mBinding.emailEditText.text.toString()
        mBinding.emailTextInput
        mBinding.emailEditText

        if (email.isNullOrEmptyOrBlank() || !mBinding.emailEditText.text.isValidEmail()) {
            mBinding.emailTextInput.error = getString(R.string.error_email)
            toast(R.string.type_your_email)
            return
        }
        mViewModel.changeLoadingVisibility(true)
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                mViewModel.changeLoadingVisibility(false)
                if (task.isSuccessful) {
                    toast("")
                } else{
                    when(task.exception){
                        is FirebaseAuthEmailException -> {
                            longToast(R.string.ForgotErrorEmail)
                        }
                        else -> {
                            longToast(R.string.registerError)
                            Timber.i(task.result.toString())
                        }
                    }
                }
            }
    }
}