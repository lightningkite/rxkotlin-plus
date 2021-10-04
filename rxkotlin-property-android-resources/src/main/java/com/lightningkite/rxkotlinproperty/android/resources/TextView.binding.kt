package com.lightningkite.rxkotlinproperty.android.resources

import android.widget.TextView
import com.lightningkite.rxkotlinproperty.Property
import com.lightningkite.rxkotlinproperty.android.removed
import com.lightningkite.rxkotlinproperty.subscribeBy
import com.lightningkite.rxkotlinproperty.until

/**
 *
 * Binds the text in the text view to the property provided
 *
 * Example
 * val text = StandardProperty("Test Text")
 * view.bindString(text)
 *
 */

fun TextView.bindViewString(property: Property<ViewString>) {
    property.subscribeBy { value ->
        this.text = value.get(context)
    }.until(this.removed)
}

