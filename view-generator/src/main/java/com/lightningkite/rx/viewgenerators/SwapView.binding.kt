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
fun <T: Any, SOURCE : Observable<T>> SOURCE.showIn(
    swapView: SwapView,
    transition: TransitionTriple = TransitionTriple.FADE,
    makeView: (T)->View,
): SOURCE {
    var currentData: T? = null
    this.subscribeBy { value ->
        post {
            if (currentData == value) return@post
            swapView.swap(makeView(value), transition)
            currentData = value
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
    var currentData: ViewGenerator? = null
    this.subscribeBy { datas ->
        post {
            val newData = datas
            if (currentData == newData) return@post
            swapView.swap(newData.generate(dependency), transition)
            currentData = newData
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
    var currentData: ViewGenerator? = null
    var currentStackSize = 0
    this.debounce(1L, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribeBy { datas ->
        val newData = datas.lastOrNull()
        val newStackSize = datas.size
        if (currentData == newData) return@subscribeBy
        swapView.swap(
            newData?.generate(dependency), when {
                currentStackSize == 0 -> (newData as? UsesCustomTransition)?.transition?.neutral ?: stackTransition.neutral
                newStackSize == 0 -> (currentData as? UsesCustomTransition)?.transition?.pop ?: stackTransition.pop
                newStackSize > currentStackSize -> (newData as? UsesCustomTransition)?.transition?.push ?: stackTransition.push
                newStackSize < currentStackSize -> (currentData as? UsesCustomTransition)?.transition?.pop ?: stackTransition.pop
                else -> (newData as? UsesCustomTransition)?.transition?.neutral ?: stackTransition.neutral
            }
        )
        currentData = newData
        currentStackSize = newStackSize
    }.addTo(swapView.removed)
    return this
}

