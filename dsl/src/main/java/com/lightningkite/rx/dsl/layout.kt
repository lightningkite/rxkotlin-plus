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

@RxKotlinViewDsl fun ViewDsl.htl(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.TOP or Gravity.LEFT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.hts(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.TOP or Gravity.START, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.htc(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.TOP or Gravity.CENTER_HORIZONTAL, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.htr(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.TOP or Gravity.RIGHT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.hte(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.TOP or Gravity.END, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)

@RxKotlinViewDsl fun ViewDsl.hcl(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.CENTER_VERTICAL or Gravity.LEFT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.hcs(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.CENTER_VERTICAL or Gravity.START, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.hcc(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.hc(vararg elements: View) = hcc(*elements)
@RxKotlinViewDsl fun ViewDsl.hcr(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.CENTER_VERTICAL or Gravity.RIGHT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.hce(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.CENTER_VERTICAL or Gravity.END, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)

@RxKotlinViewDsl fun ViewDsl.hbl(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.LEFT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.hbs(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.START, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.hbc(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.hbr(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.RIGHT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.hbe(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.END, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)

@RxKotlinViewDsl fun ViewDsl.hfl(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.LEFT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, elements)
@RxKotlinViewDsl fun ViewDsl.hfs(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.START, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, elements)
@RxKotlinViewDsl fun ViewDsl.hfc(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, elements)
@RxKotlinViewDsl fun ViewDsl.hfr(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.RIGHT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, elements)
@RxKotlinViewDsl fun ViewDsl.hfe(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.END, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, elements)

@RxKotlinViewDsl fun ViewDsl.vtl(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.TOP or Gravity.LEFT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.vts(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.TOP or Gravity.START, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.vtc(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.TOP or Gravity.CENTER_HORIZONTAL, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.vtr(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.TOP or Gravity.RIGHT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.vte(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.TOP or Gravity.END, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.vtf(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.TOP or Gravity.END, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)

@RxKotlinViewDsl fun ViewDsl.vcl(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.CENTER_VERTICAL or Gravity.LEFT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.vcs(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.CENTER_VERTICAL or Gravity.START, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.vcc(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.vc(vararg elements: View) = vcc(*elements)
@RxKotlinViewDsl fun ViewDsl.vcr(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.CENTER_VERTICAL or Gravity.RIGHT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.vce(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.CENTER_VERTICAL or Gravity.END, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.vcf(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.CENTER_VERTICAL or Gravity.END, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)

@RxKotlinViewDsl fun ViewDsl.vbl(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.BOTTOM or Gravity.LEFT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.vbs(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.BOTTOM or Gravity.START, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.vbc(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.vbr(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.BOTTOM or Gravity.RIGHT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.vbe(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.BOTTOM or Gravity.END, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
@RxKotlinViewDsl fun ViewDsl.vbf(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.BOTTOM or Gravity.END, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)

@RxKotlinViewDsl fun ViewDsl.f(vararg elements: View) = FrameLayout(context).apply {
    for(child in elements) addView(child, child.flparams.apply {
        if(width == unsetSize) width = ViewGroup.LayoutParams.MATCH_PARENT
        if(height == unsetSize) height = ViewGroup.LayoutParams.MATCH_PARENT
    })
}
@RxKotlinViewDsl fun ViewDsl.scroll(view: View) = ScrollView(context).apply {
    addView(view, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
}