package com.lightningkite.rx.viewgenerators

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.*
import androidx.core.content.ContextCompat
import androidx.core.location.LocationListenerCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.location.LocationRequestCompat
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable

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
): Observable<Location> = Observable.concat(
    internalGetCurrentLocation().toObservable(),
    internalGetLocationUpdates(LocationRequestCompat.Builder(5L * 60L * 1000L)
        .build())
).filter { it.accuracy < accuracyBetterThanMeters }

@SuppressLint("MissingPermission")
internal fun ActivityAccess.internalGetCurrentLocation(
): Maybe<Location> = requestPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    .flatMapMaybe {
        Maybe.create<Location> { em ->
            val manager = (context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager) ?: run {
                em.onError(Exception("No location service found"))
                return@create
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
        Observable.create<Location> { em ->
            val manager = (context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager) ?: run {
                em.onError(Exception("No location service found"))
                return@create
            }
            val listener = LocationListenerCompat { location -> em.onNext(location) }
            LocationManagerCompat.requestLocationUpdates(
                manager,
                if (LocationManagerCompat.hasProvider(manager, "fused")) "fused" else LocationManager.GPS_PROVIDER,
                request,
                ContextCompat.getMainExecutor(context),
                listener
            )
            em.setDisposable(Disposable.fromAction {
                LocationManagerCompat.removeUpdates(manager, listener)
            })
        }
    }