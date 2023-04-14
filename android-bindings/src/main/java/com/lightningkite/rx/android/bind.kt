package com.lightningkite.rx.android

import android.view.View
import com.badoo.reaktive.disposable.addTo
import com.badoo.reaktive.observable.observeOn
import com.badoo.reaktive.scheduler.mainScheduler
import com.badoo.reaktive.subject.Subject
import com.lightningkite.rx.bind
import com.lightningkite.rx.withWrite

internal fun <S: Subject<T>, T: Any> S.bindView(view: View, other: Subject<T>): S {
    this.observeOn(mainScheduler)
        .withWrite { this.onNext(it) }
        .bind(other)
        .addTo(view.removed)
    return this
}

