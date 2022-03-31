package com.lightningkite.rx.android

import android.widget.CompoundButton
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.Subject

/**
 * Binds the value of this to the checked state of the view. Changes will propagate both directions
 *
 * Example:
 * val selected = PublishSubject(false)
 * selected.bind(compoundButtonView)
 */
fun <SOURCE : Subject<Boolean>> SOURCE.bind(compoundButton: CompoundButton): SOURCE {
    var lastKnownValue: Boolean = false
    observeOn(RequireMainThread).subscribeBy { it ->
        lastKnownValue = it
        if (it != compoundButton.isChecked) {
            compoundButton.isChecked = it
        }
    }.addTo(compoundButton.removed)
    compoundButton.setOnCheckedChangeListener { _, isChecked ->
        if (lastKnownValue != isChecked) {
            onNext(isChecked)
        }
    }
    return this
}

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
    observeOn(RequireMainThread).subscribeBy { it ->
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

