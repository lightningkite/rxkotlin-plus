//! This file will translate using Khrysalis.
package com.lightningkite.rxexample.vg

import android.view.View
import android.widget.TextView
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.subject.behavior.BehaviorSubject
import com.lightningkite.rx.android.into
import com.lightningkite.rx.android.showIn
import com.lightningkite.rx.viewgenerators.ActivityAccess
import com.lightningkite.rx.viewgenerators.StackSubject
import com.lightningkite.rx.viewgenerators.ViewGenerator
import com.lightningkite.rx.viewgenerators.layoutInflater
import com.lightningkite.rxexample.databinding.ComponentTextBinding
import com.lightningkite.rxexample.databinding.ListDemoBinding
import kotlin.random.Random

class ListDemoVG(val stack: StackSubject<ViewGenerator>) : ViewGenerator {

    val data = BehaviorSubject(listOf<Int>(20, 40, 60, 80, 100))

    override fun generate(dependency: ActivityAccess): View {
        val xml = ListDemoBinding.inflate(dependency.layoutInflater)
        val view = xml.root

        data.showIn(xml.list, getId = { it }) { obs ->
            val cell = ComponentTextBinding.inflate(dependency.layoutInflater)
            obs.map { it.toString() }.into(cell.label, TextView::setText)
            cell.root
        }

        xml.addItem.setOnClickListener {
            data.onNext((data.value + Random.nextInt(1, 100)).sorted())
        }

        return view
    }
}
