//! This file will translate using Khrysalis.
package com.lightningkite.rxkotlinproperty

import io.reactivex.Observable

class TransformedProperty<A, B>(
    val basedOn: Property<A>,
    val read: (A) -> B
) : Property<B>() {
    override val value: B
        get() {
            return read(basedOn.value)
        }
    override val onChange: Observable<Box<B>> get() {
        val readCopy = read
        return basedOn.onChange.map { it -> boxWrap(readCopy(it.value)) }
    }
}

fun <T, B> Property<T>.map(read: (T) -> B): Property<B> {
    return TransformedProperty<T, B>(this, read)
}
