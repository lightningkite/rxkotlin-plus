//! This file will translate using Khrysalis.
@file:OptIn(RxKotlinViewDsl::class)
package com.lightningkite.rxexample.vg

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.airbnb.paris.extensions.style
import com.lightningkite.rx.ValueSubject
import com.lightningkite.rx.android.into
import com.lightningkite.rx.dsl.*
import com.lightningkite.rx.viewgenerators.ActivityAccess
import com.lightningkite.rx.viewgenerators.ViewGenerator
import com.lightningkite.rxexample.R
import io.reactivex.rxjava3.subjects.PublishSubject

class DslExampleVG : ViewGenerator {
    val number: ValueSubject<Int> = ValueSubject(0)
    val chained: ValueSubject<ValueSubject<Int>> = ValueSubject(ValueSubject(0))
    val scrollToTop = PublishSubject.create<Unit>()

    fun increment() {
        number.value += 1
    }

    override fun generate(dependency: ActivityAccess): View = dependency.dsl {
        fun quickButton(text: String) = button { style(R.style.ButtonPrimary); this.text = text }
        scroll(
            vtl(
                text {
                    style(R.style.Header)
                    setText(R.string.welcome)
                    setBackgroundResource(R.color.colorAccent)
                    transitionName = "title"
                }.pad(4),
                text {
                    style(R.style.Body)
                    setText(R.string.welcome_message)
                },
                hc(
                    text {
                        style(R.style.Body)
                        number.map { it -> it.toString() }.into(this, TextView::setText)
                    }.weight(1f),
                    button {
                        style(R.style.ButtonPrimary)
                        setText(R.string.increment_the_number)
                        setOnClickListener { increment() }
                    }
                ).matchWidth(),
                hc(
                    text {
                        style(R.style.Body)
                        chained.flatMap { it -> it }.map { it -> it.toString() }.into(this, TextView::setText)
                    }.weight(1f),
                    button {
                        style(R.style.ButtonPrimary)
                        setText(R.string.increment_the_number)
                        setOnClickListener { chained.value.value = chained.value.value + 1 }
                    }
                ).matchWidth(),
                image { setImageResource(R.drawable.reason_expertise) }.width(100).height(50),
                vtf(
                    quickButton("First"),
                    quickButton("Second"),
                    quickButton("Third"),
                ).matchWidth(),
                button {
                    style(R.style.ButtonPrimary)
                    text = "Scroll to Top"
                    setOnClickListener { scrollToTop.onNext(Unit) }
                }
            ).applyDefaultPadding()
        ).apply { scrollToTop.into(this) { smoothScrollTo(0, 0) } }
    }
}

private class Preview(context: Context) : VgPreview(context, DslExampleVG())