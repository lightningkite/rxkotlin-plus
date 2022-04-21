package com.lightningkite.rx.android

import android.view.View
import com.lightningkite.rx.bind
import com.lightningkite.rx.withWrite
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.subjects.Subject

internal fun <S: Subject<T>, T: Any> S.bindView(view: View, other: Subject<T>): S {
    this.observeOn(RequireMainThread)
        .withWrite { this.onNext(it) }
        .bind(other)
        .addTo(view.removed)
    return this
}

