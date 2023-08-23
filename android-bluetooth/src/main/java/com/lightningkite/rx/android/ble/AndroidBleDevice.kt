package com.lightningkite.rx.android.ble

import android.util.Log
import com.lightningkite.rx.filterIsPresent
import com.lightningkite.rx.forever
import com.lightningkite.rx.viewgenerators.ActivityAccess
import com.polidea.rxandroidble3.RxBleConnection
import com.polidea.rxandroidble3.RxBleDevice
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.util.*
import java.util.concurrent.TimeUnit

internal class AndroidBleDevice private constructor(
    val activityAccess: ActivityAccess,
    val device: RxBleDevice,
    val mtu: Int? = null,
) :
    BleDevice {
    override val id: String
        get() = device.macAddress

    companion object {
        private val cache = HashMap<String, AndroidBleDevice>()
        operator fun get(activityAccess: ActivityAccess, id: String, mtu: Int?): AndroidBleDevice = cache.getOrPut(id) {
            AndroidBleDevice(activityAccess, bleClient.getBleDevice(id), mtu)
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
        .replay(1)
        .refCount(5_000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())

    override fun isConnected(): Observable<Boolean> = device.observeConnectionStateChanges()
        .startWithItem(device.connectionState)
        .map {
        when (it) {
            RxBleConnection.RxBleConnectionState.CONNECTED -> true
            else -> false
        }
    }

    override fun stayConnected(): Observable<Unit> =
        connection.switchMap { Observable.never<Unit>() }.retryWhen { it.delay(1000L, TimeUnit.MILLISECONDS) }

    override fun rssi(): Single<Int> =
        connection.firstOrError().flatMap { it.readRssi() }

    override fun read(characteristic: BleCharacteristic): Single<ByteArray> = connection
        .doOnSubscribe { connection.take(5000L, TimeUnit.MILLISECONDS).subscribeBy(onError = {}).forever() }
        .firstOrError().flatMap {
            it.readCharacteristic(characteristic.id)
        }
        .doOnSuccess {
            Log.d(
                "RxPlusAndroidBle",
                "Read ${characteristic.id}: ${it.contentToString()} (${it.toString(Charsets.UTF_8)})"
            )
        }
        .doOnError { Log.w("RxPlusAndroidBle", "Failed to read ${characteristic.id}") }


    override fun write(characteristic: BleCharacteristic, value: ByteArray): Single<Unit> =
        connection.firstOrError().flatMap {
            it.writeCharacteristic(characteristic.id, value)
        }.map { Unit }
            .doOnSuccess {
                Log.d(
                    "RxPlusAndroidBle",
                    "Wrote ${characteristic.id}: ${value.contentToString()} (${value.toString(Charsets.UTF_8)})"
                )
            }
            .doOnError { Log.w("RxPlusAndroidBle", "Failed to write ${characteristic.id}") }


    override fun notify(characteristic: BleCharacteristic): Observable<ByteArray> = connection.flatMap {
        it.setupNotification(characteristic.id).switchMap { it }
    }
        .doOnNext {
            Log.d(
                "RxPlusAndroidBle",
                "Notified ${characteristic.id}: ${it.contentToString()} (${it.toString(Charsets.UTF_8)})"
            )
        }

}