//! This file will translate using Khrysalis.
package com.lightningkite.rxexample.vg

import android.view.View
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
import io.reactivex.rxjava3.subjects.PublishSubject

class ExampleContentVG : ViewGenerator {
    val number: ValueSubject<Int> = ValueSubject(0)
    val chained: ValueSubject<ValueSubject<Int>> = ValueSubject(ValueSubject(0))
    val scrollToTop = PublishSubject.create<Unit>()

    fun increment() {
        number.value += 1
    }

    override fun generate(dependency: ActivityAccess): View = dependency.dsl {
        scroll(vl(
            text(R.style.Header).text(R.string.welcome).pad(4).apply { setBackgroundResource(R.color.colorAccent); transitionName = "title" },
            text(R.style.Body).text(R.string.welcome_message),
            hc(
                text(R.style.Body).text(number.map { it -> it.toString() }).weight(1f),
                button(R.style.ButtonPrimary).text(R.string.increment_the_number).onClick { increment() }
            ).matchWidth(),
            hc(
                text(R.style.Body).text(chained.flatMap { it -> it }.map { it -> it.toString() }).weight(1f),
                button(R.style.ButtonPrimary).text(R.string.increment_the_number).onClick { chained.value.value = chained.value.value + 1 }
            ).matchWidth(),
            image().apply { setImageResource(R.drawable.reason_expertise) }.width(100).height(50),
            button(R.style.ButtonPrimary).apply { text = "Scroll to Top"; onClick { scrollToTop.onNext(Unit) } }
        )).apply { scrollToTop.into(this) { smoothScrollTo(0, 0) } }.applyDefaultPadding()
    }
}
