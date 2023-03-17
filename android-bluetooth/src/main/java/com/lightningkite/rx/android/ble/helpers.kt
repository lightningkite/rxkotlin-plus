package com.lightningkite.rx.android.ble

import io.reactivex.rxjava3.core.Observable


fun BleDevice.readNotify(characteristic: BleCharacteristic) = Observable.merge(
    read(characteristic).toObservable().retry(1).onErrorComplete(),
    notify(characteristic)
)
