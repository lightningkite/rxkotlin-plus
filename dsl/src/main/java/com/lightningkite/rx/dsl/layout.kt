package com.lightningkite.rx.dsl

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.airbnb.paris.extensions.style
import com.lightningkite.rx.viewgenerators.transition.SlideFixed


@RxKotlinViewDsl fun ViewDsl.ll(orientation: Int, defaultGravity: Int, defaultWidth: Int, defaultHeight: Int, elements: Array<out View>) = LinearLayout(context).apply {
    this.orientation = orientation
    this.gravity = defaultGravity
    for(child in elements) addView(child, child.llparams.apply {
        if(width == unsetSize) width = defaultWidth // ViewGroup.LayoutParams.WRAP_CONTENT
        if(height == unsetSize) height = defaultHeight // ViewGroup.LayoutParams.WRAP_CONTENT
        if(weight != 0f) {
            if(orientation == LinearLayout.HORIZONTAL) width = 0
            else height = 0
        }
    })
}

@RxKotlinViewDsl inline fun ViewDsl.rowTopLeft(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.HORIZONTAL, Gravity.TOP or Gravity.LEFT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements).apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.rowTopStart(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.HORIZONTAL, Gravity.TOP or Gravity.START, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements).apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.rowTopCenter(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.HORIZONTAL, Gravity.TOP or Gravity.CENTER_HORIZONTAL, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements).apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.rowTopRight(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.HORIZONTAL, Gravity.TOP or Gravity.RIGHT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements).apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.rowTopEnd(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.HORIZONTAL, Gravity.TOP or Gravity.END, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements).apply(setup)

@RxKotlinViewDsl inline fun ViewDsl.rowCenterLeft(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.HORIZONTAL, Gravity.CENTER_VERTICAL or Gravity.LEFT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements).apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.rowCenterStart(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.HORIZONTAL, Gravity.CENTER_VERTICAL or Gravity.START, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements).apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.rowCenter(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.HORIZONTAL, Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements).apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.rowCenterRight(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.HORIZONTAL, Gravity.CENTER_VERTICAL or Gravity.RIGHT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements).apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.rowCenterEnd(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.HORIZONTAL, Gravity.CENTER_VERTICAL or Gravity.END, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements).apply(setup)

@RxKotlinViewDsl inline fun ViewDsl.rowBottomLeft(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.LEFT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements).apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.rowBottomStart(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.START, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements).apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.rowBottomCenter(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements).apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.rowBottomRight(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.RIGHT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements).apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.rowBottomEnd(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.END, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements).apply(setup)

@RxKotlinViewDsl inline fun ViewDsl.rowFillLeft(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.HORIZONTAL, Gravity.CENTER_VERTICAL or Gravity.LEFT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, elements).apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.rowFillStart(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.HORIZONTAL, Gravity.CENTER_VERTICAL or Gravity.START, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, elements).apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.rowFillCenter(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.HORIZONTAL, Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, elements).apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.rowFillRight(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.HORIZONTAL, Gravity.CENTER_VERTICAL or Gravity.RIGHT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, elements).apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.rowFillEnd(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.HORIZONTAL, Gravity.CENTER_VERTICAL or Gravity.END, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, elements).apply(setup)

@RxKotlinViewDsl inline fun ViewDsl.columnTopLeft(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.VERTICAL, Gravity.TOP or Gravity.LEFT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements).apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.columnTopStart(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.VERTICAL, Gravity.TOP or Gravity.START, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements).apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.columnTopCenter(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.VERTICAL, Gravity.TOP or Gravity.CENTER_HORIZONTAL, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements).apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.columnTopRight(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.VERTICAL, Gravity.TOP or Gravity.RIGHT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements).apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.columnTopEnd(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.VERTICAL, Gravity.TOP or Gravity.END, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements).apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.columnTopFill(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.VERTICAL, Gravity.TOP or Gravity.CENTER_HORIZONTAL, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements).apply(setup)

@RxKotlinViewDsl inline fun ViewDsl.columnCenterLeft(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.VERTICAL, Gravity.CENTER_VERTICAL or Gravity.LEFT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements).apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.columnCenterStart(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.VERTICAL, Gravity.CENTER_VERTICAL or Gravity.START, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements).apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.columnCenter(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.VERTICAL, Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements).apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.columnCenterRight(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.VERTICAL, Gravity.CENTER_VERTICAL or Gravity.RIGHT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements).apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.columnCenterEnd(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.VERTICAL, Gravity.CENTER_VERTICAL or Gravity.END, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements).apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.columnCenterFill(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.VERTICAL, Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements).apply(setup)

@RxKotlinViewDsl inline fun ViewDsl.columnBottomLeft(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.VERTICAL, Gravity.BOTTOM or Gravity.LEFT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements).apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.columnBottomStart(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.VERTICAL, Gravity.BOTTOM or Gravity.START, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements).apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.columnBottomCenter(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.VERTICAL, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements).apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.columnBottomRight(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.VERTICAL, Gravity.BOTTOM or Gravity.RIGHT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements).apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.columnBottomEnd(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.VERTICAL, Gravity.BOTTOM or Gravity.END, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements).apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.columnBottomFill(vararg elements: View, setup: LinearLayout.()->Unit = {}) = ll(LinearLayout.VERTICAL, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements).apply(setup)

@RxKotlinViewDsl inline fun ViewDsl.frame(vararg elements: View, setup: FrameLayout.()->Unit = {}) = FrameLayout(context).apply {
    for(child in elements) addView(child, child.flparams.apply {
        if(width == unsetSize) width = ViewGroup.LayoutParams.MATCH_PARENT
        if(height == unsetSize) height = ViewGroup.LayoutParams.MATCH_PARENT
    })
}.apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.scroll(view: View, setup: ScrollView.()->Unit = {}) = ScrollView(context).apply {
    addView(view, view.flparams.apply {
        if(width == unsetSize) width = ViewGroup.LayoutParams.MATCH_PARENT
        if(height == unsetSize) height = ViewGroup.LayoutParams.WRAP_CONTENT
    })
}.apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.horizontalScroll(view: View, setup: HorizontalScrollView.()->Unit = {}) = HorizontalScrollView(context).apply {
    addView(view, view.flparams.apply {
        if(width == unsetSize) width = ViewGroup.LayoutParams.WRAP_CONTENT
        if(height == unsetSize) height = ViewGroup.LayoutParams.MATCH_PARENT
    })
}.apply(setup)

@RxKotlinViewDsl inline fun ViewDsl.flipper(vararg elements: View, setup: ViewFlipper.() -> Unit = {}) = ViewFlipper(context).apply {
    for(child in elements) addView(child, child.lparams.apply {
        if(width == unsetSize) width = ViewGroup.LayoutParams.MATCH_PARENT
        if(height == unsetSize) height = ViewGroup.LayoutParams.MATCH_PARENT
    })
}.apply(setup)
