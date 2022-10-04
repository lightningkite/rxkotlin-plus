package com.lightningkite.rx.android.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.os.Build
import android.os.ParcelUuid
import android.util.Log
import com.lightningkite.rx.android.staticApplicationContext
import com.lightningkite.rx.filterIsPresent
import com.lightningkite.rx.forever
import com.lightningkite.rx.kotlin
import com.lightningkite.rx.mapNotNull
import com.lightningkite.rx.viewgenerators.ActivityAccess
import com.polidea.rxandroidble3.RxBleClient
import com.polidea.rxandroidble3.RxBleConnection
import com.polidea.rxandroidble3.RxBleCustomOperation
import com.polidea.rxandroidble3.RxBleDevice
import com.polidea.rxandroidble3.RxBleRadioOperationCustom
import com.polidea.rxandroidble3.internal.connection.RxBleConnectionImpl
import com.polidea.rxandroidble3.internal.connection.RxBleGattCallback
import com.polidea.rxandroidble3.scan.ScanFilter
import com.polidea.rxandroidble3.scan.ScanSettings
import com.polidea.rxandroidble3.scan.ScanSettings.SCAN_MODE_LOW_LATENCY
import com.polidea.rxandroidble3.scan.ScanSettings.SCAN_MODE_LOW_POWER
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.exceptions.UndeliverableException
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

data class BleScanResult(
    val name: String?,
    val rssi: Int,
    val id: String,
    val services: Map<UUID, ByteArray>,
)

private object FixRxUndeliverable {
    init {
        val originalHandler = RxJavaPlugins.getErrorHandler()
        RxJavaPlugins.setErrorHandler { throwable ->
            if (throwable is UndeliverableException) {
                return@setErrorHandler // ignore BleExceptions as they were surely delivered at least once
            }
            // add other custom handlers if needed
            originalHandler?.accept(throwable) ?: throw RuntimeException("No handler available", throwable)
        }
    }
}

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
            services = (it.scanRecord?.serviceData?.mapKeys { it.key.uuid }
                ?: mapOf()) + (it.scanRecord?.serviceUuids?.associate { it.uuid to byteArrayOf() } ?: mapOf())
        )
    }
}

fun ActivityAccess.bleDevice(
    id: String,
    mtu: Int? = null,
): BleDevice = AndroidBleDevice[this, id, mtu]


private val ble = RxBleClient.create(staticApplicationContext)

private class AndroidBleDevice private constructor(
    val activityAccess: ActivityAccess,
    val device: RxBleDevice,
    val mtu: Int? = null,
) :
    BleDevice {
    override val id: String
        get() = device.macAddress

    companion object {
        private val cache = HashMap<String, AndroidBleDevice>()
        operator fun get(activityAccess: ActivityAccess, id: String, mtu: Int?): AndroidBleDevice {
            val value = cache.getOrPut(id) {
                AndroidBleDevice(activityAccess, ble.getBleDevice(id), mtu)
            }
            if(value.mtu != mtu){
                val newDevice = AndroidBleDevice(activityAccess, ble.getBleDevice(id), mtu)
                cache[id] = newDevice
                return newDevice
            }
            return value
        }
    }

    //get bluetooth permission
    private val rawConnection = activityAccess.requireBle.flatMapObservable {
        println("Attempting connection to ${device.name ?: device.macAddress}")
        //Check and see if bond is required
        device.establishConnection(false)
            .doOnNext { println("Established connection to ${device.name ?: device.macAddress}") }
    }.map { Optional.of(it) } //Maps to a optional because a connection may or may not be present
    val connection: Observable<RxBleConnection> = rawConnection.onErrorResumeNext {
        println("Connection failed to ${device.name ?: device.macAddress} with $it")
        it.printStackTrace()
        /*Connects the pipelines to allow a connection to fire if present and do nothing
        if no connection is present*/
        Observable.concat<Optional<RxBleConnection>>(
            Observable.just(Optional.empty()),
            Observable.empty<Optional<RxBleConnection>>()
                .delay(1000L, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()),
            rawConnection
        )
    }.filterIsPresent()
        .flatMapSingle {
            mtu?.let { mtu ->
                it.requestMtu(mtu).map { _ -> it }
            } ?: Single.just(it)
        }
        .replay(1).refCount(5_000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())

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

    override fun read(characteristic: BleCharacteristic): Single<ByteArray> = connection
        .doOnSubscribe { connection.take(5000L, TimeUnit.MILLISECONDS).subscribeBy(onError = {}).forever() }
        .firstOrError().flatMap {
            it.readCharacteristic(characteristic.id)
        }
        .doOnSuccess { Log.d("RxPlusAndroidBle", "Read ${characteristic.id}: ${it.contentToString()} (${it.toString(Charsets.UTF_8)})") }
        .doOnError {
            Log.w("RxPlusAndroidBle", "Failed to read ${characteristic.id}")
            connection.firstOrError().flatMapObservable {
                it.queue(CustomRefresh())
            }.subscribeBy(onError = { it.printStackTrace()}, onNext = { println("Refresh $it") })
        }


    override fun write(characteristic: BleCharacteristic, value: ByteArray): Single<Unit> =
        connection.firstOrError().flatMap {
            it.writeCharacteristic(characteristic.id, value)
        }.map { Unit }
            .doOnSuccess { Log.d("RxPlusAndroidBle", "Wrote ${characteristic.id}: ${value.contentToString()} (${value.toString(Charsets.UTF_8)})") }
            .doOnError {
                Log.w("RxPlusAndroidBle", "Failed to write ${characteristic.id}")
                connection.firstOrError().flatMapObservable {
                    it.queue(CustomRefresh())
                }.subscribeBy(onError = { it.printStackTrace()}, onNext = { println("Refresh $it") })
            }


    override fun notify(characteristic: BleCharacteristic): Observable<ByteArray> = connection.flatMap {
        it.setupNotification(characteristic.id).switchMap { it }
    }.retryWhen { it.delay(1000L, TimeUnit.MILLISECONDS) }
        .doOnNext { Log.d("RxPlusAndroidBle", "Notified ${characteristic.id}: ${it.contentToString()} (${it.toString(Charsets.UTF_8)})") }

}

fun BleDevice.readNotify(characteristic: BleCharacteristic) = Observable.merge(
    read(characteristic).toObservable().retry(1).onErrorComplete(),
    notify(characteristic)
)

class CustomRefresh: RxBleCustomOperation<Boolean> {

    @Throws(Throwable::class)
    override fun asObservable(bluetoothGatt: BluetoothGatt,
                              rxBleGattCallback: RxBleGattCallback,
                              scheduler: Scheduler
    ): Observable<Boolean> {

        return Observable.fromCallable<Boolean> { refreshDeviceCache(bluetoothGatt) }
            .delay(500, TimeUnit.MILLISECONDS, Schedulers.computation())
            .subscribeOn(scheduler)
    }

    private fun refreshDeviceCache(gatt: BluetoothGatt): Boolean {
        var isRefreshed = false

        try {
            val localMethod = gatt.javaClass.getMethod("refresh")
            if (localMethod != null) {
                isRefreshed = (localMethod.invoke(gatt) as Boolean)
                Log.i("CustomRefresh", "Gatt cache refresh successful: [$isRefreshed]")
            }
        } catch (localException: Exception) {
            Log.e("CustomRefresh", "An exception occured while refreshing device", localException)
        }

        return isRefreshed
    }
}