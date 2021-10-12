package com.lightningkite.rxkotlinproperty.android

import android.widget.ToggleButton
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.kotlin.addTo


/**
 *
 * Binds the textOn value in the toggle button to the property provided
 *
 * Example
 * val text = StandardProperty("Test Text")
 * view.bindOnString(text)
 *
 */

fun ToggleButton.bindOnString(property: Observable<String>) {
    property.subscribeBy { value ->
        this.textOn = value
        if(this.isChecked){
            this.text = value
        }
    }.addTo(this.removed)
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

fun ToggleButton.bindOffString(property: Observable<String>) {
    property.subscribeBy { value ->
        this.textOff = value
        if(!this.isChecked){
            this.text = value
        }
    }.addTo(this.removed)
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

fun ToggleButton.bindOnOffString(property: Observable<String>) {
    property.subscribeBy { value ->
        this.textOff = value
        this.textOn = value
        this.text = value
    }.addTo(this.removed)
}