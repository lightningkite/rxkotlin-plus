package com.lightningkite.rxkotlinproperty.utils

interface Mergable<T> {
    fun merge(other: T): T?
}