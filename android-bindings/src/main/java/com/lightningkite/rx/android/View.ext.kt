package com.lightningkite.rx.android

import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.observeOn
import com.badoo.reaktive.completable.subscribe
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.addTo
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.observeOn
import com.badoo.reaktive.maybe.subscribe
import com.badoo.reaktive.observable.*
import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.observeOn
import com.badoo.reaktive.single.subscribe
import java.util.concurrent.TimeUnit
import kotlin.reflect.KMutableProperty1


/**
 * Create an observable which emits on `view` click events. The emitted value is
 * unspecified and should only be used as notification.
 *
 * *Warning:* The created observable keeps a strong reference to `view`. Unsubscribe
 * to free this reference.
 *
 * *Warning:* The created observable uses [View.setOnClickListener] to observe
 * clicks. Only one observable can be used for a view at a time.
 */
@CheckResult
fun View.clicks(): Observable<Unit> {
    return ViewClickObservable(this)
}

private class ViewClickObservable(
    private val view: View
) : Observable<Unit> {

    private class Listener(
        private val view: View,
        private val observer: ObservableCallbacks<Unit>
    ) : View.OnClickListener, Disposable {

        override fun onClick(v: View) {
            observer.onNext(Unit)
        }

        private var disposed: Boolean = false
        override val isDisposed: Boolean
            get() = disposed

        override fun dispose() {
            disposed = true
            view.setOnClickListener(null)
        }
    }

    override fun subscribe(observer: ObservableObserver<Unit>) {
        val listener = Listener(view, observer)
        observer.onSubscribe(listener)
        view.setOnClickListener(listener)
    }
}

fun View.longClicks(handled: () -> Boolean = { true }): Observable<Unit> {
    return ViewLongClickObservable(this, handled)
}

private class ViewLongClickObservable(
    private val view: View,
    private val handled: () -> Boolean
) : Observable<Unit> {

    private class Listener(
        private val view: View,
        private val handled: () -> Boolean,
        private val observer: ObservableCallbacks<Unit>
    ) : View.OnLongClickListener, Disposable {

        override fun onLongClick(v: View): Boolean {
            if (!isDisposed) {
                try {
                    if (handled()) {
                        observer.onNext(Unit)
                        return true
                    }
                } catch (e: Exception) {
                    observer.onError(e)
                    dispose()
                }

            }
            return false
        }

        private var disposed: Boolean = false
        override val isDisposed: Boolean
            get() = disposed

        override fun dispose() {
            disposed = true
            view.setOnLongClickListener(null)
        }
    }

    override fun subscribe(observer: ObservableObserver<Unit>) {
        val listener = Listener(view, handled, observer)
        observer.onSubscribe(listener)
        view.setOnLongClickListener(listener)
    }
}


/**
 * One way binding of this to a view.
 * <p>
 * subscribeAutoDispose will subscribe to this, and add the disposable to the views removed CompositeDisposable.
 * The setter provided is meant to manage changes to the view according to the value of this.
 *
 * Example:
 * val value = Completable.complete()
 * values.subscribeAutoDispose(textView){ action() }
 */
fun <SOURCE : Completable, VIEW : View> SOURCE.subscribeAutoDispose(view: VIEW, action: VIEW.() -> Unit = {}): SOURCE {
    observeOn(RequireMainThread).subscribe {
        action(view)
    }.addTo(view.removed)
    return this
}

/**
 * One way binding of this to a view.
 * <p>
 * subscribeAutoDispose will subscribe to this, and add the disposable to the views removed CompositeDisposable.
 * The setter provided is meant to manage changes to the view according to the value of this.
 *
 * Example:
 * val value = Single.just<String>("Hi")
 * values.subscribeAutoDispose(textView){ setText(it) }
 */
fun <SOURCE : Single<TYPE>, VIEW : View, TYPE> SOURCE.subscribeAutoDispose(
    view: VIEW,
    setter: VIEW.(TYPE) -> Unit
): SOURCE {
    observeOn(RequireMainThread).subscribe {
        setter(view, it)
    }.addTo(view.removed)
    return this
}

/**
 * One way binding of this to a view.
 * <p>
 * subscribeAutoDispose will subscribe to this, and add the disposable to the views removed CompositeDisposable.
 * The setter provided is meant to manage changes to the view according to the value of this.
 *
 * Example:
 * val value: Maybe<String> ...
 * values.subscribeAutoDispose(textView){ setText(it) }
 */
