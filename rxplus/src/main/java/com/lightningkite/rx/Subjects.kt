package com.lightningkite.rx

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.Subject
import java.util.Optional

abstract class HasValueSubject<T>: Subject<T>() {
    abstract var value: T
}

class ValueSubject<T: Any>(value: T): HasValueSubject<T>() {
    val underlying = BehaviorSubject.createDefault(value)
    override var value: T
        get() = underlying.value!!
        set(value) { underlying.onNext(value) }
    override fun subscribeActual(observer: Observer<in T>) { underlying.subscribe(observer) }
    override fun onSubscribe(d: Disposable) = underlying.onSubscribe(d)
    override fun onNext(t: T) {
        value = t
    }
    override fun onError(e: Throwable) = underlying.onError(e)
    override fun onComplete() = underlying.onComplete()
    override fun hasObservers(): Boolean = underlying.hasObservers()
    override fun hasThrowable(): Boolean = underlying.hasThrowable()
    override fun hasComplete(): Boolean = underlying.hasComplete()
    override fun getThrowable(): Throwable? = underlying.getThrowable()
}

fun <T : Any> makeSubject(
    observable: Observable<T>,
    observer: Observer<T>
): Subject<T> = object : Subject<T>() {
    private var hasObservers = false
    private var error: Throwable? = null
    private var hasCompleted = false
    override fun subscribeActual(observer: Observer<in T>) {
        hasObservers = true
        observable.subscribe(observer)
    }

    override fun onSubscribe(d: Disposable) = observer.onSubscribe(d)
    override fun onNext(t: T) = observer.onNext(t)
    override fun onError(e: Throwable) {
        error = e
        observer.onError(e)
    }

    override fun onComplete() {
        hasCompleted = true
        observer.onComplete()
    }

    override fun hasObservers(): Boolean = hasObservers
    override fun hasThrowable(): Boolean = error != null
    override fun hasComplete(): Boolean = hasCompleted
    override fun getThrowable(): Throwable? = error
}

fun <A : Any, B : Any> Subject<A>.map(read: (A) -> B, write: (B) -> A?): Subject<B> = makeSubject(
    this.map(read),
    this.observerMapNotNull(write)
)

fun <A : Any, B : Any> HasValueSubject<A>.mapWithExisting(read: (A) -> B, write: (A, B) -> A): HasValueSubject<B> {
    return object : HasValueSubject<B>() {
        override fun subscribeActual(observer: Observer<in B>) = this@mapWithExisting.map(read).subscribe(observer)
        override fun onSubscribe(d: Disposable) = this@mapWithExisting.onSubscribe(d)
        override fun onNext(t: B) = this@mapWithExisting.onNext(write(this@mapWithExisting.value, t))
        override fun onError(e: Throwable) = this@mapWithExisting.onError(e)
        override fun onComplete() = this@mapWithExisting.onComplete()
        override fun hasObservers(): Boolean = this@mapWithExisting.hasObservers()
        override fun hasThrowable(): Boolean = this@mapWithExisting.hasThrowable()
        override fun hasComplete(): Boolean = this@mapWithExisting.hasComplete()
        override fun getThrowable(): Throwable? = this@mapWithExisting.throwable
        override var value: B
            get() = read(this@mapWithExisting.value)
            set(value) { this@mapWithExisting.onNext(write(this@mapWithExisting.value, value)) }
    }
}

fun <T : Any> Observable<Optional<T>>.notNull(): Observable<T> = this.filter { it.isPresent }.map { it.get() }

val <T> Optional<T>.kotlin: T? get() = if (isPresent) this.get() else null
val <T : Any> T?.optional: Optional<T> get() = Optional.ofNullable(this)

fun <T : Any, B : Any> Observer<T>.observerMap(mapper: (B) -> T): Observer<B> = object : Observer<B> {
    override fun onSubscribe(d: Disposable) = this@observerMap.onSubscribe(d)
    override fun onNext(t: B) = this@observerMap.onNext(mapper(t))
    override fun onError(e: Throwable) = this@observerMap.onError(e)
    override fun onComplete() = this@observerMap.onComplete()
}

fun <T : Any, B : Any> Observer<T>.observerMapNotNull(mapper: (B) -> T?): Observer<B> = object : Observer<B> {
    override fun onSubscribe(d: Disposable) = this@observerMapNotNull.onSubscribe(d)
    override fun onNext(t: B) {
        mapper(t)?.let { this@observerMapNotNull.onNext(it) }
    }

    override fun onError(e: Throwable) = this@observerMapNotNull.onError(e)
    override fun onComplete() = this@observerMapNotNull.onComplete()
}

fun <T : Any> Observable<T>.withWrite(onWrite: (T) -> Unit): Subject<T> = makeSubject(
    observable = this,
    observer = object : Observer<T> {
        override fun onSubscribe(d: Disposable) {}
        override fun onNext(t: T) {
            onWrite(t)
        }

        override fun onError(e: Throwable) {}
        override fun onComplete() {}
    }
)

fun <T : Any, B : Any> Observable<T>.switchMapMutable(transformation: (T) -> Subject<B>): Subject<B> =
    object : Subject<B>() {
        private var lastValue: Subject<B>? = null
        private var hasObservers = false
        private var error: Throwable? = null
        private var hasCompleted = false
        override fun subscribeActual(observer: Observer<in B>) {
            hasObservers = true
            this@switchMapMutable.switchMap(transformation).subscribe(observer)
        }

        override fun onSubscribe(d: Disposable) { lastValue?.onSubscribe(d) }
        override fun onNext(t: B) { lastValue?.onNext(t) }
        override fun onError(e: Throwable) {
            error = e
            lastValue?.onError(e)
        }

        override fun onComplete() {
            hasCompleted = true
            lastValue?.onComplete()
        }

        override fun hasObservers(): Boolean = hasObservers
        override fun hasThrowable(): Boolean = error != null
        override fun hasComplete(): Boolean = hasCompleted
        override fun getThrowable(): Throwable? = error
    }