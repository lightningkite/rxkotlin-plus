//! This file will translate using Khrysalis.
package com.lightningkite.rxkotlinproperty

import io.reactivex.Observable

class ConstantProperty<T>(val underlyingValue: T) : Property<T>() {
    override val onChange: Observable<Box<T>> = Observable.never()
    override val value: T
        get() = underlyingValue
}
