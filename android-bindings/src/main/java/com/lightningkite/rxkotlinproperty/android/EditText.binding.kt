package com.lightningkite.rxkotlinproperty.android

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.lightningkite.rxkotlinproperty.MutableProperty
import com.lightningkite.rxkotlinproperty.subscribeBy
import com.lightningkite.rxkotlinproperty.until

/**
 *
 * Binds the edit text internal text to the Property provided.
 * Any changes made by the user to the edit text will update the value of the property.
 * Any changes directly to the property will manifest in the edit text.
 *
 * Example
 * val name = StandardProperty("Jason")
 * nameField.bindString(name)
 * The name field will be populated with "Jason"
 *
 */
fun EditText.bindString(property: MutableProperty<String>) {
    property.subscribeBy { value ->
        if (property.value != text.toString()) {
            this.setText(property.value)
        }
    }.until(this.removed)
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (property.value != s) {
                property.value = (s.toString())
            }
        }
    })
}

/**
 *
 * Binds the edit text internal text to the Property provided.
 * This also limits the input type to a valid integer.
 * Any changes made by the user to the edit text will update the value of the property.
 * Any changes directly to the property will manifest in the edit text.
 *
 * Example
 * val value = StandardProperty(23)
 * nameField.bindString(value)
 * The name field will be populated with "23"
 *
 */
fun EditText.bindInteger(property: MutableProperty<Int>) {
    property.subscribeBy { value ->
        val currentValue = this.text.toString().toIntOrNull()
        if (value != currentValue) {
            this.setText(value.takeUnless { it == 0 }?.toString() ?: "")
        }
    }.until(this.removed)
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val currentValue = s.toString().toIntOrNull() ?: 0
            if (property.value != currentValue) {
                property.value = currentValue
            }
        }
    })
}



/**
 *
 * Binds the edit text internal text to the Property provided.
 * This also limits the input type to a valid double.
 * Any changes made by the user to the edit text will update the value of the property.
 * Any changes directly to the property will manifest in the edit text.
 *
 * Example
 * val value = StandardProperty(23.3)
 * nameField.bindString(value)
 * The name field will be populated with "23.3"
 *
 */
fun EditText.bindDouble(property: MutableProperty<Double>) {
    property.subscribeBy { value ->
        val currentValue = this.text.toString().toDoubleOrNull()
        if (value != currentValue) {
            this.setText(value.takeUnless { it == 0.0 }?.toString() ?: "")
        }
    }.until(this.removed)
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val currentValue = s.toString().toDoubleOrNull() ?: 0.0
            if (property.value != currentValue) {
                property.value = currentValue
            }
        }
    })
}


/**
 *
 * Binds the edit text internal text to the Property provided.
 * This also limits the input type to a valid integer.
 * Any changes made by the user to the edit text will update the value of the property.
 * Any changes directly to the property will manifest in the edit text.
 *
 * Blanks or invalid numbers are interpreted as null.
 *
 * Example
 * val value = StandardProperty(23)
 * nameField.bindString(value)
 * The name field will be populated with "23"
 *
 */
fun EditText.bindIntegerNullable(property: MutableProperty<Int?>) {
    property.subscribeBy { value ->
        val currentValue = this.text.toString().toIntOrNull()
        if (value != currentValue) {
            this.setText(value.takeUnless { it == 0 }?.toString() ?: "")
        }
    }.until(this.removed)
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val currentValue = s.toString().toIntOrNull()
            if (property.value != currentValue) {
                property.value = currentValue
            }
        }
    })
}



/**
 *
 * Binds the edit text internal text to the Property provided.
 * This also limits the input type to a valid double.
 * Any changes made by the user to the edit text will update the value of the property.
 * Any changes directly to the property will manifest in the edit text.
 *
 * Blanks or invalid numbers are interpreted as null.
 *
 * Example
 * val value = StandardProperty(23.3)
 * nameField.bindString(value)
 * The name field will be populated with "23.3"
 *
 */
fun EditText.bindDoubleNullable(property: MutableProperty<Double?>) {
    property.subscribeBy { value ->
        val currentValue = this.text.toString().toDoubleOrNull()
        if (value != currentValue) {
            this.setText(value.takeUnless { it == 0.0 }?.toString() ?: "")
        }
    }.until(this.removed)
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val currentValue = s.toString().toDoubleOrNull()
            if (property.value != currentValue) {
                property.value = currentValue
            }
        }
    })
}
