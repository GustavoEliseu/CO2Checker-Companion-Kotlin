package com.gustavo.cocheckercompanionkotlin.base

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.gustavo.cocheckercompanionkotlin.utils.PERMISSION_REQUEST

abstract class BaseActivity<out VM : BaseViewModel> : AppCompatActivity() {

    abstract val mViewModel: VM
    @LayoutRes
    abstract fun getLayoutId(): Int
    abstract fun initializeUi()

    override fun onCreate(savedInstanceState: Bundle?){
        //INIT VIEW MODEL
        super.onCreate(savedInstanceState)
        initializeUi()
    }

    fun onCreateOnly(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
    }
    open fun onPermissionGranted(permissions: Array<out String>, requestCode : Int? = null) {}
    open fun onPermissionDenied(permanently: Boolean = false) {}

    fun askPermissions(permissions: Array<out String>? = null, requestCode : Int? = null) {
        val lPermissions = mutableListOf<String>()
        if (permissions != null) lPermissions.addAll(permissions)
        else lPermissions.addAll(
            arrayOf(
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
            )
        )

        permissions?.forEach {
            if (checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED) {
                val getRequestCode = requestCode ?: PERMISSION_REQUEST
                requestPermissions(permissions, getRequestCode)
                return
            }
        }

        this.onPermissionGranted(lPermissions.toTypedArray(), requestCode)
    }
}