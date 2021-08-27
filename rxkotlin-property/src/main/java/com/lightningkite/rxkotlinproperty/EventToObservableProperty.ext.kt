package com.lightningkite.rxkotlinproperty

import io.reactivex.Observable

fun <T> Observable<Box<T>>.asObservablePropertyUnboxed(defaultValue: T): Property<T> {
    return EventToProperty<T>(defaultValue, this)
}
