//! This file will translate using Khrysalis.
package com.lightningkite.rxexample.vg

import android.view.View
import android.widget.TextView
import com.lightningkite.rx.ValueSubject
import com.lightningkite.rx.android.into
import com.lightningkite.rx.android.visible
import com.lightningkite.rx.viewgenerators.*
import com.lightningkite.rxexample.databinding.MainBinding
import io.reactivex.rxjava3.core.Observable

class MainVG : ViewGenerator, EntryPoint {
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
        stack.map { it -> it.lastOrNull()?.let { it::class.simpleName } ?: "" }
            .into(xml.title, TextView::setText)
        shouldBackBeShown.into(xml.mainBack, View::visible)
        xml.mainBack.setOnClickListener { this.stack.pop() }

        return view
    }

    override fun handleDeepLink(schema: String, host: String, path: String, params: Map<String, String>) {
        stack.push(ExampleContentVG())
    }

}
