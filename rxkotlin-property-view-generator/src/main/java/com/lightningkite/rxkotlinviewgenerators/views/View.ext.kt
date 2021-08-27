package com.lightningkite.rxkotlinviewgenerators.views

import android.view.View
import android.graphics.drawable.Drawable
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import java.util.*


private val View_focusAtStartup = WeakHashMap<View, Boolean>()
var View.focusAtStartup: Boolean
    get() = View_focusAtStartup[this] ?: true
    set(value) {
        View_focusAtStartup[this] = value
    }