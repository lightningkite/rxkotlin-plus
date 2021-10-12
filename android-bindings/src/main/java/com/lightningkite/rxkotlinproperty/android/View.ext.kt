package com.lightningkite.rxkotlinproperty.android

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.kotlin.addTo

fun View.onClick(disabledMilliseconds: Long = 500, action: () -> Unit) {
    var lastActivated = System.currentTimeMillis()
    setOnClickListener {
        if(System.currentTimeMillis() - lastActivated > disabledMilliseconds) {
            action()
            lastActivated = System.currentTimeMillis()
        }
    }
}

fun View.onLongClick(action: () -> Unit) {
    setOnLongClickListener { action(); true }
}

fun View.replace(other: View) {
    val parent = (this.parent as ViewGroup)
    other.layoutParams = this.layoutParams
    val index = parent.indexOfChild(this)
    parent.removeView(this)
    parent.addView(other, index)
}

fun <VIEW: View, TYPE: Any> VIEW.bind(observable: Observable<TYPE>, setter: VIEW.(TYPE)->Unit) {
    observable.subscribeBy {
        setter(it)
    }.addTo(this.removed)
}

fun test(textView: TextView, prop: Observable<Int>) {
    textView.bind(prop.map { it.toString() }) { text = it }
}