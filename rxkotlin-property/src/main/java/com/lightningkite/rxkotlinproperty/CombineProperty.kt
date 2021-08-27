//! This file will translate using Khrysalis.
package com.lightningkite.rxkotlinproperty

import io.reactivex.Observable

class CombineProperty<T, A, B>(
    val observableA: Property<A>,
    val observableB: Property<B>,
    val combiner: (A, B) -> T
): Property<T>() {
    override val value: T
        get() = combiner(observableA.value, observableB.value)
    override val onChange: Observable<Box<T>>
        get() {
            val combinerCopy = combiner
            return observableA.onChange.startWith(Box.wrap(observableA.value))
                .combineLatest(observableB.onChange.startWith(Box.wrap(observableB.value))) { a: Box<A>, b: Box<B> -> boxWrap(combinerCopy(a.value, b.value)) }
                .skip(1)
        }
}

fun <T, B, C> Property<T>.combine(
    other: Property<B>,
    combiner: (T, B) -> C
): Property<C> {
    return CombineProperty(this, other, combiner)
}
