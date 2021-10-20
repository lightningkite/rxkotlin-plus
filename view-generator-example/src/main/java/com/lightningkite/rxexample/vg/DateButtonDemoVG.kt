//! This file will translate using Khrysalis.
package com.lightningkite.rxexample.vg

import android.view.View
import android.widget.TextView
import com.lightningkite.rx.viewgenerators.ActivityAccess
import io.reactivex.rxjava3.subjects.Subject
import com.lightningkite.rx.ValueSubject
import com.lightningkite.rx.android.bind
import com.lightningkite.rx.android.bindString
import com.lightningkite.rx.toSubjectLocalDate
import com.lightningkite.rx.toSubjectLocalTime

import java.time.format.FormatStyle
import java.time.*
import java.time.format.*
import com.lightningkite.rx.viewgenerators.*
import com.lightningkite.rx.android.resources.*
import com.lightningkite.rxexample.databinding.DateButtonDemoBinding
import com.lightningkite.rx.android.subscribeAutoDispose
import io.reactivex.rxjava3.core.Observable
import java.util.*
import java.time.*

class DateButtonDemoVG() : ViewGenerator {
    override val titleString: ViewString get() = ViewStringRaw("ZonedDateTime Button Demo")

    val date: ValueSubject<ZonedDateTime> = ValueSubject(ZonedDateTime.now())

    override fun generate(dependency: ActivityAccess): View {
        val xml = DateButtonDemoBinding.inflate(dependency.layoutInflater)
        val view = xml.root

        date.map { it -> it.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM)) }
            .subscribeAutoDispose<Observable<String>, TextView, String>(xml.text, TextView::setText)
        date.toSubjectLocalDate().bind(xml.dateButton)
        date.toSubjectLocalTime().bind(xml.timeButton)

        return view
    }
}
