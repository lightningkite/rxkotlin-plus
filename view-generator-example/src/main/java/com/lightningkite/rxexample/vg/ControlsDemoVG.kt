//! This file will translate using Khrysalis.
package com.lightningkite.rxexample.vg

import android.view.View
import android.widget.TextView
import com.lightningkite.rx.viewgenerators.ActivityAccess
import com.lightningkite.rx.ValueSubject
import com.lightningkite.rx.android.bind
import com.lightningkite.rx.android.bindString
import com.lightningkite.rx.viewgenerators.*
import com.lightningkite.rx.android.resources.*
import com.lightningkite.rxexample.databinding.ControlsDemoBinding
import com.lightningkite.rx.android.showIn
import com.lightningkite.rx.android.subscribeAutoDispose
import com.lightningkite.rx.toSubjectString
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.Subject

class ControlsDemoVG() : ViewGenerator {
    override val titleString: ViewString get() = ViewStringRaw("Controls Demo")

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
            .subscribeAutoDispose(xml.editableTextCopy, TextView::setText)
        options
            .showIn(xml.editableAutoText, this.text)
            .showIn(xml.spinner, text)

        return view
    }
}
