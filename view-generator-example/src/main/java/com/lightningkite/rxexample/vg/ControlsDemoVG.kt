//! This file will translate using Khrysalis.
package com.lightningkite.rxexample.vg

import android.view.View
import android.widget.TextView
import com.lightningkite.rx.ValueSubject
import com.lightningkite.rx.android.bind
import com.lightningkite.rx.android.into
import com.lightningkite.rx.android.showIn
import com.lightningkite.rx.toSubjectString
import com.lightningkite.rx.viewgenerators.ActivityAccess
import com.lightningkite.rx.viewgenerators.ViewGenerator
import com.lightningkite.rx.viewgenerators.layoutInflater
import com.lightningkite.rxexample.databinding.ControlsDemoBinding

class ControlsDemoVG : ViewGenerator {
    val text: ValueSubject<String> = ValueSubject("")
    val options: ValueSubject<List<String>> =
        ValueSubject(listOf("Apple", "Banana", "Chili Pepper", "Dragon Fruit"))
    val number: ValueSubject<Int> = ValueSubject(32)

    override fun generate(dependency: ActivityAccess): View {
        val xml = ControlsDemoBinding.inflate(dependency.layoutInflater)
        val view = xml.root

        number.toSubjectString().bind(xml.numberText)
        text
            .bind(xml.editableText)
            .bind(xml.editableAutoText)
            .bind(xml.editableTextBig)
            .into(xml.editableTextCopy, TextView::setText)
        options
            .showIn(xml.editableAutoText, this.text)
            .showIn(xml.spinner, text)

        return view
    }
}
