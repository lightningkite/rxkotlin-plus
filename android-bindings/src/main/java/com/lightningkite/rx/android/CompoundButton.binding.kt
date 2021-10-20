package com.lightningkite.rx.android

import android.widget.CompoundButton
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.Subject

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

