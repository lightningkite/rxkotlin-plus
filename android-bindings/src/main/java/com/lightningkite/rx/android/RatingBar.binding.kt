package com.lightningkite.rx.android

import android.widget.RatingBar
import com.jakewharton.rxbinding4.widget.ratingChanges
import com.lightningkite.rx.withWrite
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.Subject
import io.reactivex.rxjava3.kotlin.addTo

/**
 *
 * Binds the rating bar to the property provided.
 */
fun <SOURCE: Subject<Float>> SOURCE.bind(
    ratingBar: RatingBar
): SOURCE = bindView(ratingBar, ratingBar.ratingChanges().withWrite { ratingBar.rating = it })