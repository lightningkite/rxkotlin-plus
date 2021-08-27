//! This file will translate using Khrysalis.
package com.lightningkite.rxkotlinproperty

import io.reactivex.Observable

class CombineManyProperty<IN>(
    val observables: List<Property<IN>>
): Property<List<IN>>() {
    override val value: List<IN>
        get() = observables.map { it.value }
    override val onChange: Observable<Box<List<IN>>>
        get() = observables.map { it.observable }.combineLatest { items ->
            Box.wrap(items.map { it.value })
        }.skip(1)
}

fun <IN, OUT> List<Property<IN>>.combined(
    combiner: (List<IN>) -> OUT
): Property<OUT> {
    return CombineManyProperty(this).map(combiner)
}


fun <T> List<Property<T>>.combined(): Property<List<T>> {
    return CombineManyProperty(this)
}
