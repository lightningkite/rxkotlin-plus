//! This file will translate using Khrysalis.
package com.lightningkite.rxexample.vg

import android.view.View
import com.lightningkite.rx.viewgenerators.ActivityAccess
import com.lightningkite.rx.ValueSubject
import com.lightningkite.rx.android.bind
import com.lightningkite.rx.viewgenerators.*
import com.lightningkite.rx.android.resources.*
import com.lightningkite.rxexample.databinding.SegmentedControlDemoBinding

class SegmentedControlDemoVG() : ViewGenerator {

    override fun generate(dependency: ActivityAccess): View {
        val xml = SegmentedControlDemoBinding.inflate(dependency.layoutInflater)
        val view = xml.root

        xml.tabs.bind(listOf("A", "B", "C"), ValueSubject(0))
        xml.tabs2.bind(listOf("A", "B", "C", "D", "E", "F", "G"), ValueSubject(0))

        return view
    }
}
