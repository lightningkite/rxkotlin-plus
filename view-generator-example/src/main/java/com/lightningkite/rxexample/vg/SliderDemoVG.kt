//! This file will translate using Khrysalis.
package com.lightningkite.rxexample.vg

import android.view.View
import android.widget.ProgressBar
import com.lightningkite.rx.viewgenerators.ActivityAccess
import io.reactivex.rxjava3.subjects.Subject
import com.lightningkite.rx.ValueSubject
import com.lightningkite.rx.map
import com.lightningkite.rx.android.bind
import com.lightningkite.rx.android.bindFloat

import com.lightningkite.rx.viewgenerators.*
import com.lightningkite.rx.android.resources.*
import com.lightningkite.rxexample.databinding.SliderDemoBinding
import com.lightningkite.rx.android.subscribeAutoDispose
import com.lightningkite.rx.plus
import com.lightningkite.rx.toSubjectFloat
import io.reactivex.rxjava3.core.Observable

class SliderDemoVG() : ViewGenerator {
    override val titleString: ViewString get() = ViewStringRaw("Slider Demo")

    val ratio: ValueSubject<Float> = ValueSubject(0f)
    val percent: Subject<Int> = ratio.map(
        read = { it -> (it * 100).toInt() },
        write = { it -> it.toFloat() / 100f }
    )
    val obsRatingInt: Subject<Int> = ratio.map(
        read = { it -> (it * 5).toInt() },
        write = { it -> it.toFloat() / 5f }
    )
    val obsRatingFloat: Subject<Float> = ratio.map(
        read = { it -> it * 5f },
        write = { it -> it / 5f }
    )

    override fun generate(dependency: ActivityAccess): View {
        val xml = SliderDemoBinding.inflate(dependency.layoutInflater)
        val view = xml.root

        xml.slider.run {
            xml.slider.max = 100 - 0; (percent + 0).bind(
            xml.slider
        )
        }
        percent.subscribeAutoDispose(xml.valueDisplay) { xml.valueDisplay.text = it.toString() }
        xml.progress.run {
            xml.progress.max =
                10000; ratio.subscribeAutoDispose<Observable<Float>, ProgressBar, Float>(
            xml.progress
        ) { xml.progress.progress = (it * 10000).toInt() }
        }

        run { xml.rating.numStars = 5; xml.rating.stepSize = 1f; obsRatingInt.toSubjectFloat().bind(xml.rating) }
        run { xml.ratingDisplayStars.numStars = 5; obsRatingInt.toSubjectFloat().bind(xml.ratingDisplayStars) }
        run {
            xml.ratingDisplayStarsSmall.numStars = 5; obsRatingInt.toSubjectFloat().bind(xml.ratingDisplayStarsSmall)
        }
        obsRatingInt.subscribeAutoDispose(xml.ratingDisplayNumber) { xml.ratingDisplayNumber.text = it.toString() }

        xml.ratingFloat.run {
            xml.ratingFloat.stepSize = 0.01f
            xml.ratingFloat.numStars =
                5; obsRatingFloat.subscribeAutoDispose(
            xml.ratingFloat
        ) { xml.ratingFloat.rating = it }
        }
        xml.ratingDisplayStarsFloat.run {
            xml.ratingDisplayStarsFloat.numStars =
                5; obsRatingFloat.subscribeAutoDispose(
            xml.ratingDisplayStarsFloat
        ) { xml.ratingDisplayStarsFloat.rating = it }
        }
        xml.ratingDisplayStarsSmallFloat.run {
            xml.ratingDisplayStarsSmallFloat.numStars =
                5; obsRatingFloat.subscribeAutoDispose(
            xml.ratingDisplayStarsSmallFloat
        ) { xml.ratingDisplayStarsSmallFloat.rating = it }
        }
        obsRatingFloat.subscribeAutoDispose(xml.ratingDisplayNumberFloat) {
            xml.ratingDisplayNumberFloat.text = it.toString()
        }

        return view
    }
}
