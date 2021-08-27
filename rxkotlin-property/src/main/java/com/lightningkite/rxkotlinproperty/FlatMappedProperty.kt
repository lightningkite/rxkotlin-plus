//! This file will translate using Khrysalis.
package com.lightningkite.rxkotlinproperty

import io.reactivex.Observable

class FlatMappedProperty<A, B>(
    val basedOn: Property<A>,
    val transformation: (A) -> Property<B>
) : Property<B>() {
    override val value: B
        get() = transformation(basedOn.value).value
    override val onChange: Observable<Box<B>>
        get() {
            val transformCopy = transformation
            return basedOn.observable.switchMap { it -> transformCopy(it.value).observable }.skip(1)
        }
}

fun <T, B> Property<T>.switchMap(transformation: (T) -> Property<B>): FlatMappedProperty<T, B> {
    return FlatMappedProperty<T, B>(this, transformation)
}

fun <T, B> Property<T>.flatMap(transformation: (T) -> Property<B>): FlatMappedProperty<T, B> {
    return FlatMappedProperty<T, B>(this, transformation)
}

fun <T: Any, B: Any> Property<T?>.switchMapNotNull(transformation: (T) -> Property<B?>): FlatMappedProperty<T?, B?> {
    return FlatMappedProperty<T?, B?>(this) { item ->
        if(item != null) return@FlatMappedProperty transformation(item)
        else return@FlatMappedProperty ConstantProperty<B?>(null)
    }
}

fun <T: Any, B: Any> Property<T?>.flatMapNotNull(transformation: (T) -> Property<B?>): FlatMappedProperty<T?, B?> {
    return FlatMappedProperty<T?, B?>(this) { item ->
        if(item != null) return@FlatMappedProperty transformation(item)
        else return@FlatMappedProperty ConstantProperty<B?>(null)
    }
}

class MutableFlatMappedProperty<A, B>(
    val basedOn: Property<A>,
    val transformation: (A) -> MutableProperty<B>
) : MutableProperty<B>() {
    override var value: B
        get() = transformation(basedOn.value).value
        set(value) {
            transformation(basedOn.value).value = value
        }

    var lastProperty: MutableProperty<B>? = null

    override val onChange: Observable<Box<B>>
        get() {
            val transformCopy = transformation
            return basedOn.observable.switchMap { it: Box<A> ->
                val prop = transformCopy(it.value)
                this?.lastProperty = prop
                prop.observable
            }.skip(1)
        }

    override fun update() {
        lastProperty?.update()
    }
}

fun <T, B> Property<T>.switchMapMutable(transformation: (T) -> MutableProperty<B>): MutableFlatMappedProperty<T, B> {
    return MutableFlatMappedProperty<T, B>(this, transformation)
}

fun <T, B> Property<T>.flatMapMutable(transformation: (T) -> MutableProperty<B>): MutableFlatMappedProperty<T, B> {
    return MutableFlatMappedProperty<T, B>(this, transformation)
}
