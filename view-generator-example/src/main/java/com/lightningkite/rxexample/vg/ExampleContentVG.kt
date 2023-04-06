//! This file will translate using Khrysalis.
package com.lightningkite.rxexample.vg


import android.view.View
import android.widget.TextView
import com.badoo.reaktive.observable.flatMap
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.subject.behavior.BehaviorSubject
import com.lightningkite.rx.android.into
import com.lightningkite.rx.viewgenerators.ActivityAccess
import com.lightningkite.rx.viewgenerators.ViewGenerator
import com.lightningkite.rx.viewgenerators.layoutInflater
import com.lightningkite.rxexample.databinding.ExampleContentBinding

class ExampleContentVG : ViewGenerator {
    val number: BehaviorSubject<Int> = BehaviorSubject(0)
    val chained: BehaviorSubject<BehaviorSubject<Int>> = BehaviorSubject(BehaviorSubject(0))

    fun increment() {
        number.onNext(number.value + 1)
    }

    override fun generate(dependency: ActivityAccess): View {
        val xml = ExampleContentBinding.inflate(dependency.layoutInflater)
        val view = xml.root
        xml.exampleContentIncrement.setOnClickListener { this.increment() }
        number.map { it -> it.toString() }
            .into(xml.exampleContentNumber, TextView::setText)
        xml.chainedIncrement.setOnClickListener { this.chained.value.onNext(this.chained.value.value + 1) }
        chained.flatMap { it -> it }.map { it -> it.toString() }
            .into(xml.chainedNumber, TextView::setText)
        xml.scrollToTop.setOnClickListener { xml.scrollView.smoothScrollTo(0, 0) }
        return view
    }
}
