package com.lightningkite.rx.android.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.Build
import android.os.ParcelUuid
import com.lightningkite.rx.android.staticApplicationContext
import com.lightningkite.rx.filterIsPresent
import com.lightningkite.rx.kotlin
import com.lightningkite.rx.mapNotNull
import com.lightningkite.rx.viewgenerators.ActivityAccess
import com.polidea.rxandroidble3.RxBleClient
import com.polidea.rxandroidble3.RxBleConnection
import com.polidea.rxandroidble3.RxBleDevice
import com.polidea.rxandroidble3.scan.ScanFilter
import com.polidea.rxandroidble3.scan.ScanSettings
import com.polidea.rxandroidble3.scan.ScanSettings.SCAN_MODE_LOW_LATENCY
import com.polidea.rxandroidble3.scan.ScanSettings.SCAN_MODE_LOW_POWER
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.*
import java.util.concurrent.TimeUnit

data class BleScanResult(
    val name: String?,
    val rssi: Int,
    val id: String,
    val services: Map<UUID, ByteArray>
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
            id = it.bleDevice.macAddress,
            services = (it.scanRecord?.serviceData?.mapKeys { it.key.uuid }  ?: mapOf()) + (it.scanRecord?.serviceUuids?.associate { it.uuid to byteArrayOf() } ?: mapOf())
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


    //get bluetooth permission
    private val rawConnection = activityAccess.requireBle.flatMapObservable {
        println("Attempting connection to ${device.name ?: device.macAddress}")
        //Check and see if bond is required
        if (requiresBond)
            device.establishConnection(false).doOnNext { println("Established connection to ${device.name ?: device.macAddress}") }.switchMapSingle { device.waitForBond().toSingleDefault(it) }
        else
            device.establishConnection(false).doOnNext { println("Established connection to ${device.name ?: device.macAddress}") }
    }.map { Optional.of(it) } //Maps to a optional because a connection may or may not be present
    val connection: Observable<RxBleConnection> = rawConnection.onErrorResumeNext {
        println("Connection failed to ${device.name ?: device.macAddress} with $it")
        it.printStackTrace()
        /*Connects the pipelines to allow a connection to fire if present and do nothing
        if no connection is present*/
        Observable.concat<Optional<RxBleConnection>>(
            Observable.just(Optional.empty()),
            Observable.empty<Optional<RxBleConnection>>().delay(1000L, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()),
            rawConnection
        )
    }.replay(1).refCount().filterIsPresent()

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


@SuppressLint("MissingPermission")
private fun RxBleDevice.waitForBond(): Completable {
    this.bluetoothDevice.createBond()
    return Observable.interval(0, 300, TimeUnit.MILLISECONDS)
        .filter {
            println("Bonded List")
            ble.bondedDevices.forEach {
                println("Bonded: ${it.name}")
            }
            ble.bondedDevices.any {
                it.macAddress.lowercase() == this.macAddress.lowercase()
                        && it.bluetoothDevice.bondState == BluetoothDevice.BOND_BONDED
            }
        }
        .firstOrError()
        .ignoreElement()
}