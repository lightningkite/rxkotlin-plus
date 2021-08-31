package com.lightningkite.rxkotlinproperty.android

import android.widget.ToggleButton
import com.lightningkite.rxkotlinproperty.Property
import com.lightningkite.rxkotlinproperty.subscribeBy
import com.lightningkite.rxkotlinproperty.until


/**
 *
 * Binds the textOn value in the toggle button to the property provided
 *
 * Example
 * val text = StandardProperty("Test Text")
 * view.bindOnString(text)
 *
 */

fun ToggleButton.bindOnString(property: Property<String>) {
    property.subscribeBy { value ->
        this.textOn = value
        if(this.isChecked){
            this.text = value
        }
    }.until(this.removed)
}

/**
 *
 * Binds the textOff value in the toggle button to the property provided
 *
 * Example
 * val text = StandardProperty("Test Text")
 * view.bindOffString(text)
 *
 */

fun ToggleButton.bindOffString(property: Property<String>) {
    property.subscribeBy { value ->
        this.textOff = value
        if(!this.isChecked){
            this.text = value
        }
    }.until(this.removed)
}

/**
 *
 * Binds both the textOff and textOn values in the toggle button to the property provided
 *
 * Example
 * val text = StandardProperty("Test Text")
 * view.bindOnOffString(text)
 *
 */

fun ToggleButton.bindOnOffString(property: Property<String>) {
    property.subscribeBy { value ->
        this.textOff = value
        this.textOn = value
        this.text = value
    }.until(this.removed)
}