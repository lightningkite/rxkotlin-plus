package com.lightningkite.rxkotlinproperty.android

import android.widget.ProgressBar
import com.lightningkite.rxkotlinproperty.Property
import com.lightningkite.rxkotlinproperty.subscribeBy
import com.lightningkite.rxkotlinproperty.until

fun ProgressBar.bindInt(
    property: Property<Int>
){
    property.subscribeBy { value ->
        this.progress = value
    }.until(this@bindInt.removed)
}

fun ProgressBar.bindLong(
    property: Property<Long>
){
    property.subscribeBy { value ->
        this.progress = value.toInt()
    }.until(this@bindLong.removed)
}

fun ProgressBar.bindFloat(
    property: Property<Float>
){
    property.subscribeBy { value ->
        this.progress = (value.coerceIn(0.0f, 1.0f) * 100).toInt()
    }.until(this@bindFloat.removed)
}