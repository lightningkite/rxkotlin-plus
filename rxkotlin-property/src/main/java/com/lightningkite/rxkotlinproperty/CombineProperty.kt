//! This file will translate using Khrysalis.
package com.lightningkite.rxkotlinproperty

import io.reactivex.Observable

class CombineProperty<T, A, B>(
    val propertyA: Property<A>,
    val propertyB: Property<B>,
    val combiner: (A, B) -> T
): Property<T>() {
    override val value: T
        get() = combiner(propertyA.value, propertyB.value)
    override val onChange: Observable<Box<T>>
        get() {
            val combinerCopy = combiner
            return propertyA.onChange.startWith(Box.wrap(propertyA.value))
                .combineLatest(propertyB.onChange.startWith(Box.wrap(propertyB.value))) { a: Box<A>, b: Box<B> -> Box.wrap(combinerCopy(a.value, b.value)) }
                .skip(1)
        }
}

fun <T, B, C> Property<T>.combine(
    other: Property<B>,
    combiner: (T, B) -> C
): Property<C> {
    return CombineProperty(this, other, combiner)
}
