//! This file will translate using Khrysalis.
package com.lightningkite.rxkotlinproperty

import io.reactivex.Observable
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy

val <T> Property<T>.observable: Observable<Box<T>> get() = Observable.concat(Observable.create { it.onNext(boxWrap(value)); it.onComplete() }, onChange)
val <T> Property<T>.observableNN: Observable<T> get() = Observable.concat(Observable.create { it.onNext(boxWrap(value)); it.onComplete() }, onChange).map { it -> it.value }
val <T> Property<T>.onChangeNN: Observable<T> get() = onChange.map { it -> it.value }

@CheckReturnValue
inline fun <T> Property<T>.subscribeBy(
    noinline onError: (Throwable) -> Unit = { it -> it.printStackTrace() },
    noinline onComplete: () -> Unit = {},
    crossinline onNext: (T) -> Unit = { it -> }
): Disposable = this.observable.subscribeBy(
    onError = onError,
    onComplete = onComplete,
    onNext = { boxed -> onNext(boxed.value) }
)

fun <E> includes(collection: MutableProperty<Set<E>>, element: E): MutableProperty<Boolean> {
    return collection.map { it ->
        it.contains(element)
    }.withWrite { it ->
        if (it) {
            collection.value = collection.value.plus(element)
        } else {
            collection.value = collection.value.minus(element)
        }
    }
}

fun Property<Boolean>.whileActive(action: () -> Disposable): Disposable {
    var current: Disposable? = null
    return this.subscribeBy {
        if (it) {
            if (current == null) {
                current = action()
            }
        } else {
            current?.dispose()
            current = null
        }
    }
}