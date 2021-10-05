//! This file will translate using Khrysalis.
package com.lightningkite.rxkotlinproperty

import io.reactivex.Observable

abstract class Property<out T> {
    abstract val value: T
    abstract val onChange: Observable<Box<@UnsafeVariance T>>
}

