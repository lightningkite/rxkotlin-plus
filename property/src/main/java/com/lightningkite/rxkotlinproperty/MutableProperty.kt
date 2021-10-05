//! This file will translate using Khrysalis.
package com.lightningkite.rxkotlinproperty

abstract class MutableProperty<T> : Property<T>() {
    abstract override var value: T
    abstract fun update()
}
