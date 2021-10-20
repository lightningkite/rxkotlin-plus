//! This file will translate using Khrysalis.
package com.lightningkite.rxexample.vg

import android.view.View
import android.widget.TextView
import com.lightningkite.rx.viewgenerators.ActivityAccess
import io.reactivex.rxjava3.subjects.Subject
import com.lightningkite.rx.ValueSubject
import com.lightningkite.rx.android.bindString


import com.lightningkite.rx.viewgenerators.*
import com.lightningkite.rx.android.resources.*
import com.lightningkite.rx.android.onClick
import com.lightningkite.rxexample.databinding.ExampleContentBinding
import com.lightningkite.rx.android.subscribeAutoDispose
import io.reactivex.rxjava3.core.Observable

class ExampleContentVG : ViewGenerator {
    override val titleString: ViewString get() = ViewStringRaw("Example Content")

    val number: ValueSubject<Int> = ValueSubject(0)
    val chained: ValueSubject<ValueSubject<Int>> = ValueSubject(ValueSubject(0))

    fun increment(){
        number.value += 1
    }

    override fun generate(dependency: ActivityAccess): View {
        val xml = ExampleContentBinding.inflate(dependency.layoutInflater)
        val view = xml.root
        xml.exampleContentIncrement.onClick{ this.increment() }
        number.map { it -> it.toString() }
            .subscribeAutoDispose(xml.exampleContentNumber, TextView::setText)
        xml.chainedIncrement.onClick { this.chained.value.value = this.chained.value.value + 1 }
        chained.flatMap { it -> it }.map { it -> it.toString() }
            .subscribeAutoDispose(xml.chainedNumber, TextView::setText)
        xml.scrollToTop.onClick { xml.scrollView.smoothScrollTo(0,0) }
        return view
    }
}
