package com.lightningkite.rx.android

import android.widget.RatingBar
import com.badoo.reaktive.subject.Subject
import com.lightningkite.rx.withWrite

/**
 *
 * Binds the rating bar to the property provided.
 */
fun <SOURCE: Subject<Float>> SOURCE.bind(
    ratingBar: RatingBar
): SOURCE = bindView(ratingBar, ratingBar.ratingChanges().withWrite { ratingBar.rating = it })