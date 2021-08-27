//! This file will translate using Khrysalis.
package com.lightningkite.rxkotlinproperty

import io.reactivex.Observable

class TransformedMutableProperty<A, B>(
    val basedOn: MutableProperty<A>,
    val read: (A) -> B,
    val write: (B) -> A
) : MutableProperty<B>() {
    override fun update() {
        basedOn.update()
    }

    override var value: B
        get() {
            return read(basedOn.value)
        }
        set(value) {
            basedOn.value = write(value)
        }
    override val onChange: Observable<Box<B>> get() {
        val readCopy = read
        return basedOn.onChange.map { it -> boxWrap(readCopy(it.value)) }
    }
}



fun <T, B> MutableProperty<T>.map(
    read: (T) -> B,
    write: (B) -> T
): MutableProperty<B> {
    return TransformedMutableProperty<T, B>(this, read, write)
}
