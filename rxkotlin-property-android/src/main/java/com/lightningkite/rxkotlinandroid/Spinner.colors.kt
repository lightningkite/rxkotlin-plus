package com.lightningkite.rxkotlinandroid

import android.widget.Spinner
import java.util.*

var Spinner.spinnerTextColor: Int
    get() = spinnerTextColorMap[this] ?: 0xFF000000.toInt()
    set(value) {
        spinnerTextColorMap[this] = value
    }
private val spinnerTextColorMap = WeakHashMap<Spinner, Int>()


var Spinner.spinnerTextSize: Double
    get() = spinnerTextSizeMap[this] ?: (context.resources.displayMetrics.scaledDensity * 16.0)
    set(value) {
        spinnerTextSizeMap[this] = value
    }
private val spinnerTextSizeMap = WeakHashMap<Spinner, Double>()
