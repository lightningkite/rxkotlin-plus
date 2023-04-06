//! This file will translate using Khrysalis.
package com.lightningkite.rxexample.vg

import android.location.Location
import android.view.View
import android.widget.TextView
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.single.subscribe
import com.badoo.reaktive.subject.behavior.BehaviorSubject
import com.lightningkite.rx.android.into
import com.lightningkite.rx.viewgenerators.ActivityAccess
import com.lightningkite.rx.viewgenerators.ViewGenerator
import com.lightningkite.rx.viewgenerators.layoutInflater
import com.lightningkite.rx.viewgenerators.requestLocation
import com.lightningkite.rxexample.databinding.LocationDemoBinding
import java.util.*

class LocationDemoVG : ViewGenerator {

    val locationInfo = BehaviorSubject<Location?>(null)

    override fun generate(dependency: ActivityAccess): View {
        val xml = LocationDemoBinding.inflate(dependency.layoutInflater)
        val view = xml.root
        xml.getLocation.setOnClickListener {
            dependency.requestLocation(
                accuracyBetterThanMeters = 100.0
            ).subscribe{ location ->
                this.locationInfo.onNext(location)
            }
        }
        locationInfo.map { it ->
            if (it != null) {
                return@map "${it}"
            } else {
                return@map "Nothing yet"
            }
        }.into(xml.locationDisplay, TextView::setText)
        return view
    }
}
