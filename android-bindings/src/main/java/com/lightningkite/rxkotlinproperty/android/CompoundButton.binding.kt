package com.lightningkite.rxkotlinproperty.android

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
fun CompoundButton.bind(property: Subject<Boolean>) {
    property.subscribeBy { it ->
        if (it != this.isChecked) {
            this.isChecked = it
        }
    }.addTo(this.removed)
    setOnCheckedChangeListener { buttonView, isChecked ->
        property.onNext(isChecked)
    }
}

