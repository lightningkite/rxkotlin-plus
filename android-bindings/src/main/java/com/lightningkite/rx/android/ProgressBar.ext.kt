package com.lightningkite.rx.android

import android.widget.ProgressBar

var ProgressBar.progressRatio: Float
    get() = this.progress / this.max.toFloat()
    set(value) {
        this.max = 10000
        this.progress = (10000 * value).toInt()
    }