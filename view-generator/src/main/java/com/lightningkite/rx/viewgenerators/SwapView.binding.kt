package com.lightningkite.rx.viewgenerators

import android.view.View
import com.badoo.reaktive.disposable.addTo
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.debounce
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.scheduler.mainScheduler
import com.lightningkite.rx.android.removed
import com.lightningkite.rx.viewgenerators.transition.StackTransition
import com.lightningkite.rx.viewgenerators.transition.TransitionTriple
import com.lightningkite.rx.viewgenerators.transition.UsesCustomTransition
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

private class WeakKey(item: Any) {
    val ref = WeakReference(item)
    val hashCode = item.hashCode()
    override fun hashCode(): Int = hashCode
    override fun equals(other: Any?): Boolean = other is WeakKey && ref.get() == other.ref.get()
    override fun toString(): String = ref.get()?.toString() ?: hashCode.toString()
}

/**
 * Displays the Observable's view generator as the content of this view.
 * Uses a single animation.
 */
fun <T : Any, SOURCE : Observable<T>> SOURCE.showIn(
    swapView: SwapView,
    transition: TransitionTriple = TransitionTriple.FADE,
    makeView: (T) -> View,
): SOURCE {
    var currentItem: T? = null
    this.subscribe{ newItem ->
        if (currentItem == newItem) return@subscribe
        post {
            val view = makeView(newItem)
            view.tag = WeakKey(newItem)
            swapView.swap(view, transition)
            currentItem = newItem
        }
    }.addTo(swapView.removed)
    return this
}

/**
 * Displays the Observable's view generator as the content of this view.
 * Uses a single animation.
 */
fun <T : ViewGenerator, SOURCE : Observable<T>> SOURCE.showIn(
    swapView: SwapView,
    dependency: ActivityAccess,
    transition: TransitionTriple = TransitionTriple.FADE
): SOURCE {
    var currentGenerator: ViewGenerator? = null
    this.subscribe { newGenerator ->
        if (currentGenerator == newGenerator) return@subscribe
        post {
            val view = newGenerator.generate(dependency)
            view.tag = WeakKey(newGenerator)
            swapView.swap(view, transition)
            currentGenerator = newGenerator
        }
    }.addTo(swapView.removed)
    return this
}

/**
 * Displays the view generator at the top of the stack in the observable as the content of this view
 * Picks a default animation based on the change in stack size.
 */
@JvmName("showStackIn")
fun <T : ViewGenerator, SOURCE : Observable<List<T>>> SOURCE.showIn(
    swapView: SwapView,
    dependency: ActivityAccess,
    stackTransition: StackTransition = StackTransition.PUSH_POP
): SOURCE {
    var currentGenerator: ViewGenerator? = null
    var currentStackSize = 0
    this.debounce(50L, mainScheduler).subscribe { value ->
        val newGenerator = value.lastOrNull()
        val newStackSize = value.size
        if (currentGenerator == newGenerator) return@subscribe
        val view = newGenerator?.generate(dependency)
        view?.tag = WeakKey(newGenerator!!)
        swapView.swap(
            view, when {
                currentStackSize == 0 -> (newGenerator as? UsesCustomTransition)?.transition?.neutral
                    ?: stackTransition.neutral
                newStackSize == 0 -> (currentGenerator as? UsesCustomTransition)?.transition?.pop ?: stackTransition.pop
                newStackSize > currentStackSize -> (newGenerator as? UsesCustomTransition)?.transition?.push
                    ?: stackTransition.push
                newStackSize < currentStackSize -> (currentGenerator as? UsesCustomTransition)?.transition?.pop
                    ?: stackTransition.pop
                else -> (newGenerator as? UsesCustomTransition)?.transition?.neutral ?: stackTransition.neutral
            }
        )
        currentGenerator = newGenerator
        currentStackSize = newStackSize
    }.addTo(swapView.removed)
    return this
}

