package com.lightningkite.rx.android

import android.widget.RatingBar
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.Subject
import io.reactivex.rxjava3.kotlin.addTo

/**
 *
 * Binds the rating bar to the property provided.
 */

fun <SOURCE: Subject<Float>> SOURCE.bind(
    view: RatingBar
): SOURCE {
    var suppress = false
    subscribeBy { value ->
        if (!suppress) {
            suppress = true
            view.rating = value
            suppress = false
        }
    }.addTo(view.removed)
    view.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { p0, p1, p2 ->
        if (!suppress) {
            suppress = true
            onNext(p1)
            suppress = false
        }
    }
    return this
}
