package com.lightningkite.rxkotlinproperty.android

import android.widget.SeekBar
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.Subject
import io.reactivex.rxjava3.kotlin.addTo

/**
 *
 * Binds the value of the seek bar to the property. You can also state what you want the
 * low and high values the seek bar can have. Any change to the seek bar will set the property
 * as well any changes in the property will manifest in the seek bar.
 *
 */

fun SeekBar.bind(
    property: Subject<Int>
) {
    var suppress = false
    property.subscribeBy { value ->
        if (!suppress) {
            suppress = true
            this.progress = value
            suppress = false
        }
    }.addTo(this@bind.removed)
    this.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            if (!suppress) {
                suppress = true
                property.onNext(p1)
                suppress = false
            }
        }

        override fun onStartTrackingTouch(p0: SeekBar?) {
        }

        override fun onStopTrackingTouch(p0: SeekBar?) {
        }
    })

}
