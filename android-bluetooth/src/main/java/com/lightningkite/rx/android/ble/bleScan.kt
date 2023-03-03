package com.lightningkite.rx.android.ble

import android.os.ParcelUuid
import com.lightningkite.rx.viewgenerators.ActivityAccess
import com.polidea.rxandroidble3.scan.ScanFilter
import com.polidea.rxandroidble3.scan.ScanSettings
import io.reactivex.rxjava3.core.Observable
import java.util.*


fun ActivityAccess.bleScan(
    lowPower: Boolean = false,
    filterForService: UUID? = null,
): Observable<BleScanResult> = requireBle.flatMapObservable {
    bleClient.scanBleDevices(
        ScanSettings.Builder().setScanMode(if (lowPower) ScanSettings.SCAN_MODE_LOW_POWER else ScanSettings.SCAN_MODE_LOW_LATENCY).build(),
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