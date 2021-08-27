package com.lightningkite.rxkotlinproperty

import org.junit.Assert.*
import org.junit.Test

class RxTransformationOnlyPropertyTest {
    @Test fun distinct(){
        val source = StandardProperty(0)
        var lastValue: Box<Int>? = null
        val mapped = source.distinctUntilChanged()
        var hits = 0
        val sub = mapped.subscribeBy {
            hits++
            println(it)
        }
        source.value = 0
        source.value = 0
        source.value = 1
        source.value = 2
        source.value = 2
        source.value = 1
        assertEquals(4, hits)
    }
}