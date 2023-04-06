package com.lightningkite.rx.android

import android.view.View
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.addTo
import java.util.*

/**
 * A CompositeDisposable which is tied to the life cycle of the view.
 * When the view is detached from the window the CompositeDisposable is
 * disposed.
 */
val View.removed: CompositeDisposable
    get() {
        return View_lifecycle.getOrPut(this) {
            val composite = CompositeDisposable()
            this.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewDetachedFromWindow(v: View) {
                    composite.clear()
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

private val View_lifecycle = WeakHashMap<View, CompositeDisposable>()
private val View_lifecycleDeferTo = WeakHashMap<View, CompositeDisposable>()
fun View.setRemovedCondition(condition: CompositeDisposable) {
    View_lifecycleDeferTo[this] = condition
}
fun View.findReplacedRemovedCondition(): CompositeDisposable? {
    return View_lifecycleDeferTo[this] ?: (this.parent as? View)?.findReplacedRemovedCondition()
}
