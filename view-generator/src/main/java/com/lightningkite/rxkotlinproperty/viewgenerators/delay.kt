package com.lightningkite.rxkotlinproperty.viewgenerators

import android.os.Handler
import android.os.Looper
import io.reactivex.rxjava3.subjects.PublishSubject

fun delay(milliseconds: Long, action: () -> Unit) {
    if (milliseconds == 0L) action()
    else Handler(Looper.getMainLooper()).postDelayed(action, milliseconds)
}

fun post(action: () -> Unit) {
    Handler(Looper.getMainLooper()).post(action)
}

val animationFrame: PublishSubject<Float> = PublishSubject.create()
