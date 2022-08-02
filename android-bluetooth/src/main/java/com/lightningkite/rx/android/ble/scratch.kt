package com.lightningkite.rx.android.ble

import android.os.Build
import android.os.ParcelUuid
import com.lightningkite.rx.android.staticApplicationContext
import com.lightningkite.rx.viewgenerators.ActivityAccess
import com.polidea.rxandroidble3.RxBleClient
import com.polidea.rxandroidble3.RxBleConnection
import com.polidea.rxandroidble3.RxBleDevice
import com.polidea.rxandroidble3.scan.ScanFilter
import com.polidea.rxandroidble3.scan.ScanSettings
import com.polidea.rxandroidble3.scan.ScanSettings.SCAN_MODE_LOW_LATENCY
import com.polidea.rxandroidble3.scan.ScanSettings.SCAN_MODE_LOW_POWER
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.UUID
import java.util.concurrent.TimeUnit

data class BleScanResult(
    val name: String?,
    val rssi: Int,
    val id: String
)

data class BleCharacteristic(val serviceId: UUID, val id: UUID)

interface BleDevice {
    val id: String
    fun stayConnected(): Observable<Unit>
    fun isConnected(): Observable<Boolean>
    fun rssi(): Single<Int>
    fun read(characteristic: BleCharacteristic): Single<ByteArray>
    fun write(characteristic: BleCharacteristic, value: ByteArray): Single<Unit>
    fun notify(characteristic: BleCharacteristic): Observable<ByteArray>
}

private val ActivityAccess.requireBle: Single<Unit>
    get() {
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

fun ActivityAccess.bleScan(
    lowPower: Boolean = false,
    filterForService: UUID? = null,
): Observable<BleScanResult> = requireBle.flatMapObservable {
    ble.scanBleDevices(
        ScanSettings.Builder().setScanMode(if (lowPower) SCAN_MODE_LOW_POWER else SCAN_MODE_LOW_LATENCY).build(),
        filterForService?.let { ScanFilter.Builder().setServiceUuid(ParcelUuid(it)).build() } ?: ScanFilter.empty()
    ).map {
        BleScanResult(
            name = it.bleDevice.name,
            rssi = it.rssi,
            id = it.bleDevice.macAddress
        )
    }
}

fun ActivityAccess.bleDevice(
    id: String,
    requiresBond: Boolean,
): BleDevice = AndroidBleDevice(this, ble.getBleDevice(id), requiresBond)


private val ble = RxBleClient.create(staticApplicationContext)

private class AndroidBleDevice(val activityAccess: ActivityAccess, val device: RxBleDevice, val requiresBond: Boolean) :
    BleDevice {
    override val id: String
        get() = device.macAddress

    val connection = activityAccess.requireBle.flatMapObservable {
        if (requiresBond)
            device.establishConnection(false)
        else
            device.establishConnection(false)
    }.retryWhen { it.delay(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread()) }

    override fun isConnected(): Observable<Boolean> = device.observeConnectionStateChanges().map {
        when (it) {
            RxBleConnection.RxBleConnectionState.CONNECTED -> true
            else -> false
        }
    }

    override fun stayConnected(): Observable<Unit> =
        connection.switchMap { Observable.never<Unit>() }.retryWhen { it.delay(1000L, TimeUnit.MILLISECONDS) }

    override fun rssi(): Single<Int> =
        connection.firstOrError().flatMap { it.readRssi() }.retryWhen { it.delay(1000L, TimeUnit.MILLISECONDS) }

    override fun read(characteristic: BleCharacteristic): Single<ByteArray> = connection.firstOrError().flatMap {
        it.readCharacteristic(characteristic.id)
    }

    override fun write(characteristic: BleCharacteristic, value: ByteArray): Single<Unit> =
        connection.firstOrError().flatMap {
            it.writeCharacteristic(characteristic.id, value)
        }.map { Unit }

    override fun notify(characteristic: BleCharacteristic): Observable<ByteArray> = connection.flatMap {
        it.setupNotification(characteristic.id).switchMap { it }
    }.retryWhen { it.delay(1000L, TimeUnit.MILLISECONDS) }
}

fun BleDevice.readNotify(characteristic: BleCharacteristic) = Observable.merge(
    read(characteristic).toObservable().retry(1).onErrorComplete(),
    notify(characteristic)
)