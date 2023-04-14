package com.lightningkite.rx.android.ble

import android.bluetooth.BluetoothGatt
import android.os.Build
import android.os.ParcelUuid
import android.util.Log
import com.badoo.reaktive.annotations.ExperimentalReaktiveApi
import com.badoo.reaktive.observable.*
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.plugin.ReaktivePlugin
import com.badoo.reaktive.plugin.registerReaktivePlugin
import com.badoo.reaktive.rxjavainterop.asReaktiveObservable
import com.badoo.reaktive.rxjavainterop.asReaktiveSingle
import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.scheduler.computationScheduler
import com.badoo.reaktive.scheduler.mainScheduler
import com.badoo.reaktive.single.*
import com.lightningkite.rx.android.staticApplicationContext
import com.lightningkite.rx.forever
import com.lightningkite.rx.viewgenerators.ActivityAccess
import com.polidea.rxandroidble3.RxBleClient
import com.polidea.rxandroidble3.RxBleConnection
import com.polidea.rxandroidble3.RxBleCustomOperation
import com.polidea.rxandroidble3.RxBleDevice
import com.polidea.rxandroidble3.internal.connection.RxBleGattCallback
import com.polidea.rxandroidble3.scan.ScanFilter
import com.polidea.rxandroidble3.scan.ScanSettings
import com.polidea.rxandroidble3.scan.ScanSettings.SCAN_MODE_LOW_LATENCY
import com.polidea.rxandroidble3.scan.ScanSettings.SCAN_MODE_LOW_POWER
import io.reactivex.rxjava3.exceptions.UndeliverableException
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.delay
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

data class BleScanResult(
    val name: String?,
    val rssi: Int,
    val id: String,
    val services: Map<UUID, ByteArray>,
)

//@OptIn(ExperimentalReaktiveApi::class)
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
            singleOf(Unit)
                .flatMap {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        requestPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
                            .doOnAfterSuccess { if (!it) throw Exception() }
                            .map { Unit }
                    } else {
                        singleOf(Unit)
                    }
                }
                .flatMap {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        requestPermission(android.Manifest.permission.BLUETOOTH_SCAN)
                            .doOnAfterSuccess { if (!it) throw Exception() }
                            .map { Unit }
                    } else {
                        singleOf(Unit)
                    }
                }
        } else {
            singleOf(Unit)
                .flatMap {
                    requestPermission(android.Manifest.permission.BLUETOOTH)
                        .doOnAfterSuccess { if (!it) throw Exception() }
                        .map { Unit }
                }
                .flatMap {
                    requestPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                        .doOnAfterSuccess { if (!it) throw Exception() }
                        .map { Unit }
                }
                .flatMap {
                    requestPermission(android.Manifest.permission.BLUETOOTH_ADMIN)
                        .doOnAfterSuccess { if (!it) throw Exception() }
                        .map { Unit }
                }
        }
    }

