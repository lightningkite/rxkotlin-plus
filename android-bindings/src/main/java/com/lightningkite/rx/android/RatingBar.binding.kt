package com.lightningkite.rx.android

import android.widget.RatingBar
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.subject.Subject
import com.lightningkite.rx.withWrite


fun RatingBar.ratingChanges(): Observable<Float> {
    return RatingBarRatingChangeObservable(this)
}

private class RatingBarRatingChangeObservable(
    private val view: RatingBar
) : Observable<Float> {

    override fun subscribe(observer: ObservableObserver<Float>) {
        val listener = Listener(view, observer)
        view.onRatingBarChangeListener = listener
        observer.onSubscribe(listener)
    }

    private class Listener(
        private val view: RatingBar,
        private val observer: ObservableObserver<Float>
    ) : RatingBar.OnRatingBarChangeListener, Disposable {

        override fun onRatingChanged(ratingBar: RatingBar, rating: Float, fromUser: Boolean) {
            if (!isDisposed) {
                observer.onNext(rating)
            }
        }

        private var disposed: Boolean = false
        override val isDisposed: Boolean
            get() = disposed

        override fun dispose() {
            disposed = true
            view.setOnLongClickListener(null)
        }
    }
}

/**
 *
 * Binds the rating bar to the property provided.
 */
fun <SOURCE: Subject<Float>> SOURCE.bind(
    ratingBar: RatingBar
): SOURCE = bindView(ratingBar, ratingBar.ratingChanges().withWrite { ratingBar.rating = it })