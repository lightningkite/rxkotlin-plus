package com.lightningkite.rx.dsl

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView


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

@RxKotlinViewDsl fun ViewDsl.rowTop(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.TOP or Gravity.START, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.rowTopLeft(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.TOP or Gravity.LEFT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.rowTopStart(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.TOP or Gravity.START, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.rowTopCenter(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.TOP or Gravity.CENTER_HORIZONTAL, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.rowTopRight(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.TOP or Gravity.RIGHT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.rowTopEnd(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.TOP or Gravity.END, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)

@RxKotlinViewDsl fun ViewDsl.rowCenter(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.CENTER_VERTICAL or Gravity.START, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.rowCenterLeft(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.CENTER_VERTICAL or Gravity.LEFT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.rowCenterStart(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.CENTER_VERTICAL or Gravity.START, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.rowCenterBoth(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.rowCenterRight(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.CENTER_VERTICAL or Gravity.RIGHT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.rowCenterEnd(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.CENTER_VERTICAL or Gravity.END, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)

@RxKotlinViewDsl fun ViewDsl.rowBottom(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.START, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.rowBottomLeft(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.LEFT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.rowBottomStart(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.START, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.rowBottomCenter(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.rowBottomRight(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.RIGHT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.rowBottomEnd(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.END, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)

@RxKotlinViewDsl fun ViewDsl.rowFill(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.START, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, elements)
@RxKotlinViewDsl fun ViewDsl.rowFillLeft(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.LEFT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, elements)
@RxKotlinViewDsl fun ViewDsl.rowFillStart(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.START, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, elements)
@RxKotlinViewDsl fun ViewDsl.rowFillCenter(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, elements)
@RxKotlinViewDsl fun ViewDsl.rowFillRight(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.RIGHT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, elements)
@RxKotlinViewDsl fun ViewDsl.rowFillEnd(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.END, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, elements)

@RxKotlinViewDsl fun ViewDsl.columnLeft(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.TOP or Gravity.LEFT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.columnStart(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.TOP or Gravity.START, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.columnCenter(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.TOP or Gravity.CENTER_HORIZONTAL, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.columnRight(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.TOP or Gravity.RIGHT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.columnEnd(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.TOP or Gravity.END, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.columnFill(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.TOP or Gravity.END, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)

@RxKotlinViewDsl fun ViewDsl.columnTopLeft(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.TOP or Gravity.LEFT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.columnTopStart(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.TOP or Gravity.START, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.columnTopCenter(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.TOP or Gravity.CENTER_HORIZONTAL, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.columnTopRight(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.TOP or Gravity.RIGHT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.columnTopEnd(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.TOP or Gravity.END, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.columnTopFill(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.TOP or Gravity.END, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)

@RxKotlinViewDsl fun ViewDsl.columnCenterLeft(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.CENTER_VERTICAL or Gravity.LEFT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.columnCenterStart(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.CENTER_VERTICAL or Gravity.START, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.columnCenterBoth(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.columnCenterRight(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.CENTER_VERTICAL or Gravity.RIGHT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.columnCenterEnd(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.CENTER_VERTICAL or Gravity.END, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.columnCenterFill(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.CENTER_VERTICAL or Gravity.END, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)

@RxKotlinViewDsl fun ViewDsl.columnBottomLeft(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.BOTTOM or Gravity.LEFT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.columnBottomStart(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.BOTTOM or Gravity.START, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.columnBottomCenter(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.columnBottomRight(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.BOTTOM or Gravity.RIGHT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.columnBottomEnd(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.BOTTOM or Gravity.END, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.columnBottomFill(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.BOTTOM or Gravity.END, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)

@RxKotlinViewDsl fun ViewDsl.f(vararg elements: View) = FrameLayout(context).apply {
    for(child in elements) addView(child, child.flparams.apply {
        if(width == unsetSize) width = ViewGroup.LayoutParams.MATCH_PARENT
        if(height == unsetSize) height = ViewGroup.LayoutParams.MATCH_PARENT
    })
}
@RxKotlinViewDsl fun ViewDsl.scroll(view: View) = ScrollView(context).apply {
    addView(view, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
}