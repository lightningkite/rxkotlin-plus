package com.lightningkite.rxkotlinproperty.android

import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.annotation.RequiresApi
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.Subject
import java9.util.Optional

fun <T: Any> Subject(
    observable: Observable<T>,
    observer: Observer<T>
): Subject<T> = object: Subject<T>() {
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

fun <A: Any, B: Any> Subject<A>.map(read: (A)->B, write: (B)->A?): Subject<B>
    = Subject(
        this.map(read),
        this.observerMapNotNull(write)
    )

fun <A: Any, B: Any> BehaviorSubject<A>.mapWithExisting(read: (A)->B, write: (A, B)->A): Subject<B> {
    return object: Subject<B>() {
        override fun subscribeActual(observer: Observer<in B>) = this@mapWithExisting.map(read).subscribe(observer)
        override fun onSubscribe(d: Disposable) = this@mapWithExisting.onSubscribe(d)
        override fun onNext(t: B) = this@mapWithExisting.onNext(write(this@mapWithExisting.value!!, t))
        override fun onError(e: Throwable) = this@mapWithExisting.onError(e)
        override fun onComplete() = this@mapWithExisting.onComplete()
        override fun hasObservers(): Boolean = this@mapWithExisting.hasObservers()
        override fun hasThrowable(): Boolean = this@mapWithExisting.hasThrowable()
        override fun hasComplete(): Boolean = this@mapWithExisting.hasComplete()
        override fun getThrowable(): Throwable? = this@mapWithExisting.throwable
    }
}

fun <T: Any> Observable<Optional<T>>.notNull(): Observable<T> = this.filter { it.isPresent }.map { it.get() }

val <T> Optional<T>.kotlin: T? get() = if(isPresent) this.get() else null
val <T: Any> T?.optional: Optional<T> get() = Optional.ofNullable(this)

fun <T: Any, B: Any> Observer<T>.observerMap(mapper: (B)->T): Observer<B> = object: Observer<B> {
    override fun onSubscribe(d: Disposable) = this@observerMap.onSubscribe(d)
    override fun onNext(t: B) = this@observerMap.onNext(mapper(t))
    override fun onError(e: Throwable) = this@observerMap.onError(e)
    override fun onComplete() = this@observerMap.onComplete()
}
fun <T: Any, B: Any> Observer<T>.observerMapNotNull(mapper: (B)->T?): Observer<B> = object: Observer<B> {
    override fun onSubscribe(d: Disposable) = this@observerMapNotNull.onSubscribe(d)
    override fun onNext(t: B) { mapper(t)?.let { this@observerMapNotNull.onNext(it) } }
    override fun onError(e: Throwable) = this@observerMapNotNull.onError(e)
    override fun onComplete() = this@observerMapNotNull.onComplete()
}