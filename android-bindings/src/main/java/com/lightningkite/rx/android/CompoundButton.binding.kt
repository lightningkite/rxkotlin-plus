package com.lightningkite.rx.android

import android.widget.CompoundButton
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.Subject

/**
 * Binds the value of this to the checked state of the view. Changes will propagate both directions
 *
 * Example:
 * val selected = PublishSubject(false)
 * selected.bind(compoundButtonView)
 * button.bindSelect(selected)
 */
fun <SOURCE : Subject<Boolean>> SOURCE.bind(view: CompoundButton): SOURCE {
    var lastKnownValue: Boolean = false
    subscribeBy { it ->
        lastKnownValue = it
        if (it != view.isChecked) {
            view.isChecked = it
        }
    }.addTo(view.removed)
    view.setOnCheckedChangeListener { _, isChecked ->
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
 * button.bindSelect(selected)
 */
fun <SOURCE : Subject<Boolean>> SOURCE.bindNoUncheck(view: CompoundButton): SOURCE {
    var lastKnownValue: Boolean = false
    subscribeBy { it ->
        lastKnownValue = it
        if (it != view.isChecked) {
            view.isChecked = it
        }
    }.addTo(view.removed)
    view.setOnCheckedChangeListener { _, isChecked ->
        if (lastKnownValue != isChecked) {
            if (isChecked) {
                onNext(isChecked)
            } else {
                view.isChecked = true
            }
        }
    }
    return this
}

