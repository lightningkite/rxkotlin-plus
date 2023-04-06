package com.lightningkite.rx.viewgenerators

import android.os.Handler
import android.os.Looper
import com.badoo.reaktive.subject.publish.PublishSubject

/**
 * Shortcut for calling [Handler.postDelayed]
 */
fun delay(milliseconds: Long, action: () -> Unit) {
    if (milliseconds == 0L) action()
    else Handler(Looper.getMainLooper()).postDelayed(action, milliseconds)
}

/**
 * Shortcut for calling [Handler.post]
 */
fun post(action: () -> Unit) {
    Handler(Looper.getMainLooper()).post(action)
}

/**
 * A subject that is called every animation frame with the time elapsed since the last frame in seconds.
 */
val animationFrame: PublishSubject<Float> = PublishSubject()
