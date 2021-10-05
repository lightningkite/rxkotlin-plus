//! This file will translate using Khrysalis.
package com.lightningkite.rxkotlinproperty

import io.reactivex.Observable

class RxTransformationOnlyProperty<T>(
    val basedOn: Property<T>,
    val operator: (Observable<Box<T>>) -> Observable<Box<T>>
) : Property<T>() {
    override val value: T
        get() = basedOn.value

    override val onChange: Observable<Box<T>> get() = operator(basedOn.onChange)
}

fun <T> Property<T>.distinctUntilChanged(): Property<T> = this.plusRx { it.startWith(Box.wrap(value)).distinctUntilChanged().skip(1) }

fun <T> Property<T>.plusRx(operator: (Observable<Box<T>>) -> Observable<Box<T>>): Property<T> {
    return RxTransformationOnlyProperty<T>(this, operator)
}