//! This file will translate using Khrysalis.
package com.lightningkite.rxexample.vg

import android.view.View
import android.widget.TextView
import com.jakewharton.rxbinding4.view.clicks
import com.lightningkite.rx.android.into
import com.lightningkite.rx.android.showIn
import com.lightningkite.rx.viewgenerators.*
import com.lightningkite.rxexample.databinding.ComponentTestBinding
import com.lightningkite.rxexample.databinding.SelectDemoBinding
import io.reactivex.rxjava3.core.Observable

class SelectDemoVG(val stack: StackSubject<ViewGenerator>) : ViewGenerator {

    val options: List<ViewGenerator> = listOf(
        ListDemoVG(stack),
        DslExampleVG(),
        VideoDemoVG(),
        WebsocketDemoVG(),
        HttpDemoVG(),
        ExternalTestVG(),
        DateButtonDemoVG(),
        LocationDemoVG(),
        LoginDemoVG(stack),
        LoadImageDemoVG(),
        ControlsDemoVG(),
        ExampleContentVG(),
        ViewPagerDemoVG(stack),
        SegmentedControlDemoVG(),
        SliderDemoVG(),
        PreviewVG()
    )

    fun selectVG(viewGenerator: ViewGenerator) {
        stack.push(viewGenerator)
    }

    override fun generate(dependency: ActivityAccess): View {
        val xml = SelectDemoBinding.inflate(dependency.layoutInflater)

        Observable.just(options).showIn(xml.list) { obs: Observable<ViewGenerator> ->
            val cellXml = ComponentTestBinding.inflate(dependency.layoutInflater)

            obs
                .map { it::class.java.simpleName ?: "" }
                .into(cellXml.label, TextView::setText)

            cellXml.button.clicks()
                .flatMapSingle { obs.firstOrError() }
                .into(cellXml.button) { selectVG(it) }

            cellXml.root
        }

        return xml.root
    }
}
