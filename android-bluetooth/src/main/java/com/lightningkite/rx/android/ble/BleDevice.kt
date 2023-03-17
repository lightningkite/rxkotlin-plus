package com.lightningkite.rx.android.ble

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface BleDevice {
    val id: String
    fun stayConnected(): Observable<Unit>
    fun isConnected(): Observable<Boolean>
    fun rssi(): Single<Int>
    fun read(characteristic: BleCharacteristic): Single<ByteArray>
    fun write(characteristic: BleCharacteristic, value: ByteArray): Single<Unit>
    fun notify(characteristic: BleCharacteristic): Observable<ByteArray>
}