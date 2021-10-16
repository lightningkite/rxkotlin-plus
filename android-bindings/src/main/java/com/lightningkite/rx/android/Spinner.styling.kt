package com.lightningkite.rx.android

import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.Spinner
import android.widget.TextView
import java.util.*

data class SpinnerTextStyle(
    val textColor: Int,
    val textSize: Float,
    val paddingLeft: Int,
    val paddingTop: Int,
    val paddingRight: Int,
    val paddingBottom: Int,
    val typeface: Typeface?,
    val letterSpacing: Float?,
    val lineSpacingMultiplier: Float?,
) {
    fun apply(to: TextView) {
        to.setTextColor(textColor)
        to.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        to.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
        letterSpacing?.let { to.letterSpacing = it }
        lineSpacingMultiplier?.let { to.setLineSpacing(0f, it) }
        typeface?.let { to.typeface = it }
    }
}

var Spinner.spinnerTextStyle: SpinnerTextStyle?
    get() = spinnerTextStyleMap[this]
    set(value) {
        spinnerTextStyleMap[this] = value
    }
private val spinnerTextStyleMap = WeakHashMap<Spinner, SpinnerTextStyle>()
