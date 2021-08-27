//! This file will translate using Khrysalis.
package com.lightningkite.rxkotlinproperty

import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class StandardProperty<T>(
    var underlyingValue: T,
    override val onChange: Subject<Box<T>> = PublishSubject.create()
) : MutableProperty<T>() {
    override var value: T
        get() = underlyingValue
        set(value) {
            underlyingValue = value
            onChange.onNext(boxWrap(value))
        }

    override fun update() {
        onChange.onNext(boxWrap(value))
    }
}
