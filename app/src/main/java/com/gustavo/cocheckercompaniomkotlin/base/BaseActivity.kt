package com.gustavo.cocheckercompaniomkotlin.base

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingComponent
import androidx.fragment.app.Fragment
import com.gustavo.cocheckercompaniomkotlin.utils.LOCATION_PERMISSION_REQUEST
import com.gustavo.cocheckercompaniomkotlin.utils.PERMISSION_REQUEST
import com.gustavo.cocheckercompanionkotlin.R
import com.gustavo.cocheckercompanionkotlin.databinding.ActivityMainBinding

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

    @SuppressLint("PrivateResource")
    open fun openFragment(
        fragment: Fragment,
        tag: String?,
        imgClose: ImageView?,
        animated: Boolean = true
    ) {
        val transaction = supportFragmentManager.beginTransaction()
        if (animated) transaction.setCustomAnimations(
            R.anim.enter_from_right,
            R.anim.exit_to_left,
            R.anim.enter_from_left,
            R.anim.exit_to_right
        )

        transaction
            .replace(R.id.container, fragment, tag)
            .addToBackStack(null)
            .commit()

        if (supportFragmentManager.backStackEntryCount > 0)
            imgClose?.setImageResource(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
    }

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
                val requestCode = requestCode ?: PERMISSION_REQUEST
                requestPermissions(permissions, requestCode)
                return
            }
        }

        this.onPermissionGranted(lPermissions.toTypedArray(), requestCode)
    }
}