package com.lightningkite.rxkotlinproperty.android

import android.graphics.drawable.Drawable
import android.widget.Button
import com.lightningkite.rxkotlinproperty.Property
import com.lightningkite.rxkotlinproperty.subscribeBy
import com.lightningkite.rxkotlinproperty.until



/**
 *
 * Bind isEnabled with the provided observable<Boolean>. This will turn the button on and off, or allow it to be tapped,
 * according to the value in the observable. As well you can provide a color resource for each state.
 * The colors will set the background color as it changes. By default colors don't change.
 *
 * Example
 * val active = StandardObservableProperty<Boolean>(true)
 * button.bindSelect(active, R.color.blue, R.color.grey)
 * If active is true the button can be clicked, otherwise it is not enabled, and cannot be clicked.
 * when active the button will be the blue color, otherwise the button will be grey.
 */
fun Button.bindActive(
    observable: Property<Boolean>,
    activeColorResource: ColorResource? = null,
    inactiveColorResource: ColorResource? = null
) {
    observable.subscribeBy { it ->
        this.isEnabled = it
        if (it) {
            activeColorResource?.let { color ->
                this.setBackgroundResource(color)
            }
        } else {
            inactiveColorResource?.let { color ->
                this.setBackgroundResource(color)
            }
        }
    }.until(this.removed)
}

/**
 *
 * Bind Active when provided an property<Boolean> will turn the button on and off, or allow it to be tapped,
 * according to the value in the property. As well you can provide a drawable for each state.
 * The background drawable will change as the state does. By Default drawable doesn't change.
 *
 *  * Example
 * val active = StandardProperty<Boolean>(true)
 * button.bindSelect(active, R.drawable.blue_border, R.drawable.grey_border)
 * If active is true the button can be clicked, otherwise it is not enabled, and cannot be clicked.
 * when active the button background will be the blue border, otherwise the button will be the grey border.
 */
fun Button.bindActive(
    property: Property<Boolean>,
    activeBackground: Drawable,
    inactiveBackground: Drawable
) {
    property.subscribeBy { it ->
        this.isEnabled = it
        if (it) {
            this.background = activeBackground
        } else {
            this.background = inactiveBackground
        }
    }.until(this.removed)
}
