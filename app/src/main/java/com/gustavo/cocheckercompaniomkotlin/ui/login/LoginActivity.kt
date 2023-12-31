package com.gustavo.cocheckercompaniomkotlin.ui.login

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.FirebaseDatabase
import com.gustavo.cocheckercompaniomkotlin.base.BaseActivity
import com.gustavo.cocheckercompaniomkotlin.model.data.RegisterUser
import com.gustavo.cocheckercompaniomkotlin.model.domain.Result
import com.gustavo.cocheckercompaniomkotlin.ui.login.viewmodel.LoginViewModel
import com.gustavo.cocheckercompaniomkotlin.ui.main.mainIntent
import com.gustavo.cocheckercompaniomkotlin.utils.LoggerUtil
import com.gustavo.cocheckercompaniomkotlin.utils.hideKeyboard
import com.gustavo.cocheckercompaniomkotlin.utils.isNullOrEmptyOrBlank
import com.gustavo.cocheckercompaniomkotlin.utils.isPasswordValid
import com.gustavo.cocheckercompaniomkotlin.utils.isValidEmail
import com.gustavo.cocheckercompaniomkotlin.utils.longToast
import com.gustavo.cocheckercompaniomkotlin.utils.toast
import com.gustavo.cocheckercompanionkotlin.R
import com.gustavo.cocheckercompanionkotlin.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference


@AndroidEntryPoint
class LoginActivity : BaseActivity<LoginViewModel>() {
    private lateinit var mBinding: ActivityLoginBinding
    override val mViewModel: LoginViewModel by viewModels()
    private var showErrors = false
    private var activeButton = true
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

        mBinding.emailEditText.doOnTextChanged { text, _, _, _ ->
            if (showErrors) verifyValidEmail(text.toString())
        }
        mBinding.passwordEditText.doOnTextChanged { text, _, _, _ ->
            if (showErrors) verifyPasswordValid(text.toString())
        }
        mBinding.passwordEditText.setOnEditorActionListener { textView, i, keyEvent ->

            if(i == EditorInfo.IME_ACTION_DONE){
                loginClick()
                false
            }else if(keyEvent.keyCode == KeyEvent.KEYCODE_ENTER){
                if(textView.isFocused) {
                    textView.hideKeyboard(WeakReference(this))
                }
                loginClick()
                false
            }else{
                true
            }
        }
    }

    private fun verifyPasswordValid(value: String): Boolean {
        return if (!value.isPasswordValid()) {
            mBinding.passwordTextInput.error = getString(R.string.error_password)
            mViewModel.changeLoadingVisibility(false)
            false
        } else {
            mBinding.passwordTextInput.error = null
            true
        }
    }

    private fun verifyValidEmail(value: String): Boolean {
        return if (!value.isValidEmail()) {
            mBinding.emailTextInput.error = getString(R.string.error_email)
            mViewModel.changeLoadingVisibility(false)
            false
        } else {
            mViewModel.changeLoadingVisibility(true)
            true
        }
    }

    private fun loginClick() {
        if(activeButton) {
            activeButton = false
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
                lifecycleScope.launch {
                    try {
                        val result = mViewModel.login(email = mBinding.emailEditText.text.toString(),
                            password = mBinding.passwordEditText.text.toString())
                        when (result) {
                            is Result.Success -> {
                                mViewModel.changeLoadingVisibility(false)
                                startActivity(mainIntent())
                                activeButton = true
                            }
                            is Result.Error ->{
                                activeButton = true
                                mViewModel.changeLoadingVisibility(false)
                                when (result.exception) {
                                    is FirebaseAuthInvalidCredentialsException -> {
                                        toast(R.string.loginErrorCredentialError)
                                    }
                                    else -> {
                                        val message = result.exception.message
                                        toast(message ?: getString(R.string.genericLoginError))
                                        result.exception.let {
                                            LoggerUtil.printStackTraceOnlyInDebug(it)
                                        }
                                    }
                                }
                            }
                        }
                        // Handle the result here
                    } catch (e: Exception) {
                        // Handle exceptions here
                        toast(e.message ?: getString(R.string.genericLoginError))
                        LoggerUtil.printStackTraceOnlyInDebug(e)
                        mViewModel.changeLoadingVisibility(false)
                    }
                }
            } else {
                showErrors = true
                activeButton = true
            }
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
            lifecycleScope.launch {
                try {
                    val result = mViewModel.register(email = mBinding.emailEditText.text.toString(),
                        password = mBinding.passwordEditText.text.toString())
                    when (result) {
                        is Result.Success -> {
                            mViewModel.changeLoadingVisibility(false)
                            startActivity(mainIntent())
                            activeButton = true
                        }
                        is Result.Error ->{
                            activeButton = true
                            mViewModel.changeLoadingVisibility(false)
                            when (result.exception) {
                                is FirebaseAuthInvalidCredentialsException -> {
                                    toast(R.string.loginErrorCredentialError)
                                }
                                else -> {
                                    val message = result.exception.message
                                    toast(message ?: getString(R.string.genericLoginError))
                                    result.exception.let {
                                        LoggerUtil.printStackTraceOnlyInDebug(it)
                                    }
                                }
                            }
                        }
                    }
                    // Handle the result here
                } catch (e: Exception) {
                    // Handle exceptions here
                    toast(e.message ?: getString(R.string.genericLoginError))
                    LoggerUtil.printStackTraceOnlyInDebug(e)
                    mViewModel.changeLoadingVisibility(false)
                }
            }
        }
    }

    private fun forgotClick() {
        if (mBinding.emailEditText.text.isNullOrEmptyOrBlank() || !mBinding.emailEditText.text.isValidEmail()) {
            mBinding.emailTextInput.error = getString(R.string.error_email)
            toast(R.string.type_your_email)
            return
        }
        mViewModel.changeLoadingVisibility(true)

        lifecycleScope.launch {
            try {
                val result = mViewModel.forgot(mBinding.emailEditText.text.toString())
                when (result) {
                    is Result.Success -> {
                        toast(R.string.forgot_password_result)
                        mViewModel.changeLoadingVisibility(false)
                    }
                    is Result.Error ->{
                        activeButton = true
                        mViewModel.changeLoadingVisibility(false)
                        when (result.exception) {
                            is FirebaseAuthEmailException -> {
                                longToast(R.string.ForgotErrorEmail)
                            }
                            else -> {
                                longToast(R.string.registerError)
                                LoggerUtil.printStackTraceOnlyInDebug(result.exception)
                            }
                        }
                    }
                }
                // Handle the result here
            } catch (e: Exception) {
                // Handle exceptions here
                mViewModel.changeLoadingVisibility(false)
                toast(e.message ?: getString(R.string.genericLoginError))
                LoggerUtil.printStackTraceOnlyInDebug(e)
            }
        }
//        mViewModel.forgot(mBinding.emailEditText.text.toString()){success, exception ->
//            if (success) {
//                toast(R.string.forgot_password_result)
//            } else {
//                when (exception) {
//                    is FirebaseAuthEmailException -> {
//                        longToast(R.string.ForgotErrorEmail)
//                    }
//                    else -> {
//                        longToast(R.string.registerError)
//                        if(exception!= null) LoggerUtil.printStackTraceOnlyInDebug(exception)
//                    }
//                }
//            }
//        }
    }
}