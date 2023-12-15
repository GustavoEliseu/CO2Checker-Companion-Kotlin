package com.Gustavo.COCheckerCompanionKotlin.base

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.Gustavo.COCheckerCompanionKotlin.R
import com.Gustavo.COCheckerCompanionKotlin.utils.PERMISSION_REQUEST
import javax.inject.Inject

abstract class BaseActivity<out VM : BaseViewModel> : AppCompatActivity() {

    abstract val mViewModel: VM
    @LayoutRes
    abstract fun getLayoutId(): Int
    abstract fun initializeUi()

    override fun onCreate(savedInstanceState: Bundle?){
        //INIT VIEW MODEL
        mViewModel
        super.onCreate(savedInstanceState)
    }
    open fun onPermissionGranted(permissions: Array<out String>) {}
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

    @TargetApi(Build.VERSION_CODES.M)
    fun askPermissions(permissions: Array<out String>? = null) {
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
                val requestCode = PERMISSION_REQUEST
                requestPermissions(permissions, requestCode)
                return
            }
        }

        this.onPermissionGranted(lPermissions.toTypedArray())
    }
}