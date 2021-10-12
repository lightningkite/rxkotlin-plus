package com.lightningkite.rxkotlinproperty.android.resources

import android.widget.TextView
import com.lightningkite.rxkotlinproperty.android.removed
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy

/**
 *
 * Binds the text in the text view to the property provided
 *
 * Example
 * val text = StandardProperty("Test Text")
 * view.bindString(text)
 *
 */

fun TextView.bindViewString(property: Observable<ViewString>) {
    property.subscribeBy { value ->
        this.text = value.get(context)
    }.addTo(this.removed)
}

