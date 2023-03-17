package com.lightningkite.rx.android.ble

import com.lightningkite.rx.android.staticApplicationContext
import com.polidea.rxandroidble3.RxBleClient
import io.reactivex.rxjava3.exceptions.UndeliverableException
import io.reactivex.rxjava3.plugins.RxJavaPlugins

internal object FixRxUndeliverable {
    init {
        val originalHandler = RxJavaPlugins.getErrorHandler()
        RxJavaPlugins.setErrorHandler { throwable ->
            if (throwable is UndeliverableException) {
                return@setErrorHandler // ignore BleExceptions as they were surely delivered at least once
            }
            // add other custom handlers if needed
            originalHandler?.accept(throwable) ?: throw RuntimeException("No handler available", throwable)
        }
    }
}


val bleClient = RxBleClient.create(staticApplicationContext)