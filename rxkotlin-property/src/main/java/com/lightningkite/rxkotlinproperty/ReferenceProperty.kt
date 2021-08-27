//! This file will translate using Khrysalis.
package com.lightningkite.rxkotlinproperty

import io.reactivex.Observable

class ReferenceProperty<T>(
    val get: ()->T,
    val set: (T)->Unit,
    val event: Observable<Box<T>>
) : MutableProperty<T>() {

    override val onChange: Observable<Box<T>>
        get() = event
    override var value: T
        get() = this.get()
        set(value) {
            this.set(value)
        }
    override fun update() {
        //do nothing
    }
}
