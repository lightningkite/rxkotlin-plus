package com.lightningkite.rx.viewgenerators

import android.view.View
import com.lightningkite.rx.android.removed
import com.lightningkite.rx.viewgenerators.transition.StackTransition
import com.lightningkite.rx.viewgenerators.transition.TransitionTriple
import com.lightningkite.rx.viewgenerators.transition.UsesCustomTransition
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.util.concurrent.TimeUnit


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
    this.subscribeBy { newItem ->
        if (currentItem == newItem) return@subscribeBy
        post {
            swapView.swap(makeView(newItem), transition)
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
    this.subscribeBy { newGenerator ->
        if (currentGenerator == newGenerator) return@subscribeBy
        post {
            swapView.swap(newGenerator.generate(dependency), transition)
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
    this.debounce(1L, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribeBy { value ->
        val newGenerator = value.lastOrNull()
        val newStackSize = value.size
        if (currentGenerator == newGenerator) return@subscribeBy
        swapView.swap(
            newGenerator?.generate(dependency), when {
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

