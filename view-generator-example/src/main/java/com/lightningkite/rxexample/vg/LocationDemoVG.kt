//! This file will translate using Khrysalis.
package com.lightningkite.rxexample.vg

import android.location.Location
import android.view.View
import android.widget.TextView
import com.lightningkite.rx.viewgenerators.ActivityAccess
import io.reactivex.rxjava3.subjects.Subject
import com.lightningkite.rx.ValueSubject
import com.lightningkite.rx.android.bindString

import com.lightningkite.rx.viewgenerators.*
import com.lightningkite.rx.android.resources.*
import com.lightningkite.rx.android.onClick
import com.lightningkite.rxexample.databinding.LocationDemoBinding
import com.lightningkite.rx.android.subscribeAutoDispose
import com.lightningkite.rx.mapFromNullable
import com.lightningkite.rx.optional
import io.reactivex.rxjava3.core.Observable
import java.util.*

class LocationDemoVG : ViewGenerator {
    override val titleString: ViewString get() = ViewStringRaw("Location Demo")

    val locationInfo = ValueSubject<Optional<Location>>(Optional.empty())

    override fun generate(dependency: ActivityAccess): View {
        val xml = LocationDemoBinding.inflate(dependency.layoutInflater)
        val view = xml.root
        xml.getLocation.onClick {
            dependency.requestLocation(
                accuracyBetterThanMeters = 100.0
            ).subscribe { location ->
                this.locationInfo.value = location.optional
            }
        }
        locationInfo.mapFromNullable { it ->
            if(it != null){
                return@mapFromNullable "${it}"
            } else {
                return@mapFromNullable "Nothing yet"
            }
        }.subscribeAutoDispose(xml.locationDisplay, TextView::setText)
        return view
    }
}
