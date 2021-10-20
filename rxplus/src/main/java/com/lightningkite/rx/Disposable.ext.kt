package com.lightningkite.rx

import io.reactivex.rxjava3.disposables.Disposable

/**
 *
 * An explicit call on the disposable to state we plan on ignoring
 * this disposable.
 *
 */
fun Disposable.forever() {}
