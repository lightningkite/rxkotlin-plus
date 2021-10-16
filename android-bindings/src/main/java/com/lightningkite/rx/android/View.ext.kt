package com.lightningkite.rx.android

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.view.longClicks
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.kotlin.addTo
import java.util.concurrent.TimeUnit
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1

fun <SOURCE: Single<TYPE>, VIEW: View, TYPE: Any> SOURCE.subscribeAutoDispose(view: VIEW, setter: VIEW.(TYPE)->Unit): SOURCE {
    subscribeBy {
        setter(view, it)
    }.addTo(view.removed)
    return this
}

fun <SOURCE: Maybe<TYPE>, VIEW: View, TYPE: Any> SOURCE.subscribeAutoDispose(view: VIEW, setter: VIEW.(TYPE)->Unit): SOURCE {
    subscribeBy {
        setter(view, it)
    }.addTo(view.removed)
    return this
}

fun <SOURCE: Observable<TYPE>, VIEW: View, TYPE: Any> SOURCE.subscribeAutoDispose(view: VIEW, setter: VIEW.(TYPE)->Unit): SOURCE {
    subscribeBy {
        setter(view, it)
    }.addTo(view.removed)
    return this
}

fun <SOURCE: Observable<TYPE>, VIEW: View, TYPE: Any> SOURCE.subscribeAutoDispose(view: VIEW, setter: KMutableProperty1<VIEW, TYPE>): SOURCE {
    subscribeBy {
        setter.set(view, it)
    }.addTo(view.removed)
    return this
}

fun View.onClick(action: ()->Unit) {
    clicks().throttleFirst(500L, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribe { action() }.addTo(this.removed)
}
fun View.onLongClick(action: ()->Unit) {
    longClicks().throttleFirst(500L, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribe { action() }.addTo(this.removed)
}