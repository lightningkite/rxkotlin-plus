package com.lightningkite.rx.viewgenerators

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.lightningkite.rx.viewgenerators.ActivityAccess
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

@SuppressLint("MissingPermission")
fun ActivityAccess.requestLocation(
    accuracyBetterThanMeters: Double = 10.0
): Single<Location> = Single.create<Location> { em ->
    requestPermission(Manifest.permission.ACCESS_FINE_LOCATION) {
        if(it) {
            val manager = (context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager)
            val criteria = Criteria()
            criteria.horizontalAccuracy = when (accuracyBetterThanMeters) {
                in 0f..100f -> Criteria.ACCURACY_HIGH
                in 100f..500f -> Criteria.ACCURACY_MEDIUM
                else -> Criteria.ACCURACY_LOW
            }
            manager?.requestSingleUpdate(
                Criteria(),
                object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        em.onSuccess(location)
                    }

                    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

                    }
                },
                Looper.getMainLooper()
            )
        } else {
            em.tryOnError(SecurityException("User has not granted access to fine location."))
        }
    }
}.subscribeOn(AndroidSchedulers.mainThread())
