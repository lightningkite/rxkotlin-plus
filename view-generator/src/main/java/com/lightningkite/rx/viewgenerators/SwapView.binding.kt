package com.lightningkite.rx.viewgenerators

import android.view.View
import com.lightningkite.rx.ValueSubject
import io.reactivex.rxjava3.kotlin.addTo
import com.lightningkite.rx.android.removed
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.BehaviorSubject


/**
 * Displays the Observable's view generator as the content of this view.
 * Uses a single animation.
 */
fun <T : ViewGenerator, SOURCE : Observable<T>> SOURCE.showIn(
    view: SwapView,
    dependency: ActivityAccess,
    transition: ViewTransitionUnidirectional = ViewTransitionUnidirectional.FADE
): SOURCE {
    var currentData: ViewGenerator? = null
    this.subscribeBy { datas ->
        post {
            val newData = datas
            if (currentData == newData) return@post
            view.swap(newData.generate(dependency), transition)
            currentData = newData
        }
    }.addTo(view.removed)
    return this
}

/**
 * Displays the view generator at the top of the stack in the observable as the content of this view
 * Picks a default animation based on the change in stack size.
 */
@JvmName("showStackIn")
fun <T : ViewGenerator, SOURCE : Observable<List<T>>> SOURCE.showIn(
    view: SwapView,
    dependency: ActivityAccess,
    viewTransition: ViewTransition = ViewTransition.PUSH_POP
): SOURCE {
    var currentData: ViewGenerator? = null
    var currentStackSize = 0
    this.subscribeBy { datas ->
        post {
            val newData = datas.lastOrNull()
            val newStackSize = datas.size
            if (currentData == newData) return@post
            view.swap(
                newData?.generate(dependency), when {
                    currentStackSize == 0 || newStackSize == 0 -> viewTransition.neutral
                    newStackSize > currentStackSize -> viewTransition.push
                    newStackSize < currentStackSize -> viewTransition.pop
                    else -> viewTransition.neutral
                }
            )
            currentData = newData
            currentStackSize = newStackSize
        }
    }.addTo(view.removed)
    return this
}
