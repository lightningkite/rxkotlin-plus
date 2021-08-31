package com.lightningkite.rxkotlinproperty.android

import android.widget.SeekBar
import com.lightningkite.rxkotlinproperty.MutableProperty
import com.lightningkite.rxkotlinproperty.subscribeBy
import com.lightningkite.rxkotlinproperty.until

/**
 *
 * Binds the value of the seek bar to the property. You can also state what you want the
 * low and high values the seek bar can have. Any change to the seek bar will set the property
 * as well any changes in the property will manifest in the seek bar.
 *
 */

fun SeekBar.bind(
    start: Int,
    endInclusive: Int,
    property: MutableProperty<Int>
) {
    this.max = endInclusive - start
    this.incrementProgressBy(1)

    var suppress = false
    property.subscribeBy { value ->
        if (!suppress) {
            suppress = true
            this.progress = value - start
            suppress = false
        }
    }.until(this@bind.removed)
    this.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            if (!suppress) {
                suppress = true
                property.value = p1 + start
                suppress = false
            }
        }

        override fun onStartTrackingTouch(p0: SeekBar?) {
        }

        override fun onStopTrackingTouch(p0: SeekBar?) {
        }
    })

}
