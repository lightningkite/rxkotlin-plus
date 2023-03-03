package com.lightningkite.rx.android.ble

import android.os.Build
import com.lightningkite.rx.android.ble.FixRxUndeliverable
import com.lightningkite.rx.viewgenerators.ActivityAccess
import io.reactivex.rxjava3.core.Single

internal val ActivityAccess.requireBle: Single<Unit>
    get() {
        FixRxUndeliverable
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Single.just(Unit)
                .flatMap {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        requestPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
                            .doOnSuccess { if (!it) throw Exception() }
                            .map { Unit }
                    } else {
                        Single.just(Unit)
                    }
                }
                .flatMap {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        requestPermission(android.Manifest.permission.BLUETOOTH_SCAN)
                            .doOnSuccess { if (!it) throw Exception() }
                            .map { Unit }
                    } else {
                        Single.just(Unit)
                    }
                }
        } else {
            Single.just(Unit)
                .flatMap {
                    requestPermission(android.Manifest.permission.BLUETOOTH)
                        .doOnSuccess { if (!it) throw Exception() }
                        .map { Unit }
                }
                .flatMap {
                    requestPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                        .doOnSuccess { if (!it) throw Exception() }
                        .map { Unit }
                }
                .flatMap {
                    requestPermission(android.Manifest.permission.BLUETOOTH_ADMIN)
                        .doOnSuccess { if (!it) throw Exception() }
                        .map { Unit }
                }
        }
    }