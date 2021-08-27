package com.lightningkite.rxkotlinviewgenerators.android

import android.app.Application

open class ButterflyApplication: Application() {
    companion object {
        fun setup(application: Application){
            ApplicationAccess.applicationIsActiveStartup(application)
        }
    }

    override fun onCreate() {
        super.onCreate()
        setup(this)
    }
}