//! This file will translate using Khrysalis.
package com.lightningkite.rxexample.vg

import android.view.View
import com.lightningkite.rx.viewgenerators.ActivityAccess
import com.lightningkite.rx.ValueSubject
import com.lightningkite.rx.android.bind
import io.reactivex.rxjava3.kotlin.subscribeBy
import com.lightningkite.rx.android.removed
import io.reactivex.rxjava3.kotlin.addTo
import com.lightningkite.rx.viewgenerators.*
import com.lightningkite.rx.android.resources.*
import com.lightningkite.rxexample.databinding.*
import com.lightningkite.rx.android.showIn
import io.reactivex.rxjava3.core.Observable

class PreviewVG : ViewGenerator {
    override val titleString: ViewString
        get() = ViewStringRaw("Preview")

    class XmlPreview(val name: String, val make: (ActivityAccess) -> View)

    val previews: List<XmlPreview> = listOf(
        XmlPreview("ControlsDemoBinding") { it -> ControlsDemoBinding.inflate(it.layoutInflater).root },
        XmlPreview("ExampleContentBinding") { it -> ExampleContentBinding.inflate(it.layoutInflater).root },
        XmlPreview("LoginDemoBinding") { it -> LoginDemoBinding.inflate(it.layoutInflater).root },
        XmlPreview("MainBinding") { it -> MainBinding.inflate(it.layoutInflater).root },
        XmlPreview("SegmentedControlDemoBinding") { it -> SegmentedControlDemoBinding.inflate(it.layoutInflater).root },
        XmlPreview("SelectDemoBinding") { it -> SelectDemoBinding.inflate(it.layoutInflater).root },
        XmlPreview("SliderDemoBinding") { it -> SliderDemoBinding.inflate(it.layoutInflater).root },
        XmlPreview("ViewPagerDemoBinding") { it -> ViewPagerDemoBinding.inflate(it.layoutInflater).root }
    )

    val previewIndex: ValueSubject<Int> = ValueSubject(0)

    override fun generate(dependency: ActivityAccess): View {
        val xml = PreviewBinding.inflate(dependency.layoutInflater)
        val view = xml.root
        previews.showIn(xml.pager, previewIndex) { it -> it.make(dependency) }
        previewIndex.subscribeBy { it ->
            xml.viewName.text = this.previews[it].name
        }.addTo(view.removed)
        return view
    }

}
