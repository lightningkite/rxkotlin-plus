package com.lightningkite.rx.android.resources

import android.widget.TextView

/**
 * Sets the text value of the TextView to the ViewStrings get value.
 */
fun TextView.setText(viewString: ViewString) {
    text = viewString.get(context)
}