fun <SOURCE : Maybe<TYPE>, VIEW : View, TYPE> SOURCE.subscribeAutoDispose(
    view: VIEW,
    setter: VIEW.(TYPE) -> Unit
): SOURCE {
    observeOn(RequireMainThread).subscribe {
        setter(view, it)
    }.addTo(view.removed)
    return this
}

/**
 * One way binding of this to a view.
 * <p>
 * subscribeAutoDispose will subscribe to this, and add the disposable to the views removed CompositeDisposable.
 * The setter provided is meant to manage changes to the view according to the value of this.
 *
 * Example:
 * val value = ValueSubject<String>("Hi")
 * values.subscribeAutoDispose(textView){ setText(it) }
 */
fun <SOURCE : Observable<TYPE>, VIEW : View, TYPE> SOURCE.subscribeAutoDispose(
    view: VIEW,
    setter: VIEW.(TYPE) -> Unit
): SOURCE {
    observeOn(RequireMainThread).subscribe {
        setter(view, it)
    }.addTo(view.removed)
    return this
}

/**
 * One way binding of this to a view's property.
 * <p>
 * subscribeAutoDispose will subscribe to this, and add the disposable to the views removed CompositeDisposable.
 * The setter will be called with the value of this.
 *
 * Example:
 * val value = ValueSubject<String>("Hi")
 * values.subscribeAutoDispose(textView, TextView::setText)
 */
fun <SOURCE : Observable<TYPE>, VIEW : View, TYPE> SOURCE.subscribeAutoDispose(
    view: VIEW,
    setter: KMutableProperty1<VIEW, TYPE>
): SOURCE {
    observeOn(RequireMainThread).subscribe {
        setter.set(view, it)
    }.addTo(view.removed)
    return this
}


/**
 * One way binding of this to a view.
 * <p>
 * subscribeAutoDispose will subscribe to this, and add the disposable to the views removed CompositeDisposable.
 * The setter provided is meant to manage changes to the view according to the value of this.
 *
 * Purely an alias for [subscribeAutoDispose]
 *
 * Example:
 * val value = Completable.complete()
 * values.subscribeAutoDispose(textView){ action() }
 */
fun <SOURCE : Completable, VIEW : View> SOURCE.into(view: VIEW, action: VIEW.() -> Unit = {}): SOURCE =
    subscribeAutoDispose(view, action)

/**
 * One way binding of this to a view.
 * <p>
 * subscribeAutoDispose will subscribe to this, and add the disposable to the views removed CompositeDisposable.
 * The setter provided is meant to manage changes to the view according to the value of this.
 *
 * Purely an alias for [subscribeAutoDispose]
 *
 * Example:
 * val value = Single.just<String>("Hi")
 * values.subscribeAutoDispose(textView){ setText(it) }
 */
fun <SOURCE : Single<TYPE>, VIEW : View, TYPE> SOURCE.into(view: VIEW, setter: VIEW.(TYPE) -> Unit): SOURCE =
    subscribeAutoDispose(view, setter)

/**
 * One way binding of this to a view.
 * <p>
 * subscribeAutoDispose will subscribe to this, and add the disposable to the views removed CompositeDisposable.
 * The setter provided is meant to manage changes to the view according to the value of this.
 *
 * Purely an alias for [subscribeAutoDispose]
 *
 * Example:
 * val value: Maybe<String> ...
 * values.subscribeAutoDispose(textView){ setText(it) }
 */
fun <SOURCE : Maybe<TYPE>, VIEW : View, TYPE> SOURCE.into(view: VIEW, setter: VIEW.(TYPE) -> Unit): SOURCE =
    subscribeAutoDispose(view, setter)

/**
 * One way binding of this to a view.
 * <p>
 * subscribeAutoDispose will subscribe to this, and add the disposable to the views removed CompositeDisposable.
 * The setter provided is meant to manage changes to the view according to the value of this.
 *
 * Purely an alias for [subscribeAutoDispose]
 *
 * Example:
 * val value = ValueSubject<String>("Hi")
 * values.subscribeAutoDispose(textView){ setText(it) }
 */
fun <SOURCE : Observable<TYPE>, VIEW : View, TYPE> SOURCE.into(view: VIEW, setter: VIEW.(TYPE) -> Unit): SOURCE =
    subscribeAutoDispose(view, setter)

