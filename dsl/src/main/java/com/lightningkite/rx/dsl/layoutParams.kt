package com.lightningkite.rx.dsl

import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.GravityInt
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding


internal val unsetSize = -3
internal val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()

internal val View.lparams: ViewGroup.MarginLayoutParams get() = (layoutParams as? ViewGroup.MarginLayoutParams) ?: run {
    val n = when(val it = layoutParams) {
        null -> ViewGroup.MarginLayoutParams(unsetSize, unsetSize)
        is LinearLayout.LayoutParams -> ViewGroup.MarginLayoutParams(it)
        is FrameLayout.LayoutParams -> ViewGroup.MarginLayoutParams(it)
        is ViewGroup.MarginLayoutParams -> ViewGroup.MarginLayoutParams(it)
        else -> ViewGroup.MarginLayoutParams(it)
    }
    this.layoutParams = n
    n
}
internal val View.llparams: LinearLayout.LayoutParams get() = (layoutParams as? LinearLayout.LayoutParams) ?: run {
    val n = when(val it = layoutParams) {
        null -> LinearLayout.LayoutParams(unsetSize, unsetSize)
        is LinearLayout.LayoutParams -> LinearLayout.LayoutParams(it)
        is FrameLayout.LayoutParams -> LinearLayout.LayoutParams(it)
        is ViewGroup.MarginLayoutParams -> LinearLayout.LayoutParams(it)
        else -> LinearLayout.LayoutParams(it)
    }
    this.layoutParams = n
    n
}
internal val View.flparams: FrameLayout.LayoutParams get() = (layoutParams as? FrameLayout.LayoutParams) ?: run {
    val n = when(val it = layoutParams) {
        null -> FrameLayout.LayoutParams(unsetSize, unsetSize)
        is LinearLayout.LayoutParams -> FrameLayout.LayoutParams(it)
        is FrameLayout.LayoutParams -> FrameLayout.LayoutParams(it)
        is ViewGroup.MarginLayoutParams -> FrameLayout.LayoutParams(it)
        else -> FrameLayout.LayoutParams(it)
    }
    this.layoutParams = n
    n
}
@RxKotlinViewDsl fun <T: View> T.weight(value: Float) = this.apply { llparams.weight = value }
@RxKotlinViewDsl fun <T: View> T.width(value: Int) = this.apply { lparams.width = value.dp }
@RxKotlinViewDsl fun <T: View> T.matchWidth() = this.apply { lparams.width = ViewGroup.LayoutParams.MATCH_PARENT }
@RxKotlinViewDsl fun <T: View> T.height(value: Int) = this.apply { lparams.height = value.dp }
@RxKotlinViewDsl fun <T: View> T.matchHeight() = this.apply { lparams.height = ViewGroup.LayoutParams.MATCH_PARENT }
@RxKotlinViewDsl fun <T: View> T.frameGravity(@GravityInt gravity: Int) = this.apply { flparams.gravity = gravity }
@RxKotlinViewDsl fun <T: View> T.align(@GravityInt gravity: Int) = this.apply { llparams.gravity = gravity }
@RxKotlinViewDsl fun <T: View> T.pad(value: Int) = this.apply { setPadding(value.dp) }
@RxKotlinViewDsl fun <T: View> T.margin(value: Int) = this.apply { lparams.setMargins(value.dp) }
@RxKotlinViewDsl fun <T: View> T.hpad(value: Int) = this.apply { updatePadding(left = value.dp, right = value.dp) }
@RxKotlinViewDsl fun <T: View> T.hmargin(value: Int) = this.apply { lparams.updateMargins(left = value.dp, right = value.dp) }
@RxKotlinViewDsl fun <T: View> T.vpad(value: Int) = this.apply { updatePadding(top = value.dp, bottom = value.dp) }
@RxKotlinViewDsl fun <T: View> T.vmargin(value: Int) = this.apply { lparams.updateMargins(top = value.dp, bottom = value.dp) }