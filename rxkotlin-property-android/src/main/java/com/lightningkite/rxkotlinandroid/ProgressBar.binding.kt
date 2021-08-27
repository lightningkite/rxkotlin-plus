package com.lightningkite.rxkotlinandroid

import android.widget.ProgressBar
import com.lightningkite.rxkotlinproperty.until
import com.lightningkite.rxkotlinproperty.Property
import com.lightningkite.rxkotlinproperty.subscribeBy

fun ProgressBar.bindInt(
    observable: Property<Int>
){
    observable.subscribeBy { value ->
        this.progress = value
    }.until(this@bindInt.removed)
}

fun ProgressBar.bindLong(
    observable: Property<Long>
){
    observable.subscribeBy { value ->
        this.progress = value.toInt()
    }.until(this@bindLong.removed)
}

fun ProgressBar.bindFloat(
    observable: Property<Float>
){
    observable.subscribeBy { value ->
        this.progress = (value.coerceIn(0.0f, 1.0f) * 100).toInt()
    }.until(this@bindFloat.removed)
}