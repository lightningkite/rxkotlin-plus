package com.lightningkite.rx.android.resources

import android.widget.TextView
import com.lightningkite.rx.android.removed
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy

/**
 * Sets the text value of the TextView to the ViewStrings get value.
 */
fun TextView.setText(viewString: ViewString) {
    text = viewString.get(context)
}