package com.lightningkite.rx.android

import android.view.View
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.kotlin.addTo

var View.visible: Boolean
    get() = this.visibility == View.VISIBLE
    set(value) {
        if (value) this.visibility = View.VISIBLE else {
            if(this.visibility != View.GONE)
                this.visibility = View.INVISIBLE
        }
    }

var View.exists: Boolean
    get() = this.visibility == View.VISIBLE || this.visibility == View.INVISIBLE
    set(value) {
        if (value) {
            if(this.visibility != View.INVISIBLE)
                this.visibility = View.VISIBLE
        } else this.visibility = View.GONE
    }
