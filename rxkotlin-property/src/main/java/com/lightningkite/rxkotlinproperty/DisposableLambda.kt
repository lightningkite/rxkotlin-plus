package com.lightningkite.rxkotlinproperty

import io.reactivex.disposables.Disposable

class DisposableLambda(val lambda: () -> Unit) : Disposable {
    var disposed: Boolean = false
    override fun isDisposed(): Boolean {
        return disposed
    }

    override fun dispose() {
        if (!disposed) {
            disposed = true
            lambda()
        }
    }
}