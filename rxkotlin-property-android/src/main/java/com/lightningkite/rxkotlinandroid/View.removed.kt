package com.lightningkite.rxkotlinandroid

import android.view.View
import com.lightningkite.rxkotlinproperty.DisposeCondition
import com.lightningkite.rxkotlinproperty.until
import java.util.*

val View.removed: DisposeCondition
    get() {
        return DisposeCondition { disposable ->
            this.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewDetachedFromWindow(v: View) {
                    disposable.dispose()
                    v.removeOnAttachStateChangeListener(this)
                }

                override fun onViewAttachedToWindow(v: View) {
                    findReplacedRemovedCondition()?.let {
                        v.removeOnAttachStateChangeListener(this)
                        disposable.until(it)
                    }
                }
            })
        }
    }

val View_lifecycleDeferTo = WeakHashMap<View, DisposeCondition>()
fun View.setRemovedCondition(condition: DisposeCondition) {
    View_lifecycleDeferTo[this] = condition
}
fun View.findReplacedRemovedCondition(): DisposeCondition? {
    return View_lifecycleDeferTo[this] ?: (this.parent as? View)?.findReplacedRemovedCondition()
}