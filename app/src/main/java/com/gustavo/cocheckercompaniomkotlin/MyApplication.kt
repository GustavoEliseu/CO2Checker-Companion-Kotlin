package com.gustavo.cocheckercompaniomkotlin

import android.app.Application
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.HiltAndroidApp
import java.lang.ref.WeakReference

@HiltAndroidApp
class MyApplication: Application(){


    override fun onCreate() {
        weakContext = WeakReference(this)
        myPackageName = this.packageName
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        super.onCreate()
    }

    companion object{
        lateinit var myPackageName: String
        var weakContext: WeakReference<Application>? = null
    }
}