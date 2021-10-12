package com.lightningkite.rxkotlinproperty.android

import android.view.View
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import java.util.*

val View.removed: CompositeDisposable
    get() {
        return View_lifecycleDeferTo.getOrPut(this) {
            val composite = CompositeDisposable()
            this.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewDetachedFromWindow(v: View) {
                    composite.dispose()
                    v.removeOnAttachStateChangeListener(this)
                }

                override fun onViewAttachedToWindow(v: View) {
                    findReplacedRemovedCondition()?.let {
                        v.removeOnAttachStateChangeListener(this)
                        composite.addTo(it)
                    }
                }
            })
            composite
        }
    }

private val View_lifecycleDeferTo = WeakHashMap<View, CompositeDisposable>()
fun View.setRemovedCondition(condition: CompositeDisposable) {
    View_lifecycleDeferTo[this] = condition
}
fun View.findReplacedRemovedCondition(): CompositeDisposable? {
    return View_lifecycleDeferTo[this] ?: (this.parent as? View)?.findReplacedRemovedCondition()
}