//! This file will translate using Khrysalis.
package com.lightningkite.rxexample.vg

import android.view.View
import com.badoo.reaktive.subject.Subject
import com.badoo.reaktive.subject.behavior.BehaviorSubject
import com.lightningkite.rx.android.bind
import com.lightningkite.rx.android.into
import com.lightningkite.rx.map
import com.lightningkite.rx.plus
import com.lightningkite.rx.toSubjectFloat
import com.lightningkite.rx.viewgenerators.ActivityAccess
import com.lightningkite.rx.viewgenerators.ViewGenerator
import com.lightningkite.rx.viewgenerators.layoutInflater
import com.lightningkite.rxexample.databinding.SliderDemoBinding

class SliderDemoVG : ViewGenerator {

    val ratio: BehaviorSubject<Float> = BehaviorSubject(0f)
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
        percent.into(xml.valueDisplay) { xml.valueDisplay.text = it.toString() }
        xml.progress.run {
            xml.progress.max =
                10000; ratio.into(
            xml.progress
        ) { xml.progress.progress = (it * 10000).toInt() }
        }

        run { xml.rating.numStars = 5; xml.rating.stepSize = 1f; obsRatingInt.toSubjectFloat().bind(xml.rating) }
        run { xml.ratingDisplayStars.numStars = 5; obsRatingInt.toSubjectFloat().bind(xml.ratingDisplayStars) }
        run {
            xml.ratingDisplayStarsSmall.numStars = 5; obsRatingInt.toSubjectFloat().bind(xml.ratingDisplayStarsSmall)
        }
        obsRatingInt.into(xml.ratingDisplayNumber) { xml.ratingDisplayNumber.text = it.toString() }

        xml.ratingFloat.run {
            xml.ratingFloat.stepSize = 0.01f
            xml.ratingFloat.numStars =
                5; obsRatingFloat.into(
            xml.ratingFloat
        ) { xml.ratingFloat.rating = it }
        }
        xml.ratingDisplayStarsFloat.run {
            xml.ratingDisplayStarsFloat.numStars =
                5; obsRatingFloat.into(
            xml.ratingDisplayStarsFloat
        ) { xml.ratingDisplayStarsFloat.rating = it }
        }
        xml.ratingDisplayStarsSmallFloat.run {
            xml.ratingDisplayStarsSmallFloat.numStars =
                5; obsRatingFloat.into(
            xml.ratingDisplayStarsSmallFloat
        ) { xml.ratingDisplayStarsSmallFloat.rating = it }
        }
        obsRatingFloat.into(xml.ratingDisplayNumberFloat) {
            xml.ratingDisplayNumberFloat.text = it.toString()
        }

        return view
    }
}
