package com.lightningkite.rx.android.ble

import java.util.*

data class BleScanResult(
    val name: String?,
    val rssi: Int,
    val id: String,
    val services: Map<UUID, ByteArray>,
)