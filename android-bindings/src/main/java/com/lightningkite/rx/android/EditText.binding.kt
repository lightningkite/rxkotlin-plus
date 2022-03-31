package com.lightningkite.rx.android

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.Subject

/**
 * Binds the value of this to the internal text of the EditText.
 * Changes will propagate both directions.
 *
 * Example:
 * val name = ValueSubject("Jason")
 * name.bind(editTextView)
 */
fun <SOURCE: Subject<String>> SOURCE.bind(editText: EditText): SOURCE {
    observeOn(RequireMainThread).subscribeBy { value ->
        if (value != editText.text.toString()) {
            editText.setText(value)
        }
    }.addTo(editText.removed)
    editText.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onNext(s?.toString() ?: "")
        }
    })
    return this
}
