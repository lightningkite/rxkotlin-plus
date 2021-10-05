//! This file will translate using Khrysalis.
package com.lightningkite.rxkotlinproperty

import io.reactivex.Observable

class EventToProperty<T>(override var value: T, val wrapped: Observable<Box<T>>) : Property<T>() {
    override val onChange: Observable<Box<T>>
        get() = Observable.concat(wrapped.map { it ->
            value = it.value
            it
        }.doOnError {
//            Log.e(
//                "EventToObservableProperty",
//                "Oh boy, you done screwed up.  The following stack trace is from an Observable that had an error that was converted to an ObservableProperty, which has a contract to never error.  The currently held value is '$value"
//            )
            it.printStackTrace()
        }.onErrorResumeNext(Observable.never()), Observable.never())
}

fun <Element> Observable<Element>.asProperty(defaultValue: Element): Property<Element> {
    return EventToProperty<Element>(defaultValue, this.map { it -> Box.wrap(it) })
}

fun <Element> Observable<Element>.asPropertyDefaultNull(): Property<Element?> {
    return EventToProperty<Element?>(null, this.map { it -> Box.wrap(it) })
}

