package com.lightningkite.rx.android.ble

import com.lightningkite.rx.viewgenerators.ActivityAccess


fun ActivityAccess.bleDevice(
    id: String,
    mtu: Int? = null,
): BleDevice = AndroidBleDevice[this, id, mtu]