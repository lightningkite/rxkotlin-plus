//! This file will translate using Khrysalis.
package com.lightningkite.rxkotlinproperty

import io.reactivex.Observable

class SharingProperty<T>(
    val basedOn: Property<T>,
    val startAsListening: Boolean = false
) : Property<T>() {
    var cachedValue: T = basedOn.value
    var isListening = startAsListening
    override val value: T
        get() = if (isListening) cachedValue else basedOn.value

    override val onChange: Observable<Box<T>> = basedOn.onChange
        .doOnNext { this?.cachedValue = it.value }
        .doOnSubscribe { this?.isListening = true }
        .doOnDispose { this?.isListening = false }
        .share()
}

fun <T> Property<T>.share(startAsListening: Boolean = false): SharingProperty<T> {
    return SharingProperty<T>(this, startAsListening)
}