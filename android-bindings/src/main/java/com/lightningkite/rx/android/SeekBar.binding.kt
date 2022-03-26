package com.lightningkite.rx.android

import android.widget.SeekBar
import com.lightningkite.rx.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.Subject
import io.reactivex.rxjava3.kotlin.addTo

/**
 * Binds the value of this to the seekbar's internal value. Changes will propagate both directions
 *
 * Example:
 * val value = ValueSubject<Int>(10)
 * value.bind(seekBarView)
 */
fun <SOURCE: Subject<Int>> SOURCE.bind(
    seekBar: SeekBar
): SOURCE {
    var suppress = false
    observeOn(AndroidSchedulers.mainThread()).subscribeBy { value ->
        if (!suppress) {
            suppress = true
            seekBar.progress = value
            suppress = false
        }
    }.addTo(seekBar.removed)
    seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            if (!suppress) {
                suppress = true
                onNext(p1)
                suppress = false
            }
        }

        override fun onStartTrackingTouch(p0: SeekBar?) {
        }

        override fun onStopTrackingTouch(p0: SeekBar?) {
        }
    })
    return this
}

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
