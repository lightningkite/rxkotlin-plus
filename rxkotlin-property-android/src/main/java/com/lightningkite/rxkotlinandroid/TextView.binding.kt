package com.lightningkite.rxkotlinandroid

import android.view.View
import android.widget.TextView
import com.lightningkite.rxkotlinproperty.until
import com.lightningkite.rxkotlinproperty.Property
import com.lightningkite.rxkotlinproperty.subscribeBy

/**
 *
 * Binds the text in the text view to the observable provided
 *
 * Example
 * val text = StandardProperty("Test Text")
 * view.bindString(text)
 *
 */

fun TextView.bindString(observable: Property<String>) {
    observable.subscribeBy { value ->
        this.text = value
    }.until(this.removed)
}

/**
 *
 * Binds the text in the text view to the string resource provided in the observable
 *
 * Example
 * val text = StandardProperty(R.string.test_text)
 * view.bindString(text)
 *
 */
fun TextView.bindStringRes(observable: Property<StringResource?>) {
    observable.subscribeBy { value ->
        this.visibility = if (value == null) View.GONE else View.VISIBLE
        if (value != null) {
            this.text = this.resources.getString(value)
        }
    }.until(this.removed)
}


/**
 *
 * Binds the text in the text view to the string returned by the transform function
 * The transform function is the lambda that return a string when provided the value from the observable
 *
 * Example
 * val item = StandardProperty(Item())
 * view.bindString(item){ item -> return "test"}
 *
 */
fun <T> TextView.bindText(observable: Property<T>, transform: (T) -> String) {
    observable.subscribeBy { value ->
        this.text = transform(value)
    }.until(this.removed)
}
