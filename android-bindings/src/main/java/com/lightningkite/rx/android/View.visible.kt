package com.lightningkite.rx.android

import android.view.View

/**
 * A convenience function for hiding and showing views.
 */
var View.visible: Boolean
    get() = this.visibility == View.VISIBLE
    set(value) {
        if (value) this.visibility = View.VISIBLE else {
            if(this.visibility != View.GONE)
                this.visibility = View.INVISIBLE
        }
    }

/**
 * A convenience function for hiding and showing views.
 */
var View.exists: Boolean
    get() = this.visibility == View.VISIBLE || this.visibility == View.INVISIBLE
    set(value) {
        if (value) {
            if(this.visibility != View.INVISIBLE)
                this.visibility = View.VISIBLE
        } else this.visibility = View.GONE
    }
