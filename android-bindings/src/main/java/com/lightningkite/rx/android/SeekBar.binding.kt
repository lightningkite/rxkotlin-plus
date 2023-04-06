package com.lightningkite.rx.android

import android.widget.SeekBar
import com.badoo.reaktive.subject.Subject
import com.lightningkite.rx.*

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
