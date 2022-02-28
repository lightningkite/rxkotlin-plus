package com.lightningkite.rx.viewgenerators

import io.reactivex.rxjava3.kotlin.addTo
import com.lightningkite.rx.android.removed
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.util.concurrent.TimeUnit


/**
 * Displays the Observable's view generator as the content of this view.
 * Uses a single animation.
 */
fun <T : ViewGenerator, SOURCE : Observable<T>> SOURCE.showIn(
    swapView: SwapView,
    dependency: ActivityAccess,
    transition: ViewTransitionUnidirectional = ViewTransitionUnidirectional.FADE
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
    viewTransition: ViewTransition = ViewTransition.PUSH_POP
): SOURCE {
    var currentData: ViewGenerator? = null
    var currentStackSize = 0
    this.debounce(1L, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribeBy { datas ->
        val newData = datas.lastOrNull()
        val newStackSize = datas.size
        if (currentData == newData) return@subscribeBy
        swapView.swap(
            newData?.generate(dependency), when {
                currentStackSize == 0 || newStackSize == 0 -> viewTransition.neutral
                newStackSize > currentStackSize -> viewTransition.push
                newStackSize < currentStackSize -> viewTransition.pop
                else -> viewTransition.neutral
            }
        )
        currentData = newData
        currentStackSize = newStackSize
    }.addTo(swapView.removed)
    return this
}

fun Observable<List<ViewGenTransition>>.showIn(swapView: SwapView, dependency: ActivityAccess) {
    var currentData: ViewGenTransition? = null
    var currentStackSize = 0
    this.debounce(1L, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribeBy { data ->
        val newData = data.lastOrNull()
        val newStackSize = data.size
        if (currentData == newData || newData == null) return@subscribeBy
        swapView.swap(
            newData.viewGen.generate(dependency), when {
                currentStackSize == 0 || newStackSize == 0 -> ViewTransitionUnidirectional.NONE
                newStackSize > currentStackSize -> newData.transition.push
                newStackSize < currentStackSize -> newData.transition.pop
                else -> ViewTransitionUnidirectional.NONE
            }
        )
        currentData = newData
        currentStackSize = newStackSize
    }.addTo(swapView.removed)
}
