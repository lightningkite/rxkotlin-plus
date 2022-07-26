package com.lightningkite.rx.android.ble

import android.os.ParcelUuid
import com.lightningkite.rx.android.staticApplicationContext
import com.lightningkite.rx.viewgenerators.ActivityAccess
import com.polidea.rxandroidble3.RxBleClient
import com.polidea.rxandroidble3.RxBleDevice
import com.polidea.rxandroidble3.scan.ScanFilter
import com.polidea.rxandroidble3.scan.ScanSettings
import com.polidea.rxandroidble3.scan.ScanSettings.SCAN_MODE_LOW_LATENCY
import com.polidea.rxandroidble3.scan.ScanSettings.SCAN_MODE_LOW_POWER
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.UUID
import java.util.concurrent.TimeUnit

data class BleScanResult(
    val name: String?,
    val rssi: Int,
    val device: BleDevice
)
data class BleCharacteristic(val service: UUID, val id: UUID)

interface BleDevice {
    val mac: String
    fun stayConnected(): Observable<Unit> = TODO()
    fun rssi(): Single<Int> = TODO()
    fun read(characteristic: BleCharacteristic): Single<ByteArray> = TODO()
    fun write(characteristic: BleCharacteristic, value: ByteArray): Single<Unit> = TODO()
    fun notify(characteristic: BleCharacteristic): Observable<Observable<ByteArray>> = TODO()
}

fun ActivityAccess.bleScan(
    lowPower: Boolean = false,
    filterForService: UUID? = null,
): Observable<BleScanResult> = ble.scanBleDevices(
    ScanSettings.Builder().setScanMode(if(lowPower) SCAN_MODE_LOW_POWER else SCAN_MODE_LOW_LATENCY).build(),
    filterForService?.let { ScanFilter.Builder().setServiceUuid(ParcelUuid(it)).build() } ?: ScanFilter.empty()
).map {
    BleScanResult(
        name = it.bleDevice.name,
        rssi = it.rssi,
        device = AndroidBleDevice(it.bleDevice)
    )
}

fun ActivityAccess.bleDeviceFromMac(
    mac: String
): BleDevice = AndroidBleDevice(ble.getBleDevice(mac))


private val ble = RxBleClient.create(staticApplicationContext)
private class AndroidBleDevice(val device: RxBleDevice): BleDevice {
    override val mac: String
        get() = device.macAddress

    val connection = device.establishConnection(false)

    override fun stayConnected(): Observable<Unit> = connection.switchMap { Observable.never<Unit>() }.retryWhen { it.delay(1000L, TimeUnit.MILLISECONDS) }

    override fun rssi(): Single<Int> = connection.firstOrError().flatMap { it.readRssi() }.retryWhen { it.delay(1000L, TimeUnit.MILLISECONDS) }

    override fun read(characteristic: BleCharacteristic): Single<ByteArray> = connection.firstOrError().flatMap {
        it.readCharacteristic(characteristic.id)
    }

    override fun write(characteristic: BleCharacteristic, value: ByteArray): Single<Unit> = connection.firstOrError().flatMap {
        it.writeCharacteristic(characteristic.id, value)
    }.map { Unit }

    override fun notify(characteristic: BleCharacteristic): Observable<Observable<ByteArray>> = connection.flatMap {
        it.setupNotification(characteristic.id)
    }.retryWhen { it.delay(1000L, TimeUnit.MILLISECONDS) }
}

fun BleDevice.readNotify(characteristic: BleCharacteristic)
        = Observable.merge(
    read(characteristic).toObservable().retry(1).onErrorComplete(),
    notify(characteristic).switchMap{it}
)