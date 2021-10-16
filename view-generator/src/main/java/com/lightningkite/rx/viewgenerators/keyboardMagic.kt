package com.lightningkite.rx.viewgenerators

import android.content.Context
import android.view.FocusFinder
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

private operator fun View.contains(other: View?): Boolean {
    if (other == null) return false
    if (this == other) return true
    else return this.contains(other.parent as? View)
}

private fun usesKeyboard(view: View) = view is EditText

fun runKeyboardUpdate(access: ActivityAccess, root: View? = null, discardingRoot: View? = null) {
    val currentFocus = access.activity?.currentFocus
    var dismissOld = false
    if (currentFocus != null) {
        if (!currentFocus.isAttachedToWindow || discardingRoot?.contains(currentFocus) == true || !usesKeyboard(currentFocus)) {
            //dismiss keyboard if the view's gone
            dismissOld = true
        }
    }
    val keyboardView: View? = (root as? ViewGroup)?.let { it ->
        var current: View? = null
        var first: View? = null
        while(true){
            current = FocusFinder.getInstance().findNextFocus(it, current, View.FOCUS_FORWARD) ?: return@let null
            if(current == first) {
                return@let null
            }
            if(first == null){
                first = current
            }
            if(current.focusAtStartup){
                return@let current
            }
        }
        return@let null
    }
    if (keyboardView != null) {
        if (usesKeyboard(keyboardView)) {
            keyboardView.requestFocus()
            val imm = access.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(keyboardView, 0)
            dismissOld = false
        }
    }
    if (dismissOld) {
        val imm = access.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.windowToken?.let {
            imm.hideSoftInputFromWindow(it, 0)
        }
    }
}