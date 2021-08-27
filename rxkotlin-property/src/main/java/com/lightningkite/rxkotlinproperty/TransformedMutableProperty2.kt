//! This file will translate using Khrysalis.
package com.lightningkite.rxkotlinproperty

import io.reactivex.Observable

class TransformedMutableProperty2<A, B>(
    val basedOn: MutableProperty<A>,
    val read: (A) -> B,
    val write: (A, B) -> A
) : MutableProperty<B>() {
    override fun update() {
        basedOn.update()
    }

    override var value: B
        get() {
            return read(basedOn.value)
        }
        set(value) {
            basedOn.value = write(basedOn.value, value)
        }
    override val onChange: Observable<Box<B>> get() {
        val readCopy = read
        return basedOn.onChange.map { it -> boxWrap(readCopy(it.value)) }
    }
}

fun <T, B> MutableProperty<T>.mapWithExisting(
    read: (T) -> B,
    write: (T, B) -> T
): MutableProperty<B> {
    return TransformedMutableProperty2<T, B>(this, read, write)
}
