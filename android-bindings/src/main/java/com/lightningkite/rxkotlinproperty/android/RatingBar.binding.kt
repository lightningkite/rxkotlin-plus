package com.lightningkite.rxkotlinproperty.android

import android.widget.RatingBar
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.Subject
import io.reactivex.rxjava3.kotlin.addTo

/**
 *
 * Binds the rating bar to the property provided.
 */

fun RatingBar.bind(
    property: Subject<Float>
) {
    var suppress = false
    property.subscribeBy { value ->
        if (!suppress) {
            suppress = true
            this.rating = value
            suppress = false
        }
    }.addTo(this.removed)
    this.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { p0, p1, p2 ->
        if (!suppress) {
            suppress = true
            property.onNext(p1)
            suppress = false
        }
    }

}
