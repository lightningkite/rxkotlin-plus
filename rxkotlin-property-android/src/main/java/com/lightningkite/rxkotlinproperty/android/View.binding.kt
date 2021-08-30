package com.lightningkite.rxkotlinproperty.android

import android.view.View
import com.lightningkite.rxkotlinproperty.Property
import com.lightningkite.rxkotlinproperty.subscribeBy
import com.lightningkite.rxkotlinproperty.until

/**
 *
 * Binds the visibility of the view to the observable provided.
 * If the value if false, the view will not be visible, nor intractable, though the
 * view will still take up space.
 *
 */

fun View.bindVisible(observable: Property<Boolean>) {
    observable.subscribeBy { value ->
        this.visibility = if (value) View.VISIBLE else View.INVISIBLE
    }.until(this.removed)
}


/**
 *
 * Binds the existance of the view to the observable provided.
 * If the value if false, the view will not exist, meaning it will not be visible,
 * it will not be intractable, and it will not take up any space.
 *
 */
fun View.bindExists(observable: Property<Boolean>) {
    observable.subscribeBy { value ->
        this.visibility = if (value) View.VISIBLE else View.GONE
    }.until(this.removed)
}
