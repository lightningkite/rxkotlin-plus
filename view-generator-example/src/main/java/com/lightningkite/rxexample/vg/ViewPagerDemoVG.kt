//! This file will translate using Khrysalis.
package com.lightningkite.rxexample.vg

import android.view.View
import com.lightningkite.rx.viewgenerators.ActivityAccess
import com.lightningkite.rx.viewgenerators.StackSubject
import com.lightningkite.rx.ValueSubject
import com.lightningkite.rx.android.bind
import com.lightningkite.rx.viewgenerators.*
import com.lightningkite.rx.android.resources.*
import com.lightningkite.rxexample.databinding.ComponentTestBinding
import com.lightningkite.rxexample.databinding.ViewPagerDemoBinding
import com.lightningkite.rx.android.showIn

class ViewPagerDemoVG(val stack: StackSubject<ViewGenerator>) : ViewGenerator {
    override val titleString: ViewString get() = ViewStringRaw("View Pager Demo")

    val items: List<String> = listOf(
        "First",
        "Second",
        "Third"
    )
    val selectedIndex: ValueSubject<Int> = ValueSubject(0)

    override fun generate(dependency: ActivityAccess): View {
        val xml = ViewPagerDemoBinding.inflate(dependency.layoutInflater)
        val view = xml.root

        items.showIn(xml.viewPager, selectedIndex) { it ->
            val xml = ComponentTestBinding.inflate(dependency.layoutInflater)
            val view = xml.root
            xml.label.text = it
            view
        }

        return view
    }
}
