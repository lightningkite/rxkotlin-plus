package com.lightningkite.rxkotlinproperty.android

import android.view.View
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.kotlin.addTo

/**
 *
 * Binds the visibility of the view to the property provided.
 * If the value if false, the view will not be visible, nor intractable, though the
 * view will still take up space.
 *
 */

fun View.bindVisible(property: Observable<Boolean>) {
    property.subscribeBy { value ->
        this.visibility = if (value) View.VISIBLE else View.INVISIBLE
    }.addTo(this.removed)
}


/**
 *
 * Binds the existance of the view to the property provided.
 * If the value if false, the view will not exist, meaning it will not be visible,
 * it will not be intractable, and it will not take up any space.
 *
 */
fun View.bindExists(property: Observable<Boolean>) {
    property.subscribeBy { value ->
        this.visibility = if (value) View.VISIBLE else View.GONE
    }.addTo(this.removed)
}
