package com.lightningkite.rxkotlinproperty.android

import android.graphics.drawable.Drawable
import android.widget.Button
import com.lightningkite.rxkotlinproperty.Property
import com.lightningkite.rxkotlinproperty.subscribeBy
import com.lightningkite.rxkotlinproperty.until


/**
 *
 * Bind Active when provided an observable<Boolean> will turn the button on and off, or allow it to be tapped,
 * according to the value in the observable. As well you can provide a drawable for each state.
 * The background drawable will change as the state does. By Default drawable doesn't change.
 *
 *  * Example
 * val active = StandardProperty<Boolean>(true)
 * button.bindSelect(active, R.drawable.blue_border, R.drawable.grey_border)
 * If active is true the button can be clicked, otherwise it is not enabled, and cannot be clicked.
 * when active the button background will be the blue border, otherwise the button will be the grey border.
 */
fun Button.bindActive(
    observable: Property<Boolean>,
    activeBackground: Drawable,
    inactiveBackground: Drawable
) {
    observable.subscribeBy { it ->
        this.isEnabled = it
        if (it) {
            this.background = activeBackground
        } else {
            this.background = inactiveBackground
        }
    }.until(this.removed)
}
