package com.lightningkite.rxkotlinproperty

import io.reactivex.Observable

fun <T> Observable<Box<T>>.asPropertyUnboxed(defaultValue: T): Property<T> {
    return EventToProperty<T>(defaultValue, this)
}
