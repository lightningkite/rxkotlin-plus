package com.lightningkite.rx.android

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.Subject

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
fun <SOURCE: Subject<String>> SOURCE.bind(view: EditText): SOURCE {
    subscribeBy { value ->
        if (value != view.text.toString()) {
            view.setText(value)
        }
    }.addTo(view.removed)
    view.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onNext(s?.toString() ?: "")
        }
    })
    return this
}
