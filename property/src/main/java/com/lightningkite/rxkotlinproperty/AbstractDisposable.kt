package com.lightningkite.rxkotlinproperty

import io.reactivex.disposables.Disposable

abstract class AbstractDisposable: Disposable {
    private var privateDisposed = false

    abstract fun onDispose()

    override fun dispose() {
        if(privateDisposed) return
        privateDisposed = true
        onDispose()
    }

    override fun isDisposed(): Boolean = privateDisposed
}