/**
 * One way binding of this to a view's property.
 * <p>
 * subscribeAutoDispose will subscribe to this, and add the disposable to the views removed CompositeDisposable.
 * The setter will be called with the value of this.
 *
 * Purely an alias for [subscribeAutoDispose]
 *
 * Example:
 * val value = ValueSubject<String>("Hi")
 * values.subscribeAutoDispose(textView, TextView::setText)
 */
fun <SOURCE : Observable<TYPE>, VIEW : View, TYPE> SOURCE.into(
    view: VIEW,
    setter: KMutableProperty1<VIEW, TYPE>
): SOURCE = subscribeAutoDispose(view, setter)


/**
 * Subscribes to the view clicks observable which is fired any time the view is clicked. The action provided will be called on click.
 * disabledMilliseconds is the time before it can be pressed again.
 */
@Deprecated(
    "Just do it directly instead.",
    ReplaceWith(
        "this.clicks().throttleFirst(disabledMilliseconds, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribe { action() }.addTo(this.removed)",
        "com.jakewharton.rxbinding4.view.clicks"
    )
)
fun <V : View> V.onClick(disabledMilliseconds: Long = 500L, action: () -> Unit): V {
    clicks().throttleLatest(disabledMilliseconds, AndroidSchedulers.mainThread())
        .subscribe { action() }.addTo(this.removed)
    return this
}

/**
 * Subscribes to the view clicks observable which is fired any time the view is clicked.
 * The value of the observable will be passed into the action provided.
 * This is designed to work with ValueSubjects or BehaviorSubject where a value is
 * immediately available.
 * disabledMilliseconds is the time before it can be pressed again.
 */
@Deprecated(
    "Just do it directly instead.",
    ReplaceWith(
        "this.clicks().throttleFirst(disabledMilliseconds, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).flatMap{observable.take(1)}.subscribe { action(it) }.addTo(this.removed)",
        "com.jakewharton.rxbinding4.view.clicks"
    )
)
fun <T> View.onClick(observable: Observable<T>, disabledMilliseconds: Long = 500L, action: (T) -> Unit) {
    clicks().throttleLatest(disabledMilliseconds, AndroidSchedulers.mainThread())
        .flatMap { observable.take(1) }.subscribe { action(it) }.addTo(this.removed)
}

/**
 * Subscribes to the view longClicks observable which is fired any time the view is longPressed. The action provided will be called on click.
 * disabledMilliseconds is the time before it can be pressed again.
 */
@Deprecated(
    "Just do it directly instead.",
    ReplaceWith(
        "this.longClicks().throttleFirst(disabledMilliseconds, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribe { action() }.addTo(this.removed)",
        "com.jakewharton.rxbinding4.view.longClicks"
    )
)
fun View.onLongClick(disabledMilliseconds: Long = 500L, action: () -> Unit) {
    longClicks().throttleLatest(disabledMilliseconds, AndroidSchedulers.mainThread())
        .subscribe { action() }.addTo(this.removed)
}

/**
 * Subscribes to the view longClicks observable which is fired any time the view is longPressed.
 * The value of the observable will be passed into the action provided.
 * This is designed to work with ValueSubjects or BehaviorSubject where a value is
 * immediately available.
 * disabledMilliseconds is the time before it can be pressed again.
 */
@Deprecated(
    "Just do it directly instead.",
    ReplaceWith(
        "this.longClicks().throttleFirst(disabledMilliseconds, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).flatMap{observable.take(1)}.subscribe{ action(it) }.addTo(this.removed)",
        "com.jakewharton.rxbinding4.view.longClicks"
    )
)
fun <T> View.onLongClick(observable: Observable<T>, disabledMilliseconds: Long = 500L, action: (T) -> Unit) {
    longClicks().throttleLatest(disabledMilliseconds, AndroidSchedulers.mainThread())
        .flatMap { observable.take(1) }.subscribe { action(it) }.addTo(this.removed)
}


/**
 * Replaces the current view in the view hierarchy with the view provided.
 */
fun View.replace(other: View) {
    val parent = (this.parent as ViewGroup)
    other.layoutParams = this.layoutParams
    val index = parent.indexOfChild(this)
    parent.removeView(this)
    parent.addView(other, index)
}