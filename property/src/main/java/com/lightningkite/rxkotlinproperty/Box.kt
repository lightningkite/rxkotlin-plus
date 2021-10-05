package com.lightningkite.rxkotlinproperty

/*inline*/ class Box<out T> private constructor(val valueRaw: Any?) {

    @Suppress("UNCHECKED_CAST")
    val value: T
        get() = if (valueRaw == NullRep) null as T else valueRaw as T

    companion object {
        private val NullRep = object {}
        private val NullRepBox = Box<Any?>(NullRep)

        @Suppress("UNCHECKED_CAST")
        fun <T> wrap(value: T): Box<T> = if (value == null) NullRepBox as Box<T> else Box(value)
    }

    override fun toString(): String {
        return "Box($value)"
    }

    override fun equals(other: Any?): Boolean {
        if (other is Box<*>) return value == other.value
        return value == other
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}

