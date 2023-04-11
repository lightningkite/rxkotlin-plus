package com.lightningkite.rx.android

import android.widget.SeekBar
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.subject.Subject
import com.lightningkite.rx.*

fun SeekBar.changes(): Observable<Int> {
    return SeekBarChangeObservable(this, null)
}

private class SeekBarChangeObservable(
    private val view: SeekBar,
    private val shouldBeFromUser: Boolean?
) : Observable<Int> {

    override fun subscribe(observer: ObservableObserver<Int>) {
        val listener = Listener(view, shouldBeFromUser, observer)
        view.setOnSeekBarChangeListener(listener)
        observer.onSubscribe(listener)
    }


    private class Listener(
        private val view: SeekBar,
        private val shouldBeFromUser: Boolean?,
        private val observer: ObservableObserver<Int>
    ) : SeekBar.OnSeekBarChangeListener, Disposable {

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            if (!isDisposed && (shouldBeFromUser == null || shouldBeFromUser == fromUser)) {
                observer.onNext(progress)
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {}

        override fun onStopTrackingTouch(seekBar: SeekBar) {}


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
 * Binds the value of this to the seekbar's internal value. Changes will propagate both directions
 *
 * Example:
 * val value = ValueSubject<Int>(10)
 * value.bind(seekBarView)
 */
fun <SOURCE: Subject<Int>> SOURCE.bind(
    seekBar: SeekBar
): SOURCE = bindView(seekBar, seekBar.changes().withWrite { seekBar.progress = it })

/**
 * Binds the value of this to the seekbar's internal value. Changes will propagate both directions.
 *
 * Example:
 * val value = ValueSubject<Int>(10)
 * value.bind(seekBarView)
 */
fun <SOURCE: Subject<Int>> SOURCE.bind(
    seekBar: SeekBar,
    range: IntRange
): SOURCE {
    seekBar.max = range.last - range.first
    this.minus(range.first).bind(seekBar)
    return this
}

/**
 * Binds the value of this to the seekbar's internal value. Changes will propagate both directions.
 *
 * Example:
 * val value = ValueSubject<Int>(10)
 * value.bind(seekBarView)
 */
@JvmName("bindRatio")
fun <SOURCE: Subject<Float>> SOURCE.bind(
    seekBar: SeekBar
): SOURCE {
    val scale = 10_000
    seekBar.max = scale
    this.times(scale.toFloat()).toSubjectInt().bind(seekBar)
    return this
}