fun ActivityAccess.bleScan(
    lowPower: Boolean = false,
    filterForService: UUID? = null,
): Observable<BleScanResult> = requireBle.flatMapObservable {
    ble
        .scanBleDevices(
            ScanSettings.Builder().setScanMode(if (lowPower) SCAN_MODE_LOW_POWER else SCAN_MODE_LOW_LATENCY).build(),
            filterForService?.let { ScanFilter.Builder().setServiceUuid(ParcelUuid(it)).build() } ?: ScanFilter.empty()
        )
        .map {
            BleScanResult(
                name = it.bleDevice.name,
                rssi = it.rssi,
                id = it.bleDevice.macAddress,
                services = (it.scanRecord?.serviceData?.mapKeys { it.key.uuid }
                    ?: mapOf()) + (it.scanRecord?.serviceUuids?.associate { it.uuid to byteArrayOf() } ?: mapOf())
            )
        }
        .asReaktiveObservable()
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
            if (value.mtu != mtu) {
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
            .asReaktiveObservable()
            .doOnAfterNext { println("Established connection to ${device.name ?: device.macAddress}") }
    } //Maps to a optional because a connection may or may not be present
    val connection: Observable<RxBleConnection> = rawConnection.onErrorResumeNext {
        println("Connection failed to ${device.name ?: device.macAddress} with $it")
        it.printStackTrace()
        /*Connects the pipelines to allow a connection to fire if present and do nothing
        if no connection is present*/
        concat<RxBleConnection?>(
            observableOf(null),
            observableOfEmpty<RxBleConnection?>()
                .delay(1000L, mainScheduler),
            rawConnection
        )
    }
        .mapNotNull { it }
        .flatMapSingle {
            mtu
                ?.let { mtu -> it.requestMtu(mtu).map { _ -> it }.asReaktiveSingle() }
                ?: singleOf(it)
        }
        .replay(1).refCount()

    override fun isConnected(): Observable<Boolean> = device.observeConnectionStateChanges()
        .asReaktiveObservable()
        .map {
            when (it) {
                RxBleConnection.RxBleConnectionState.CONNECTED -> true
                else -> false
            }
        }
        .startWithValue(device.connectionState == RxBleConnection.RxBleConnectionState.CONNECTED)

    // This delay retry method needs reviewing.
    // Reaktive does not have a retryWhen like rxjava does.
    override fun stayConnected(): Observable<Unit> =
        connection.switchMap { observableOfNever<Unit>() }
            .delaySubscription(1000L, mainScheduler)
            .retry { _, _ -> true}

    override fun rssi(): Single<Int> =
        connection.firstOrError()
            .flatMap { it.readRssi().asReaktiveSingle() }
            .delaySubscription(1000L, mainScheduler)
            .retry { _, _ -> true}

    override fun read(characteristic: BleCharacteristic): Single<ByteArray> = connection
        .doOnAfterSubscribe { connection.take(5000).subscribe(onError = {}).forever() }
        .firstOrError()
        .flatMap { it.readCharacteristic(characteristic.id).asReaktiveSingle() }
        .doOnAfterSuccess {
            Log.d(
                "RxPlusAndroidBle",
                "Read ${characteristic.id}: ${it.contentToString()} (${it.toString(Charsets.UTF_8)})"
            )
        }
        .doOnAfterError {
            Log.w("RxPlusAndroidBle", "Failed to read ${characteristic.id}")
            connection.firstOrError()
                .flatMapObservable { it.queue(CustomRefresh()).asReaktiveObservable() }
                .subscribe(onError = { it.printStackTrace() }, onNext = { println("Refresh $it") })
        }


    override fun write(characteristic: BleCharacteristic, value: ByteArray): Single<Unit> =
        connection.firstOrError()
            .flatMap { it.writeCharacteristic(characteristic.id, value).asReaktiveSingle() }
            .map { Unit }
            .doOnAfterSuccess {
                Log.d(
                    "RxPlusAndroidBle",
                    "Wrote ${characteristic.id}: ${value.contentToString()} (${value.toString(Charsets.UTF_8)})"
                )
            }
            .doOnAfterError {
                Log.w("RxPlusAndroidBle", "Failed to write ${characteristic.id}")
                connection.firstOrError()
                    .flatMapObservable { it.queue(CustomRefresh()).asReaktiveObservable() }
                    .subscribe(onError = { it.printStackTrace() }, onNext = { println("Refresh $it") })
            }


    override fun notify(characteristic: BleCharacteristic): Observable<ByteArray> = connection
        .flatMap {
            it.setupNotification(characteristic.id)
                .switchMap { it }
                .asReaktiveObservable()
                .doOnAfterNext {
                    Log.d(
                        "RxPlusAndroidBle",
                        "Notified ${characteristic.id}: ${it.contentToString()} (${it.toString(Charsets.UTF_8)})"
                    )
                }

        }

    fun BleDevice.readNotify(characteristic: BleCharacteristic) = merge(
        read(characteristic).toObservable().retry(1),
        notify(characteristic)
    )

    class CustomRefresh : RxBleCustomOperation<Boolean> {

        @Throws(Throwable::class)
        override fun asObservable(
            bluetoothGatt: BluetoothGatt,
            rxBleGattCallback: RxBleGattCallback,
            scheduler: io.reactivex.rxjava3.core.Scheduler
        ): io.reactivex.rxjava3.core.Observable<Boolean> {

            return io.reactivex.rxjava3.core.Observable.fromCallable<Boolean> { refreshDeviceCache(bluetoothGatt) }
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
}