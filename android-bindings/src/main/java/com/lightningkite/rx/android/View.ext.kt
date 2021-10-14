package com.lightningkite.rx.android

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.kotlin.addTo
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1

fun <SOURCE: Single<TYPE>, VIEW: View, TYPE: Any> SOURCE.subscribeView(view: VIEW, setter: VIEW.(TYPE)->Unit): SOURCE {
    subscribeBy {
        setter(view, it)
    }.addTo(view.removed)
    return this
}

fun <SOURCE: Maybe<TYPE>, VIEW: View, TYPE: Any> SOURCE.subscribeView(view: VIEW, setter: VIEW.(TYPE)->Unit): SOURCE {
    subscribeBy {
        setter(view, it)
    }.addTo(view.removed)
    return this
}

fun <SOURCE: Observable<TYPE>, VIEW: View, TYPE: Any> SOURCE.subscribeView(view: VIEW, setter: VIEW.(TYPE)->Unit): SOURCE {
    subscribeBy {
        setter(view, it)
    }.addTo(view.removed)
    return this
}

fun <SOURCE: Observable<TYPE>, VIEW: View, TYPE: Any> SOURCE.subscribeView(view: VIEW, setter: KMutableProperty1<VIEW, TYPE>): SOURCE {
    subscribeBy {
        setter.set(view, it)
    }.addTo(view.removed)
    return this
}

private fun test(button: Button) {
    Observable.just(true)
        .subscribeView(button, Button::visible)
        .map { it.toString() }
        .subscribeView(button, Button::setText)
}