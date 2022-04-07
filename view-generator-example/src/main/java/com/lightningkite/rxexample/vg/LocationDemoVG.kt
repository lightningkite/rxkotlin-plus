//! This file will translate using Khrysalis.
package com.lightningkite.rxexample.vg

import android.location.Location
import android.view.View
import android.widget.TextView
import com.lightningkite.rx.ValueSubject
import com.lightningkite.rx.android.into
import com.lightningkite.rx.mapFromNullable
import com.lightningkite.rx.optional
import com.lightningkite.rx.viewgenerators.ActivityAccess
import com.lightningkite.rx.viewgenerators.ViewGenerator
import com.lightningkite.rx.viewgenerators.layoutInflater
import com.lightningkite.rx.viewgenerators.requestLocation
import com.lightningkite.rxexample.databinding.LocationDemoBinding
import java.util.*

class LocationDemoVG : ViewGenerator {

    val locationInfo = ValueSubject<Optional<Location>>(Optional.empty())

    override fun generate(dependency: ActivityAccess): View {
        val xml = LocationDemoBinding.inflate(dependency.layoutInflater)
        val view = xml.root
        xml.getLocation.setOnClickListener {
            dependency.requestLocation(
                accuracyBetterThanMeters = 100.0
            ).subscribe { location ->
                this.locationInfo.value = location.optional
            }
        }
        locationInfo.mapFromNullable { it ->
            if (it != null) {
                return@mapFromNullable "${it}"
            } else {
                return@mapFromNullable "Nothing yet"
            }
        }.into(xml.locationDisplay, TextView::setText)
        return view
    }
}
