//! This file will translate using Khrysalis.
package com.lightningkite.rxexample.vg

import android.view.View
import android.widget.TextView
import com.lightningkite.rx.viewgenerators.ActivityAccess
import io.reactivex.rxjava3.core.Observable
import com.lightningkite.rx.viewgenerators.StackSubject
import com.lightningkite.rx.ValueSubject
import com.lightningkite.rx.android.bindString
import com.lightningkite.rx.android.visible

import com.lightningkite.rx.viewgenerators.EntryPoint
import com.lightningkite.rx.viewgenerators.*
import com.lightningkite.rx.android.resources.*
import com.lightningkite.rx.android.onClick
import com.lightningkite.rxexample.databinding.MainBinding
import com.lightningkite.rx.android.subscribeAutoDispose

class MainVG : ViewGenerator, EntryPoint {
    override val titleString: ViewString get() = ViewStringRaw("Main")

    val stack: StackSubject<ViewGenerator> = ValueSubject(listOf<ViewGenerator>())
    override val mainStack: StackSubject<ViewGenerator>?
        get() = stack
    val shouldBackBeShown: Observable<Boolean> = stack.map { it -> it.size > 1 }

    init {
        stack.push(SelectDemoVG(stack))
    }

    override fun generate(dependency: ActivityAccess): View {
        val xml = MainBinding.inflate(dependency.layoutInflater)
        val view = xml.root

        stack.showIn(xml.mainContent, dependency)
        stack.map { it -> it.lastOrNull()?.titleString?.get(dependency.context) ?: "" }
            .subscribeAutoDispose<Observable<String>, TextView, String>(xml.title, TextView::setText)
        shouldBackBeShown.subscribeAutoDispose(xml.mainBack, View::visible)
        xml.mainBack.onClick { this.stack.pop() }

        return view
    }

    override fun handleDeepLink(schema: String, host: String, path: String, params: Map<String, String>) {
        stack.push(ExampleContentVG())
    }

}
