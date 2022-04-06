//! This file will translate using Khrysalis.
package com.lightningkite.rxexample.vg

import android.view.View
import android.widget.TextView
import com.jakewharton.rxbinding4.view.clicks
import com.lightningkite.rx.viewgenerators.ActivityAccess
import io.reactivex.rxjava3.core.Observable
import com.lightningkite.rx.viewgenerators.StackSubject
import com.lightningkite.rx.ValueSubject
import com.lightningkite.rx.android.*
import com.lightningkite.rx.viewgenerators.EntryPoint
import com.lightningkite.rx.viewgenerators.*
import com.lightningkite.rx.android.resources.*
import com.lightningkite.rx.android.onClick
import com.lightningkite.rxexample.databinding.ComponentTextBinding
import com.lightningkite.rxexample.databinding.ListDemoBinding
import kotlin.random.Random

class ListDemoVG(val stack: StackSubject<ViewGenerator>) : ViewGenerator {

    val data = ValueSubject(listOf<Int>(20, 40, 60, 80, 100))

    override fun generate(dependency: ActivityAccess): View {
        val xml = ListDemoBinding.inflate(dependency.layoutInflater)
        val view = xml.root

        data.showIn(xml.list, getId = { it }) { obs ->
            val cell = ComponentTextBinding.inflate(dependency.layoutInflater)
            obs.map { it.toString() }.into(cell.label, TextView::setText)
            cell.root
        }

        xml.addItem.onClick {
            data.value = (data.value + Random.nextInt(1, 100)).sorted()
        }

        return view
    }
}
