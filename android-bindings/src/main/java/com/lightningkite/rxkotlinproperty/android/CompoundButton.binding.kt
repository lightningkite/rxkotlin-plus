package com.lightningkite.rxkotlinproperty.android

import android.widget.CompoundButton
import com.lightningkite.rxkotlinproperty.MutableProperty
import com.lightningkite.rxkotlinproperty.subscribeBy
import com.lightningkite.rxkotlinproperty.until


/**
 *
 * Binds the checked state of the compound button to be checked if the property value matches the initial value provided.
 * If the button is tapped, it will change the property value to be the value provided.
 * If the property value is changed the button check state will update.
 *
 * Example
 * val selected = StandardProperty<Int>(1)
 * button.bindSelect(1, selected)
 * If selected has a value of 1 the button is checked, otherwise it is unchecked.
 *
 */
fun <T> CompoundButton.bindSelect(value: T, property: MutableProperty<T>) {
    property.subscribeBy { it ->
        val shouldBeChecked = it == value
        if (this.isChecked != shouldBeChecked) {
            this.isChecked = shouldBeChecked
        }
    }.until(this.removed)
    setOnCheckedChangeListener { buttonView, isChecked ->
        if (isChecked && property.value != value) {
            property.value = value
        } else if (!isChecked && property.value == value) {
            this.isChecked = true
        }
    }
}


/**
 *
 * Binds the checked state of the compound button to be checked if the property value matches the initial value provided.
 * If the button is tapped, it will change the property value to be the value provided.
 * If the property value is changed the button check state will update
 * This Nullable however allows for the property to be null. If null the button is UNCHECKED
 *
 * Example
 * val selected = StandardProperty<Int>(1)
 * button.bindSelect(1, selected)
 * If selected has a value of 1 the button is checked, otherwise it is unchecked.
 *
 */

fun <T> CompoundButton.bindSelectNullable(value: T, property: MutableProperty<T?>) {
    property.subscribeBy { it ->
        val shouldBeChecked = it == value
        if (this.isChecked != shouldBeChecked) {
            this.isChecked = shouldBeChecked
        }
    }.until(this.removed)
    setOnCheckedChangeListener { buttonView, isChecked ->
        if (isChecked && property.value != value) {
            property.value = value
        } else if (!isChecked && property.value == value) {
            property.value = null
        }
    }
}




/**
 *
 * Binds the checked state of the compound button to be checked if the property value matches the initial value provided or null.
 * The property is allowed to be null, and the button will be marked as checked if it is null.
 * If the button is tapped, it will change the property value to be the value provided.
 * If the property value is changed the button check state will update.
 *
 * Example
 * val selected = StandardProperty<Int>(1)
 * button.bindSelect(1, selected)
 * If selected has a value of 1 or null the button is checked, otherwise it is unchecked.
 *
 */
fun <T> CompoundButton.bindSelectInvert(value: T, property: MutableProperty<T?>) {
    var suppress = false
    property.subscribeBy { it ->
        if (!suppress) {
            suppress = true
            val shouldBeChecked = it == value || it == null
            if (this.isChecked != shouldBeChecked) {
                this.isChecked = shouldBeChecked
            }
            suppress = false
        }
    }.until(this.removed)
    setOnCheckedChangeListener { buttonView, isChecked ->
        if (!suppress) {
            suppress = true
            if (!isChecked && property.value == value) {
                property.value = null
                buttonView.isChecked = true
            } else if (property.value != value) {
                property.value = value
                buttonView.isChecked = true
            }
            suppress = false
        }
    }
}

/**
 *
 * Binds the checked state of the compound button to the property value.
 * If the button is tapped, it will change the property value.
 * If the property value is changed the button check state will update to match.
 *
 *
 * Example
 * val selected = StandardProperty<Boolean>(false)
 * button.bindSelect(selected)
 * If selected is true the button is checked, otherwise it is unchecked.
 *
 */
fun CompoundButton.bind(property: MutableProperty<Boolean>) {
    property.subscribeBy { it ->
        if (it != this.isChecked) {
            this.isChecked = it
        }
    }.until(this.removed)
    setOnCheckedChangeListener { buttonView, isChecked ->
        if (property.value != isChecked) {
            property.value = isChecked
        }
    }
}

