//! This file will translate using Khrysalis.
package com.lightningkite.rxexample.vg

import android.view.View
import android.widget.TextView
import com.lightningkite.rx.ValueSubject
import com.lightningkite.rx.android.into
import com.lightningkite.rx.android.showIn
import com.lightningkite.rx.viewgenerators.*
import com.lightningkite.rxexample.databinding.ComponentTestBinding
import com.lightningkite.rxexample.databinding.ViewPagerDemoBinding
import io.reactivex.rxjava3.core.Observable

class ViewPagerDemoVG(val stack: StackSubject<ViewGenerator>) : ViewGenerator {

    val items: List<String> = listOf(
        "First",
        "Second",
        "Third"
    )
    val selectedIndex: ValueSubject<Int> = ValueSubject(0)

    override fun generate(dependency: ActivityAccess): View {
        val xml = ViewPagerDemoBinding.inflate(dependency.layoutInflater)
        val view = xml.root

        Observable.just(items).showIn(xml.viewPager, selectedIndex) { it ->
            val xml = ComponentTestBinding.inflate(dependency.layoutInflater)
            val view = xml.root
            it.into(xml.label, TextView::setText)
            view
        }

        return view
    }
}
