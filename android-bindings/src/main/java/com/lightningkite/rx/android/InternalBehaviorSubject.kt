package com.lightningkite.rx.android

import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.subject.Subject
import com.badoo.reaktive.subject.behavior.BehaviorSubject
import com.badoo.reaktive.subject.publish.PublishSubject
import java.util.concurrent.atomic.AtomicReference

internal fun <T> InternalBehaviorSubject(initialValue: T? = null): BehaviorSubject<T> {
    val subject = PublishSubject<T>()
    return object : Subject<T> by subject, BehaviorSubject<T> {
        @Suppress("ObjectPropertyName")
        private val _value = AtomicReference(initialValue)
        override val value: T get() = _value.get() ?: throw IllegalStateException("Value not set before access.")

        override fun onNext(value: T) {
            this._value.set(value)
            subject.onNext(value)
        }

        override fun subscribe(observer: ObservableObserver<T>) {
            subject.subscribe(observer)
            _value.get()?.let{ subject.onNext(it) }
        }
    }
}