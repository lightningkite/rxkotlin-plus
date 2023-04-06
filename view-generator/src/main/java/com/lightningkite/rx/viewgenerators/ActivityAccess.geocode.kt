package com.lightningkite.rx.viewgenerators

import android.location.Address
import android.location.Geocoder
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.single
import com.badoo.reaktive.single.singleOf

/**
 * Returns a list of [Address]s matching the given [latitude] and [longitude].
 */
fun ActivityAccess.geocode(
    address: String,
    maxResults: Int = 1
): Single<List<Address>> {
    if (address.isEmpty()) {
        return singleOf(listOf())
    }
    return single{ emitter ->
        Thread {
            try {
                emitter.onSuccess(Geocoder(context).getFromLocationName(address, maxResults) ?: emptyList())
            } catch (e: Exception) {
                emitter.onError(e)
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
        return singleOf(listOf())
    }
    return single{ emitter ->
        Thread {
            try {
                emitter.onSuccess(Geocoder(context).getFromLocation(latitude, longitude, maxResults) ?: emptyList())

            } catch (e: Exception) {
                emitter.onError(e)
            }
        }.start()
    }
}
