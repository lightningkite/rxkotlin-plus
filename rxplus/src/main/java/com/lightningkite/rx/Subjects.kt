package com.lightningkite.rx

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.Subject
import java.util.Optional

/**
 * Convenience abstract class for dealing with subjects that have a value property
 */
abstract class HasValueSubject<T>: Subject<T>() {
    abstract var value: T
}

/**
 * BehaviorSubject does not guarantee that it will have a value on creation. ValueSubjects
 * wraps BehaviorSubject and requires a value at creation time. This prevents the possibility
 * of crashing when calling value.
 */
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

/**
 *  A convenient function for creating a Subject out of an observable and an observer of type T
 */
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

/**
 *  Maps a Subject<A> to a Subject<B> using the lambdas provided.
 */
fun <A : Any, B : Any> Subject<A>.map(read: (A) -> B, write: (B) -> A): Subject<B> = makeSubject<B>(
    this.map(read),
    this.observerMap(write)
)

/**
 *  Maps a Subject<A> to a Subject<B> using the lambdas provided. If the write labmda returns null it will not
 *  call onNext of this.
 */
fun <A : Any, B : Any> Subject<A>.mapMaybeWrite(read: (A) -> B, write: (B) -> A?): Subject<B> = makeSubject<B>(
    this.map(read),
    this.observerMapNotNull(write)
)

/**
 *  Maps a HasValueSubject<A> to a HasValueSubject<B> using the lambdas provided. This allows for more complicated mapping
 *  between types. When written to the current value of this is passed into the write function.
 */
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

/**
 *  Maps an Observable<Optional<T>> to an Observable<T>.
 */
fun <T : Any> Observable<Optional<T>>.notNull(): Observable<T> = this.filter { it.isPresent }.map { it.get() }

/**
 *  Retrieve the value of the optional using Kotlin's nullable type.
 */
val <T> Optional<T>.kotlin: T? get() = if (isPresent) this.get() else null

/**
 *  Wraps this into an Optional<T>
 */
val <T : Any> T?.optional: Optional<T> get() = Optional.ofNullable(this)


private fun <T : Any, B : Any> Observer<T>.observerMap(mapper: (B) -> T): Observer<B> = object : Observer<B> {
    override fun onSubscribe(d: Disposable) = this@observerMap.onSubscribe(d)
    override fun onNext(t: B) = this@observerMap.onNext(mapper(t))
    override fun onError(e: Throwable) = this@observerMap.onError(e)
    override fun onComplete() = this@observerMap.onComplete()
}
private fun <T : Any, B : Any> Observer<T>.observerMapNotNull(mapper: (B) -> T?): Observer<B> = object : Observer<B> {
    override fun onSubscribe(d: Disposable) = this@observerMapNotNull.onSubscribe(d)
    override fun onNext(t: B) {
        mapper(t)?.let { this@observerMapNotNull.onNext(it) }
    }

    override fun onError(e: Throwable) = this@observerMapNotNull.onError(e)
    override fun onComplete() = this@observerMapNotNull.onComplete()
}

/**
 * Turn an Observable<T> into a Subject<T> using the onWrite provided to create the observer portion of the subject.
 */
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

/**
 * From an Observable<T> create or choose a Subject<B> to use.
 */
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
        override fun onNext(b: B) { lastValue?.onNext(b) }
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