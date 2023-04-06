package com.lightningkite.rx.viewgenerators

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.*
import androidx.core.content.ContextCompat
import androidx.core.location.LocationListenerCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.location.LocationRequestCompat
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.asObservable
import com.badoo.reaktive.maybe.maybe
import com.badoo.reaktive.observable.*
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.flatMapMaybe


/**
 * Gets a single location update from the users.
 * Handles permissions by itself.
 */
@SuppressLint("MissingPermission")
fun ActivityAccess.requestLocation(
    accuracyBetterThanMeters: Double = 10.0
): Single<Location> = requestLocationStream(accuracyBetterThanMeters).firstOrError()

@SuppressLint("MissingPermission")
fun ActivityAccess.requestLocationStream(
    accuracyBetterThanMeters: Double = 10.0
): Observable<Location> = concat(
    internalGetCurrentLocation().asObservable(),
    internalGetLocationUpdates(LocationRequestCompat.Builder(5L * 60L * 1000L).build())
)
    .filter { it.accuracy < accuracyBetterThanMeters }

@SuppressLint("MissingPermission")
internal fun ActivityAccess.internalGetCurrentLocation(
): Maybe<Location> = requestPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    .flatMapMaybe {
        maybe{ em ->
            val manager = (context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager) ?: run {
                em.onError(Exception("No location service found"))
                return@maybe
            }
            LocationManagerCompat.getCurrentLocation(
                manager,
                if (LocationManagerCompat.hasProvider(manager, "fused")) "fused" else LocationManager.GPS_PROVIDER,
                null,
                ContextCompat.getMainExecutor(context),
            ) {

                if (it != null)
                    em.onSuccess(it)
                else
                    em.onComplete()
            }
        }
    }

@SuppressLint("MissingPermission")
internal fun ActivityAccess.internalGetLocationUpdates(
    request: LocationRequestCompat
): Observable<Location> = requestPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    .toObservable()
    .flatMap {
        observable{ em ->
            val manager = (context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager) ?: run {
                em.onError(Exception("No location service found"))
                return@observable
            }
            val listener = LocationListenerCompat { location -> em.onNext(location) }
            LocationManagerCompat.requestLocationUpdates(
                manager,
                if (LocationManagerCompat.hasProvider(manager, "fused")) "fused" else LocationManager.GPS_PROVIDER,
                request,
                ContextCompat.getMainExecutor(context),
                listener
            )
            em.setDisposable(Disposable {
                LocationManagerCompat.removeUpdates(manager, listener)
            })
        }
    }