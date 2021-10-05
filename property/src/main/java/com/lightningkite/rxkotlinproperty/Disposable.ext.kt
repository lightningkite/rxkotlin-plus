package com.lightningkite.rxkotlinproperty

import io.reactivex.disposables.Disposable


fun <Self : Disposable> Self.forever(): Self {
    return this
}

fun <Self : Disposable> Self.until(condition: DisposeCondition): Self {
    condition.call(this)
    return this
}
