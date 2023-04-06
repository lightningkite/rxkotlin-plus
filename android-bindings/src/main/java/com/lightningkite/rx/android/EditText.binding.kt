package com.lightningkite.rx.android

import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.badoo.reaktive.subject.Subject
import com.lightningkite.rx.withWrite

/**
 * Binds the value of this to the internal text of the EditText.
 * Changes will propagate both directions.
 *
 * Example:
 * val name = ValueSubject("Jason")
 * name.bind(editTextView)
 */
fun <SOURCE: Subject<String>> SOURCE.bind(editText: EditText): SOURCE =
    bindView(editText, editText.textChanges().map { it.toString() }.withWrite { editText.setText(it) })

