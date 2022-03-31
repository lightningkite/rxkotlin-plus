package com.lightningkite.rx.viewgenerators

import android.location.Address
import android.location.Geocoder
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single

/**
 * Returns a list of [Address]s matching the given [latitude] and [longitude].
 */
fun ActivityAccess.geocode(
    address: String,
    maxResults: Int = 1
): Single<List<Address>> {
    if (address.isEmpty()) {
        return Single.just(listOf())
    }
    return Single.create<List<Address>>{ emitter ->
        Thread {
            try {
                emitter.onSuccess(Geocoder(context)
                    .getFromLocationName(address, maxResults))
            } catch (e: Exception) {
                emitter.tryOnError(e)
            }
        }.start()
    }
}

/**
 * Returns a list of [Address]s matching the given [latitude] and [longitude].
 */
fun ActivityAccess.geocode(
    latitude: Double,
    longitude: Double,
    maxResults: Int = 1
): Single<List<Address>> {
    if (latitude == 0.0 && longitude == 0.0) {
        return Single.just(listOf())
    }
    return Single.create<List<Address>>{ emitter ->
        Thread {
            try {
                emitter.onSuccess(Geocoder(context)
                    .getFromLocation(latitude, longitude, maxResults))

            } catch (e: Exception) {
                emitter.tryOnError(e)
            }
        }.start()
    }
}
