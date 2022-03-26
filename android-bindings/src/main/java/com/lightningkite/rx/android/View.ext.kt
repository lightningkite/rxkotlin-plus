package com.lightningkite.rx.android

import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.view.longClicks
import com.lightningkite.rx.kotlin
import com.lightningkite.rx.subscribeByNullable
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.kotlin.addTo
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.reflect.KMutableProperty1

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
fun <SOURCE: Single<TYPE>, VIEW: View, TYPE: Any> SOURCE.subscribeAutoDispose(view: VIEW, setter: VIEW.(TYPE)->Unit): SOURCE {
    observeOn(AndroidSchedulers.mainThread()).subscribeBy {
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
fun <SOURCE: Maybe<TYPE>, VIEW: View, TYPE: Any> SOURCE.subscribeAutoDispose(view: VIEW, setter: VIEW.(TYPE)->Unit): SOURCE {
    observeOn(AndroidSchedulers.mainThread()).subscribeBy {
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
fun <SOURCE: Observable<TYPE>, VIEW: View, TYPE: Any> SOURCE.subscribeAutoDispose(view: VIEW, setter: VIEW.(TYPE)->Unit): SOURCE {
    observeOn(AndroidSchedulers.mainThread()).subscribeBy {
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
fun <SOURCE: Observable<TYPE>, VIEW: View, TYPE: Any> SOURCE.subscribeAutoDispose(view: VIEW, setter: KMutableProperty1<VIEW, TYPE>): SOURCE {
    observeOn(AndroidSchedulers.mainThread()).subscribeBy {
        setter.set(view, it)
    }.addTo(view.removed)
    return this
}

/**
 * One way binding of this to a view. Unwrapping of the Optional is automatically handled
 * <p>
 * subscribeAutoDispose will subscribe to this, and add the disposable to the views removed CompositeDisposable.
 * The setter provided is meant to manage changes to the view according to the value of this.
 *
 * Example:
 * val value = Single.just<Optional<String>>("Hi")
 * values.subscribeAutoDispose(textView){ setText(it) }
 */
fun <SOURCE: Single<Optional<TYPE>>, VIEW: View, TYPE: Any> SOURCE.subscribeAutoDisposeNullable(view: VIEW, setter: VIEW.(TYPE?)->Unit): SOURCE {
    observeOn(AndroidSchedulers.mainThread()).subscribeByNullable {
        setter(view, it)
    }.addTo(view.removed)
    return this
}

/**
 * One way binding of this to a view. Unwrapping of the Optional is automatically handled
 * <p>
 * subscribeAutoDispose will subscribe to this, and add the disposable to the views removed CompositeDisposable.
 * The setter provided is meant to manage changes to the view according to the value of this.
 *
 * Example:
 * val value: Maybe<Optional<String>> ...
 * values.subscribeAutoDispose(textView){ setText(it) }
 */
fun <SOURCE: Maybe<Optional<TYPE>>, VIEW: View, TYPE: Any> SOURCE.subscribeAutoDisposeNullable(view: VIEW, setter: VIEW.(TYPE?)->Unit): SOURCE {
    observeOn(AndroidSchedulers.mainThread()).subscribeByNullable {
        setter(view, it)
    }.addTo(view.removed)
    return this
}

/**
 * One way binding of this to a view. Unwrapping of the Optional is automatically handled
 * <p>
 * subscribeAutoDispose will subscribe to this, and add the disposable to the views removed CompositeDisposable.
 * The setter provided is meant to manage changes to the view according to the value of this.
 *
 * Example:
 * val value = ValueSubject<Optional<String>>("Hi")
 * values.subscribeAutoDispose(textView){ setText(it) }
 */
fun <SOURCE: Observable<Optional<TYPE>>, VIEW: View, TYPE: Any> SOURCE.subscribeAutoDisposeNullable(view: VIEW, setter: VIEW.(TYPE?)->Unit): SOURCE {
    observeOn(AndroidSchedulers.mainThread()).subscribeByNullable {
        setter(view, it)
    }.addTo(view.removed)
    return this
}

/**
 * One way binding of this to a view's property. Unwrapping of the Optional is automatically handled
 * <p>
 * subscribeAutoDispose will subscribe to this, and add the disposable to the views removed CompositeDisposable.
 * The setter will be called with the value of this.
 *
 * Example:
 * val value = ValueSubject<Optional<String>>("Hi")
 * values.subscribeAutoDispose(textView, TextView::setText)
 */
fun <SOURCE: Observable<Optional<TYPE>>, VIEW: View, TYPE: Any> SOURCE.subscribeAutoDisposeNullable(view: VIEW, setter: KMutableProperty1<VIEW, TYPE?>): SOURCE {
    observeOn(AndroidSchedulers.mainThread()).subscribeByNullable {
        setter.set(view, it)
    }.addTo(view.removed)
    return this
}

/**
 * Subscribes to the view clicks observable which is fired any time the view is clicked. The action provided will be called on click.
 * disabledMilliseconds is the time before it can be pressed again.
 */
fun View.onClick(disabledMilliseconds:Long = 500L, action: ()->Unit) {
    clicks().throttleFirst(disabledMilliseconds, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribe { action() }.addTo(this.removed)
}

/**
 * Subscribes to the view clicks observable which is fired any time the view is clicked.
 * The value of the observable will be passed into the action provided.
 * This is designed to work with ValueSubjects or BehaviorSubject where a value is
 * immediately available.
 * disabledMilliseconds is the time before it can be pressed again.
 */
fun <T: Any> View.onClick(observable:Observable<T>, disabledMilliseconds:Long = 500L, action: (T)->Unit) {
    clicks().throttleFirst(disabledMilliseconds, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).flatMap{observable.take(1)}.subscribe { action(it) }.addTo(this.removed)
}

/**
 * Subscribes to the view clicks observable which is fired any time the view is clicked.
 * The value of the observable will be passed into the action provided.
 * This is designed to work with ValueSubjects or BehaviorSubject where a value is
 * immediately available. Unwrapping the optional is automatically handled.
 * disabledMilliseconds is the time before it can be pressed again.
 */
fun <T: Any> View.onClickNullable(observable:Observable<Optional<T>>, disabledMilliseconds:Long = 500L, action: (T?)->Unit) {
    clicks().throttleFirst(disabledMilliseconds, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).flatMap{observable.take(1)}.subscribe { action(it.kotlin) }.addTo(this.removed)
}

/**
 * Subscribes to the view longClicks observable which is fired any time the view is longPressed. The action provided will be called on click.
 * disabledMilliseconds is the time before it can be pressed again.
 */
fun View.onLongClick(disabledMilliseconds:Long = 500L, action: ()->Unit) {
    longClicks().throttleFirst(disabledMilliseconds, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribe { action() }.addTo(this.removed)
}

/**
 * Subscribes to the view longClicks observable which is fired any time the view is longPressed.
 * The value of the observable will be passed into the action provided.
 * This is designed to work with ValueSubjects or BehaviorSubject where a value is
 * immediately available.
 * disabledMilliseconds is the time before it can be pressed again.
 */
fun <T: Any> View.onLongClick(observable:Observable<T>, disabledMilliseconds:Long = 500L, action: (T)->Unit){
    longClicks().throttleFirst(disabledMilliseconds, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).flatMap{observable.take(1)}.subscribe{ action(it) }.addTo(this.removed)
}

/**
 * Subscribes to the view longClicks observable which is fired any time the view is longPressed.
 * The value of the observable will be passed into the action provided.
 * This is designed to work with ValueSubjects or BehaviorSubject where a value is
 * immediately available. Unwrapping the optional is automatically handled.
 * disabledMilliseconds is the time before it can be pressed again.
 */
fun <T: Any> View.onLongClickNullable(observable:Observable<Optional<T>>, disabledMilliseconds:Long = 500L, action: (T?)->Unit){
    longClicks().throttleFirst(disabledMilliseconds, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).flatMap{observable.take(1)}.subscribe{ action(it.kotlin) }.addTo(this.removed)
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