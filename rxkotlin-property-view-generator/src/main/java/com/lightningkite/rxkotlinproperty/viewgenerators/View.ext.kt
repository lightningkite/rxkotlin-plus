package com.lightningkite.rxkotlinproperty.viewgenerators

import android.view.View
import java.util.*


private val View_focusAtStartup = WeakHashMap<View, Boolean>()
var View.focusAtStartup: Boolean
    get() = View_focusAtStartup[this] ?: true
    set(value) {
        View_focusAtStartup[this] = value
    }