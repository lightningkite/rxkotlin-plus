package com.lightningkite.rx.android

import android.widget.CompoundButton
import com.badoo.reaktive.disposable.addTo
import com.badoo.reaktive.observable.observeOn
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.subject.Subject
import com.lightningkite.rx.withWrite

/**
 * Binds the value of this to the checked state of the view. Changes will propagate both directions
 *
 * Example:
 * val selected = PublishSubject(false)
 * selected.bind(compoundButtonView)
 */
fun <SOURCE : Subject<Boolean>> SOURCE.bind(compoundButton: CompoundButton): SOURCE =
    bindView(compoundButton, compoundButton.checkedChanges().withWrite { compoundButton.isChecked = it })

/**
 * Binds the value of this to the checked state of the view, however this will prevent the
 * button from unchecking itself. Changes will propagate both directions.
 *
 * Example:
 * val selected = ValueSubject(false)
 * selected.bindNoUncheck(compoundButtonView)
 */
fun <SOURCE : Subject<Boolean>> SOURCE.bindNoUncheck(compoundButton: CompoundButton): SOURCE {
    var lastKnownValue: Boolean = false
    observeOn(RequireMainThread).subscribe{ it ->
        lastKnownValue = it
        if (it != compoundButton.isChecked) {
            compoundButton.isChecked = it
        }
    }.addTo(compoundButton.removed)
    compoundButton.setOnCheckedChangeListener { _, isChecked ->
        if (lastKnownValue != isChecked) {
            if (isChecked) {
                onNext(isChecked)
            } else {
                compoundButton.isChecked = true
            }
        }
    }
    return this
}

