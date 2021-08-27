//! This file will translate using Khrysalis.
package com.lightningkite.rxkotlinproperty

import io.reactivex.Observable

class WriteAddedProperty<A>(
    val basedOn: Property<A>,
    val onWrite: (A) -> Unit
) : MutableProperty<A>() {
    override var value: A
        get() = basedOn.value
        set(value) {
            onWrite(value)
        }
    override val onChange: Observable<Box<A>> get() = basedOn.onChange
    override fun update() {
        //Do nothing
    }
}

fun <T> Property<T>.withWrite(
    onWrite: (T) -> Unit
): MutableProperty<T> {
    return WriteAddedProperty<T>(this, onWrite)
}
