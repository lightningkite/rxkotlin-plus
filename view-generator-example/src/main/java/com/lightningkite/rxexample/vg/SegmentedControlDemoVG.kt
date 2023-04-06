//! This file will translate using Khrysalis.
package com.lightningkite.rxexample.vg

import android.view.View
import com.badoo.reaktive.subject.behavior.BehaviorSubject
import com.lightningkite.rx.android.bind
import com.lightningkite.rx.viewgenerators.ActivityAccess
import com.lightningkite.rx.viewgenerators.ViewGenerator
import com.lightningkite.rx.viewgenerators.layoutInflater
import com.lightningkite.rxexample.databinding.SegmentedControlDemoBinding

class SegmentedControlDemoVG : ViewGenerator {

    override fun generate(dependency: ActivityAccess): View {
        val xml = SegmentedControlDemoBinding.inflate(dependency.layoutInflater)
        val view = xml.root

        xml.tabs.bind(listOf("A", "B", "C"), BehaviorSubject(0))
        xml.tabs2.bind(listOf("A", "B", "C", "D", "E", "F", "G"), BehaviorSubject(0))

        return view
    }
}
