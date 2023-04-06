package com.lightningkite.rx

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.observable.*
import com.badoo.reaktive.subject.Subject
import com.badoo.reaktive.subject.behavior.BehaviorSubject
import com.badoo.reaktive.subject.behavior.behaviorSubject
import kotlin.reflect.KMutableProperty1



/**
 *  A convenient function for creating a Subject out of an observable and callBacks of type T
 */
fun <T> makeSubject(
    observable: Observable<T>,
    callBacks: ObservableCallbacks<T>
): Subject<T> = object : Subject<T>, Observable<T> by observable {
    private var _status: Subject.Status = Subject.Status.Active
    override val status: Subject.Status
        get() = _status

    override fun onComplete() {
        callBacks.onComplete()
        if(_status == Subject.Status.Active)
            _status = Subject.Status.Completed
    }

    override fun onError(error: Throwable) {
        callBacks.onError(error)
        if(_status == Subject.Status.Active)
            _status = Subject.Status.Error(error)
    }

    override fun onNext(value: T) {
        callBacks.onNext(value)

    }
}

/**
 *  Maps a Subject<A> to a Subject<B> using the lambdas provided.
 */
fun <A, B > Subject<A>.map(read: (A) -> B, write: (B) -> A): Subject<B> = makeSubject<B>(
    this.map(mapper = read),
    this.observerMap(write)
)

/**
 *  Maps a Subject<A> to a Subject<B> using the lambdas provided.
 */
//fun <A, B> Subject<A?>.map(read: (A?) -> B, write: (B) -> A?): Subject<B> = makeSubject<B>(
//    this.map(mapper = read),
//    this.observerMap(write)
//)

/**
 *  Maps a Subject<A> to a Subject<B> using the lambdas provided. If the write labmda returns null it will not
 *  call onNext of this.
 */
fun <A: Any, B> Subject<A>.mapMaybeWrite(read: (A) -> B, write: (B) -> A?): Subject<B> = makeSubject<B>(
    this.map(read),
    this.observerMapNotNull(write)
)

/**
 *  Maps a HasValueSubject<A> to a HasValueSubject<B> using the lambdas provided. This allows for more complicated mapping
 *  between types. When written to the current value of this is passed into the write function.
 */
fun <A, B> BehaviorSubject<A>.mapWithExisting(read: (A) -> B, write: (A, B) -> A): BehaviorSubject<B> {
    return object : BehaviorSubject<B> {
        override fun onNext(t: B) = this@mapWithExisting.onNext(write(this@mapWithExisting.value, t))
        override fun onError(e: Throwable) {
            this@mapWithExisting.onError(e)
            _status = Subject.Status.Error(e)
        }
        override fun subscribe(observer: ObservableObserver<B>) {
            this@mapWithExisting.map(read).subscribe(observer)
        }

        override fun onComplete() {
            this@mapWithExisting.onComplete()
            _status = Subject.Status.Completed
        }
        private var _status: Subject.Status = Subject.Status.Active
        override val status: Subject.Status
            get() = _status
        override var value: B
            get() = read(this@mapWithExisting.value)
            set(value) { this@mapWithExisting.onNext(write(this@mapWithExisting.value, value)) }
    }
}


internal fun <T, B > ObservableCallbacks<T>.observerMap(mapper: (B) -> T): ObservableCallbacks<B> = object : ObservableCallbacks<B> {
    override fun onNext(t: B) = this@observerMap.onNext(mapper(t))
    override fun onError(e: Throwable) = this@observerMap.onError(e)
    override fun onComplete() = this@observerMap.onComplete()
}

private fun <T : Any, B> ObservableCallbacks<T>.observerMapNotNull(mapper: (B) -> T?): ObservableCallbacks<B> = object : ObservableCallbacks<B> {
    override fun onNext(t: B) { mapper(t)?.let { this@observerMapNotNull.onNext(it) } }
    override fun onError(e: Throwable) = this@observerMapNotNull.onError(e)
    override fun onComplete() = this@observerMapNotNull.onComplete()
}

/**
 * Turn an Observable<T> into a Subject<T> using the onWrite provided to create the observer portion of the subject.
 */
fun <T> Observable<T>.withWrite(onWrite: (T) -> Unit): Subject<T> = makeSubject(
    observable = this,
    callBacks = object : ObservableCallbacks<T> {
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
fun <T, B> Observable<T>.switchMapMutable(transformation: (T) -> Subject<B>): Subject<B> =
    object : Subject<B> {
        private var lastValue: Subject<B>? = null
        private var hasObservers = false
        private var error: Throwable? = null
        private var hasCompleted = false

        override fun onNext(b: B) { lastValue?.onNext(b) }
        override fun onError(e: Throwable) {
            error = e
            _status = Subject.Status.Error(e)
            lastValue?.onError(e)
        }

        override fun subscribe(observer: ObservableObserver<B>) {
            this@switchMapMutable.map(transformation).doOnAfterNext { lastValue = it }.switchMap{it}.subscribe(observer)
        }

        override fun onComplete() {
            hasCompleted = true
            _status = Subject.Status.Completed
            lastValue?.onComplete()
        }

        private var _status: Subject.Status = Subject.Status.Active
        override val status: Subject.Status
            get() = _status
    }

fun <T, V : Any> BehaviorSubject<T>.mapWithExistingDefault(
    property: KMutableProperty1<T, V?>,
    defaultValue: V
): BehaviorSubject<V> =
    mapWithExisting(
        read = { property.get(it) ?: defaultValue },
        write = { existing, it -> property.set(existing, it); existing }
    )


fun <S: Subject<T>, T> S.bind(other: Subject<T>): CompositeDisposable {
    var suppress = false
    return CompositeDisposable().apply {
        add(this@bind.subscribe() { value ->
            if (!suppress) {
                suppress = true
                other.onNext(value)
                suppress = false
            }
        })
        add(other.subscribe() { value ->
            if (!suppress) {
                suppress = true
                onNext(value)
                suppress = false
            }
        })
    }

}