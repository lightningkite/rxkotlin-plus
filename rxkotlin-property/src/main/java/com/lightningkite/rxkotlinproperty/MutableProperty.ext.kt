//! This file will translate using Khrysalis.
package com.lightningkite.rxkotlinproperty

import io.reactivex.rxkotlin.subscribeBy


fun <T> MutableProperty<T>.serves(until: DisposeCondition, other: MutableProperty<T>) {

    var suppress = false

    other.observable.subscribeBy { value ->
        if (!suppress) {
            suppress = true
            this.value = value.value
            suppress = false
        }
    }.until(until)

    this.onChange.subscribeBy { value ->
        if (!suppress) {
            suppress = true
            other.value = value.value
            suppress = false
        }
    }.until(until)
}

