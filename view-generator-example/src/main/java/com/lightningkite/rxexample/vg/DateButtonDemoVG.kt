//! This file will translate using Khrysalis.
package com.lightningkite.rxexample.vg

import android.view.View
import android.widget.TextView
import com.lightningkite.rx.ValueSubject
import com.lightningkite.rx.android.bind
import com.lightningkite.rx.android.into
import com.lightningkite.rx.toSubjectLocalDate
import com.lightningkite.rx.toSubjectLocalTime
import com.lightningkite.rx.viewgenerators.ActivityAccess
import com.lightningkite.rx.viewgenerators.ViewGenerator
import com.lightningkite.rx.viewgenerators.layoutInflater
import com.lightningkite.rxexample.databinding.DateButtonDemoBinding
import io.reactivex.rxjava3.core.Observable
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class DateButtonDemoVG : ViewGenerator {
    val date: ValueSubject<ZonedDateTime> = ValueSubject(ZonedDateTime.now())

    override fun generate(dependency: ActivityAccess): View {
        val xml = DateButtonDemoBinding.inflate(dependency.layoutInflater)
        val view = xml.root

        date.map { it -> it.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM)) }
            .into<Observable<String>, TextView, String>(xml.text, TextView::setText)
        date.toSubjectLocalDate().bind(xml.dateButton)
        date.toSubjectLocalTime().bind(xml.timeButton)

        return view
    }
}
