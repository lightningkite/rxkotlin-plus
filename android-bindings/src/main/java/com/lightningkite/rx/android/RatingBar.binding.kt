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
    ratingBar: RatingBar
): SOURCE {
    var suppress = false
    subscribeBy { value ->
        if (!suppress) {
            suppress = true
            ratingBar.rating = value
            suppress = false
        }
    }.addTo(ratingBar.removed)
    ratingBar.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { _, p1, _ ->
        if (!suppress) {
            suppress = true
            onNext(p1)
            suppress = false
        }
    }
    return this
}
