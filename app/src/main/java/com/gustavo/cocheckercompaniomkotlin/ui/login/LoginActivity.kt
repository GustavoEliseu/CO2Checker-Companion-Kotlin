package com.gustavo.cocheckercompaniomkotlin.ui.login

import android.text.Editable
import android.util.Patterns
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
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
import com.gustavo.cocheckercompaniomkotlin.utils.LoggerUtil
import com.gustavo.cocheckercompaniomkotlin.utils.containsDigit
import com.gustavo.cocheckercompaniomkotlin.utils.isNullOrEmptyOrBlank
import com.gustavo.cocheckercompaniomkotlin.utils.isPasswordValid
import com.gustavo.cocheckercompaniomkotlin.utils.isValidEmail
import com.gustavo.cocheckercompaniomkotlin.utils.longToast
import com.gustavo.cocheckercompaniomkotlin.utils.safeRun
import com.gustavo.cocheckercompaniomkotlin.utils.toast
import com.gustavo.cocheckercompanionkotlin.R
import com.gustavo.cocheckercompanionkotlin.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class LoginActivity : BaseActivity<LoginViewModel>() {
    private lateinit var mBinding: ActivityLoginBinding
    override val mViewModel: LoginViewModel by viewModels()
    val database = FirebaseDatabase.getInstance()
    var showErrors = false
    override fun getLayoutId(): Int = R.layout.activity_login

    override fun initializeUi() {
        mBinding = DataBindingUtil.setContentView(this, getLayoutId())
        mBinding.viewModel = mViewModel
        mViewModel.initialize()
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

        mBinding.emailEditText.doOnTextChanged { text, start, before, count ->
            if (showErrors) verifyValidEmail(text.toString())
        }
        mBinding.passwordEditText.doOnTextChanged { text, start, before, count ->
            if (showErrors) verifyPasswordValid(text.toString())
        }
    }

    private fun verifyPasswordValid(value: String): Boolean {
        if (!value.isPasswordValid()) {
            mBinding.passwordTextInput.error = getString(R.string.error_password)
            mViewModel.changeLoadingVisibility(false)
            return false
        } else {
            mBinding.passwordTextInput.error = null
            return true
        }
    }

    private fun verifyValidEmail(value: String): Boolean {
        if (!value.isValidEmail()) {
            mBinding.emailTextInput.error = getString(R.string.error_email)
            mViewModel.changeLoadingVisibility(false)
            return false
        } else {
            mBinding.emailTextInput.error = getString(R.string.error_email)
            mViewModel.changeLoadingVisibility(false)
            return true
        }
    }

    private fun loginClick() {
        var validCredentials = true
        mViewModel.changeLoadingVisibility(true)
        if (!verifyPasswordValid(mBinding.passwordEditText.text.toString())) {
            validCredentials = false
        }
        if (!verifyValidEmail(mBinding.emailEditText.text.toString())) {
            validCredentials = false
        }
        if (validCredentials) {
            mBinding.passwordErrorMessage.text = null
            mBinding.emailTextInput.error = null
            mViewModel.login(
                email = mBinding.emailEditText.text.toString(),
                password = mBinding.passwordEditText.text.toString()
            ) { success, user, exception ->
                if (success) {
                    startActivity(mainIntent())
                    finish()
                } else {
                    when (exception) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            toast(R.string.registerErrorAlreadyExists)
                        }

                        else -> {
                            val message = exception?.message
                            toast(message ?: getString(R.string.genericLoginError))
                            exception?.let {
                                LoggerUtil.printStackTraceOnlyInDebug(it)
                            }
                        }
                    }
                }
            }
        } else {
            showErrors = true
        }
    }

    private fun registerClick() {
        if (!mBinding.passwordEditText.text.isPasswordValid()) {
            mBinding.passwordTextInput.error = getString(R.string.error_password)
        } else {
            mBinding.passwordTextInput.error = null
        }

        if (!mBinding.emailEditText.text.isValidEmail()) {
            mBinding.emailTextInput.error = getString(R.string.error_email)
        } else {
            mBinding.emailTextInput.error = null
            mViewModel.changeLoadingVisibility(true)
            mViewModel.register(
                email = mBinding.emailEditText.text.toString()
                , password = mBinding.passwordEditText.text.toString()
            ) { success,user, exception ->
                if (success) {
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
                        startActivity(mainIntent())
                        finish()
                    }
                } else {
                    when (exception) {
                        is FirebaseAuthUserCollisionException -> {
                            longToast(R.string.registerErrorAlreadyExists)
                        }

                        is FirebaseAuthInvalidCredentialsException -> {
                            longToast(R.string.registerErrorAlreadyExists)
                        }

                        else -> {
                            longToast(R.string.registerError)

                            if(exception!= null) LoggerUtil.printStackTraceOnlyInDebug(exception)
                        }
                    }
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
        mViewModel.forgot(email){success, exception ->
            if (success) {
                toast(R.string.forgot_password_result)
            } else {
                when (exception) {
                    is FirebaseAuthEmailException -> {
                        longToast(R.string.ForgotErrorEmail)
                    }

                    else -> {
                        longToast(R.string.registerError)
                        if(exception!= null) LoggerUtil.printStackTraceOnlyInDebug(exception)
                    }
                }
            }
        }
    }
}