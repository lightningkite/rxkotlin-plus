package com.lightningkite.rxkotlinproperty.android

import android.view.View
import android.view.ViewGroup

fun View.onClick(disabledMilliseconds: Long = 500, action: () -> Unit) {
    var lastActivated = System.currentTimeMillis()
    setOnClickListener {
        if(System.currentTimeMillis() - lastActivated > disabledMilliseconds) {
            action()
            lastActivated = System.currentTimeMillis()
        }
    }
}

fun View.onLongClick(action: () -> Unit) {
    setOnLongClickListener { action(); true }
}

fun View.replace(other: View) {
    val parent = (this.parent as ViewGroup)
    other.layoutParams = this.layoutParams
    val index = parent.indexOfChild(this)
    parent.removeView(this)
    parent.addView(other, index)
}