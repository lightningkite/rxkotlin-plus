package com.lightningkite.rx.viewgenerators

import android.view.View
import java.util.*


private val View_focusAtStartup = WeakHashMap<View, Boolean>()

/**
 * Whether the view should eligible to receive focus when it is attached to the screen.
 */
var View.focusAtStartup: Boolean
    get() = View_focusAtStartup[this] ?: true
    set(value) {
        View_focusAtStartup[this] = value
    }