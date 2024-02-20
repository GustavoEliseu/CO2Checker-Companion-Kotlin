package com.gustavo.cocheckercompaniomkotlin

import android.app.Application
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication: Application(){

    override fun onCreate() {
        context = this
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        super.onCreate()
    }

    companion object{

        var context: Application? = null
    }
}