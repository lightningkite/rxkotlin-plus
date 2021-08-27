package com.lightningkite.rxkotlinandroid

import android.widget.ToggleButton
import com.lightningkite.rxkotlinproperty.until
import com.lightningkite.rxkotlinproperty.Property
import com.lightningkite.rxkotlinproperty.subscribeBy


/**
 *
 * Binds the textOn value in the toggle button to the observable provided
 *
 * Example
 * val text = StandardProperty("Test Text")
 * view.bindOnString(text)
 *
 */

fun ToggleButton.bindOnString(observable: Property<String>) {
    observable.subscribeBy { value ->
        this.textOn = value
        if(this.isChecked){
            this.text = value
        }
    }.until(this.removed)
}

/**
 *
 * Binds the textOff value in the toggle button to the observable provided
 *
 * Example
 * val text = StandardProperty("Test Text")
 * view.bindOffString(text)
 *
 */

fun ToggleButton.bindOffString(observable: Property<String>) {
    observable.subscribeBy { value ->
        this.textOff = value
        if(!this.isChecked){
            this.text = value
        }
    }.until(this.removed)
}

/**
 *
 * Binds both the textOff and textOn values in the toggle button to the observable provided
 *
 * Example
 * val text = StandardProperty("Test Text")
 * view.bindOnOffString(text)
 *
 */

fun ToggleButton.bindOnOffString(observable: Property<String>) {
    observable.subscribeBy { value ->
        this.textOff = value
        this.textOn = value
        this.text = value
    }.until(this.removed)
}