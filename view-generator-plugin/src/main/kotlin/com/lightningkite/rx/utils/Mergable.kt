package com.lightningkite.rx.utils

interface Mergable<T> {
    fun merge(other: T): T?
}