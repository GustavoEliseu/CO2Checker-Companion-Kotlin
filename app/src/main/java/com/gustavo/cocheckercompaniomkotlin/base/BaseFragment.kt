package com.gustavo.cocheckercompaniomkotlin.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.gustavo.cocheckercompaniomkotlin.R

abstract class BaseFragment<VM: BaseViewModel,VDB: ViewDataBinding> : Fragment() {
    lateinit var mActivity: BaseActivity<VM>
    lateinit var mBinding: VDB
    lateinit var mViewModel: VM
    private var rootView: View? = null

    open fun onPermissionGranted(permissions: Array<out String>) {}
    open fun onPermissionDenied(permanently: Boolean = false) {}

    open fun onFilePathsReturn(filePaths: List<String?>, fromGallery: Boolean = false) {}
    @LayoutRes
    abstract fun getLayoutId(): Int

    abstract fun viewTitle(): Int?
    abstract fun getBindingVariable(): Int
    abstract fun initializeUi()

    open fun setTitle() {
        viewTitle()?.let {
            mActivity.supportActionBar?.title = getString(it)
        }
    }

    abstract fun onBackPressed(): Boolean

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as BaseActivity<VM>
        mViewModel = mActivity.mViewModel
    }

    //Todo - test this on leak canary
    fun getPersistentView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, layout: Int): View? {
        if (rootView == null) {
            mBinding= DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
            rootView = mBinding.root

        } else {
            (rootView?.getParent() as? ViewGroup)?.removeView(rootView)
        }
        return rootView
    }

    fun simpleCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        setTitle()
        mBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (::mBinding.isInitialized) {
            mBinding.setVariable(getBindingVariable(), mViewModel)
            mBinding.executePendingBindings()
        }
    }

    @SuppressLint("PrivateResource")
    fun pushFragment(fragment: Fragment, viewId: Int, animate: Boolean, tag: String? = null) {
        val ft = childFragmentManager.beginTransaction()
        if (animate) ft.setCustomAnimations(
            R.anim.enter_from_right,
            R.anim.exit_to_left,
            R.anim.enter_from_left,
            R.anim.exit_to_right
        ) else ft.setCustomAnimations(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
        ft.replace(viewId, fragment, tag)
        ft.addToBackStack(null)
        ft.commit()
    }
}
