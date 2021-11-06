package com.lightningkite.rx.android

import android.widget.SeekBar
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.Subject
import io.reactivex.rxjava3.kotlin.addTo

/**
 * Binds the value of this to the seekbars internal value. Changes will propagate both directions
 *
 * Example:
 * val value = ValueSubject<Int>(10)
 * value.bind(seekBarView)
 */
fun <SOURCE: Subject<Int>> SOURCE.bind(
    seekBar: SeekBar
): SOURCE {
    var suppress = false
    subscribeBy { value ->
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
