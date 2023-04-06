package com.lightningkite.rx

import com.badoo.reaktive.disposable.Disposable

/**
 *
 * An explicit call on the disposable to state we plan on ignoring
 * this disposable.
 *
 */
fun Disposable.forever() {}
