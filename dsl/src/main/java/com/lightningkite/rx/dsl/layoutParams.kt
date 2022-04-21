package com.lightningkite.rx.dsl

import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.GravityInt
import androidx.core.view.setMargins
import androidx.core.view.setPadding


val unsetSize = -3
internal val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val View.lparams: ViewGroup.MarginLayoutParams get() = (layoutParams as? ViewGroup.MarginLayoutParams) ?: run {
    val n = if(layoutParams == null) ViewGroup.MarginLayoutParams(unsetSize, unsetSize) else ViewGroup.MarginLayoutParams(layoutParams)
    this.layoutParams = n
    n
}
val View.llparams: LinearLayout.LayoutParams get() = (layoutParams as? LinearLayout.LayoutParams) ?: run {
    val n = when(val it = layoutParams) {
        null -> LinearLayout.LayoutParams(unsetSize, unsetSize)
        is FrameLayout.LayoutParams -> LinearLayout.LayoutParams(it).apply { gravity = it.gravity }
        is ViewGroup.MarginLayoutParams -> LinearLayout.LayoutParams(it)
        else -> LinearLayout.LayoutParams(it)
    }
    this.layoutParams = n
    n
}
val View.flparams: FrameLayout.LayoutParams get() = (layoutParams as? FrameLayout.LayoutParams) ?: run {
    val n = when(val it = layoutParams) {
        null -> FrameLayout.LayoutParams(unsetSize, unsetSize)
        is LinearLayout.LayoutParams -> FrameLayout.LayoutParams(it).apply { gravity = it.gravity }
        is ViewGroup.MarginLayoutParams -> FrameLayout.LayoutParams(it)
        else -> FrameLayout.LayoutParams(it)
    }
    this.layoutParams = n
    n
}

/** Sets the `weight` for `LinearLayout`s such as `row` and `column`. **/
@RxKotlinViewDsl var View.weight: Float
    get() = llparams.weight
    set(value) { llparams.weight = value }

/** - Sets the width and height to the given measurement in density pixels. **/
@RxKotlinViewDsl fun View.size(value: Int) { lparams.width = value.dp; lparams.height = value.dp }

/** Sets the width to match the width of its parent. **/
@RxKotlinViewDsl fun View.matchSize() { lparams.width = ViewGroup.LayoutParams.MATCH_PARENT; lparams.height = ViewGroup.LayoutParams.MATCH_PARENT }

/** Sets the width to wrap its content. **/
@RxKotlinViewDsl fun View.wrapSize() { lparams.width = ViewGroup.LayoutParams.WRAP_CONTENT; lparams.height = ViewGroup.LayoutParams.WRAP_CONTENT }

/** - Sets the width to the given measurement in density pixels. **/
@RxKotlinViewDsl fun View.width(value: Int) { lparams.width = value.dp }

/** Sets the width to match the width of its parent. **/
@RxKotlinViewDsl fun View.matchWidth() { lparams.width = ViewGroup.LayoutParams.MATCH_PARENT }

/** Sets the width to wrap its content. **/
@RxKotlinViewDsl fun View.wrapWidth() { lparams.width = ViewGroup.LayoutParams.WRAP_CONTENT }

/** Sets the height to the given measurement in density pixels. **/
@RxKotlinViewDsl fun View.height(value: Int) { lparams.height = value.dp }

/** Sets the height to match the width of its parent. **/
@RxKotlinViewDsl fun View.matchHeight() { lparams.height = ViewGroup.LayoutParams.MATCH_PARENT }

/** Sets the height to wrap its content. **/
@RxKotlinViewDsl fun View.wrapHeight() { lparams.height = ViewGroup.LayoutParams.WRAP_CONTENT }

/** Sets the alignment of the item inside the parent `LinearLayout`. **/
@RxKotlinViewDsl var View.layoutGravity: Int
    get() = when(val params = layoutParams) {
        is FrameLayout.LayoutParams -> params.gravity
        is LinearLayout.LayoutParams -> params.gravity
        else -> 0
    }
    set(@GravityInt gravity: Int) {
        when(val params = layoutParams) {
            is FrameLayout.LayoutParams -> params.gravity = gravity
            is LinearLayout.LayoutParams -> params.gravity = gravity
            else -> {
                layoutParams = if(params != null) FrameLayout.LayoutParams(params).apply { this.gravity = gravity } else FrameLayout.LayoutParams(
                    unsetSize, unsetSize, gravity)
            }
        }
    }

/** Sets the margin of all sides individually in density pixels. **/
@RxKotlinViewDsl fun View.margin(value: Int) { lparams.setMargins(value.dp) }

/** Sets the margin of each side individually in density pixels. **/
@RxKotlinViewDsl fun View.updateMargins(left: Int = Int.MAX_VALUE, top: Int = Int.MAX_VALUE, right: Int = Int.MAX_VALUE, bottom: Int = Int.MAX_VALUE) { lparams.setMargins(
    if(left == Int.MAX_VALUE) lparams.leftMargin else left.dp,
    if(top == Int.MAX_VALUE) lparams.topMargin else top.dp,
    if(right == Int.MAX_VALUE) lparams.rightMargin else right.dp,
    if(bottom == Int.MAX_VALUE) lparams.bottomMargin else bottom.dp,
) }

/** Sets the margin of all sides individually in density pixels. **/
@RxKotlinViewDsl fun View.pad(value: Int) { setPadding(value.dp) }

/** Sets the margin of each side individually in density pixels. **/
@RxKotlinViewDsl fun View.updatePad(left: Int = Int.MAX_VALUE, top: Int = Int.MAX_VALUE, right: Int = Int.MAX_VALUE, bottom: Int = Int.MAX_VALUE) { setPadding(
    if(left == Int.MAX_VALUE) this.paddingLeft else left.dp,
    if(top == Int.MAX_VALUE) this.paddingTop else top.dp,
    if(right == Int.MAX_VALUE) this.paddingRight else right.dp,
    if(bottom == Int.MAX_VALUE) this.paddingBottom else bottom.dp,
) }