//! This file will translate using Khrysalis.
package com.lightningkite.rxexample.vg

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.setPadding
import com.lightningkite.rx.viewgenerators.ActivityAccess
import io.reactivex.rxjava3.subjects.Subject
import com.lightningkite.rx.ValueSubject
import com.lightningkite.rx.android.bindString
import com.lightningkite.rx.viewgenerators.*
import com.lightningkite.rx.android.resources.*
import com.lightningkite.rx.android.onClick
import com.lightningkite.rxexample.databinding.ExampleContentBinding
import com.lightningkite.rx.android.into
import com.lightningkite.rx.dsl.*
import com.lightningkite.rxexample.R
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.PublishSubject

class ExampleContentVG : ViewGenerator {
    val number: ValueSubject<Int> = ValueSubject(0)
    val chained: ValueSubject<ValueSubject<Int>> = ValueSubject(ValueSubject(0))
    val scrollToTop = PublishSubject.create<Unit>()

    fun increment() {
        number.value += 1
    }

    override fun generate(dependency: ActivityAccess): View = dependency.dsl {
        scroll(
            vtl(
                text(R.style.Header).apply {
                    setText(R.string.welcome)
                    setBackgroundResource(R.color.colorAccent)
                    transitionName = "title"
                }.pad(4),
                text(R.style.Body).apply { setText(R.string.welcome_message) },
                hc(
                    text(R.style.Body).apply {
                        number.map { it -> it.toString() }.into(this, TextView::setText)
                    }.weight(1f),
                    button(R.style.ButtonPrimary).apply {
                        setText(R.string.increment_the_number)
                        onClick { increment() }
                    }
                ).matchWidth(),
                hc(
                    text(R.style.Body).apply {
                        chained.flatMap { it -> it }.map { it -> it.toString() }.into(this, TextView::setText)
                    }.weight(1f),
                    button(R.style.ButtonPrimary).apply {
                        setText(R.string.increment_the_number)
                        onClick { chained.value.value = chained.value.value + 1 }
                    }
                ).matchWidth(),
                text().apply { text = "Hello world" },
                image().apply { setImageResource(R.drawable.reason_expertise) }.width(100).height(50),
                button(R.style.ButtonPrimary).apply {
                    text = "Scroll to Top"
                    onClick { scrollToTop.onNext(Unit) }
                }
            )).apply { scrollToTop.into(this) { smoothScrollTo(0, 0) } }.applyDefaultPadding()
    }
}

private class Preview: DslPreview {
    constructor(context: Context):super(context) {}
    constructor(context: Context, attrs: AttributeSet?):super(context, attrs) {}
    override val vg get() = ExampleContentVG()
}