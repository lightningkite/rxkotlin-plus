@file:Suppress("NAME_SHADOWING")

package com.lightningkite.rxkotlinandroid

import com.lightningkite.rxkotlinproperty.until
import com.lightningkite.rxkotlinproperty.MutableProperty
import com.lightningkite.rxkotlinproperty.Property
import com.lightningkite.rxkotlinproperty.combine
import com.lightningkite.rxkotlinproperty.subscribeBy
import com.rd.PageIndicatorView


/**
 *
 * Binds the page Indicator to the observable provided and the count.
 * Count will describe how many screens exist, and the selected describes which one
 * will be visible.
 *
 *  Example
 *  val page = StandardObservableProperites(1)
 *  pageView.bind(numberOfPages, page)
 */

fun PageIndicatorView.bind(count: Int = 0, selected: MutableProperty<Int>){
    this.count = count
    selected.subscribeBy{ value ->
        this.selection = value
    }.until(this.removed)
}

/**
 *
 * Binds the page Indicator to the observable provided and the count.
 * Count will describe how many screens exist, and the selected describes which one
 * will be visible.
 *
 *  Example
 *  val page = StandardObservableProperites(1)
 *  pageView.bind(numberOfPages, page)
 */
fun PageIndicatorView.bind(count: Property<Int>, selected: MutableProperty<Int>){

    count.combine(selected){count, selected ->
        return@combine Pair(count,selected)
    }.subscribeBy { (count, selected) ->
        this.count = count
        this.selection = selected
    }.until(this.removed)
}
