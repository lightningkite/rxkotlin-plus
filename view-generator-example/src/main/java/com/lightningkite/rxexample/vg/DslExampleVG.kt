
@file:OptIn(RxKotlinViewDsl::class)

package com.lightningkite.rxexample.vg

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.airbnb.paris.extensions.style
import com.badoo.reaktive.observable.flatMap
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.subject.behavior.BehaviorSubject
import com.badoo.reaktive.subject.publish.PublishSubject
import com.lightningkite.rx.android.into
import com.lightningkite.rx.dsl.*
import com.lightningkite.rx.viewgenerators.ActivityAccess
import com.lightningkite.rx.viewgenerators.ViewGenerator
import com.lightningkite.rxexample.R

class DslExampleVG : ViewGenerator {
    val number: BehaviorSubject<Int> = BehaviorSubject(0)
    val chained: BehaviorSubject<BehaviorSubject<Int>> = BehaviorSubject(BehaviorSubject(0))
    val scrollToTop = PublishSubject<Unit>()

    fun increment() {
        number.onNext(number.value + 1)
    }

    override fun generate(dependency: ActivityAccess): View = dependency.dsl {
        fun quickButton(text: String) = button { style(R.style.ButtonPrimary); this.text = text }
        scroll(
            columnTopStart(
                text {
                    style(R.style.Header)
                    setText(R.string.welcome)
                    setBackgroundResource(R.color.colorAccent)
                    transitionName = "title"
                    pad(4)
                },
                text {
                    style(R.style.Body)
                    setText(R.string.welcome_message)
                },
                rowCenter(
                    text {
                        style(R.style.Body)
                        number.map { it -> it.toString() }.into(this, TextView::setText)
                        weight = 1f
                    },
                    button {
                        style(R.style.ButtonPrimary)
                        setText(R.string.increment_the_number)
                        setOnClickListener { increment() }
                    },
                    setup = {
                        matchWidth()
                    }
                ),
                rowCenter(
                    text {
                        style(R.style.Body)
                        chained.flatMap { it -> it }.map { it -> it.toString() }.into(this, TextView::setText)
                        weight = 1f
                    },
                    button {
                        style(R.style.ButtonPrimary)
                        setText(R.string.increment_the_number)
                        setOnClickListener { chained.value.onNext(chained.value.value + 1) }
                    },
                    setup = {
                        matchWidth()
                    }
                ),
                image {
                    setImageResource(R.drawable.reason_expertise)
                    width(100)
                    height(50)
                },
                columnTopFill(
                    quickButton("First"),
                    quickButton("Second"),
                    quickButton("Third"),
                    setup = {
                        matchWidth()
                    },
                ),
                button {
                    style(R.style.ButtonPrimary)
                    text = "Scroll to Top"
                    setOnClickListener { scrollToTop.onNext(Unit) }
                }
            ),
            setup = {
                padDefault()
                scrollToTop.into(this) { smoothScrollTo(0, 0) }
            }
        )
    }
}

private class Preview(context: Context, attrs: AttributeSet? = null) : VgPreview(context, attrs, DslExampleVG())