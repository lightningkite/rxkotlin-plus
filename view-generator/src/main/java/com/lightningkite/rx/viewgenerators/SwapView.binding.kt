package com.lightningkite.rx.viewgenerators

import android.view.View
import com.lightningkite.rx.ValueSubject
import io.reactivex.rxjava3.kotlin.addTo
import com.lightningkite.rx.android.removed
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.BehaviorSubject


/**
 *
 * Binds the view in the swap view to the top ViewGenerator in the PropertyStack.
 * Any changes to the top of the stack will manifest in the swap view.
 *
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

@JvmName("showStackIn")
fun <T : ViewGenerator, SOURCE : Observable<List<T>>> SOURCE.showIn(
    view: SwapView,
    dependency: ActivityAccess
): SOURCE {
    var currentData: ViewGenerator? = null
    var currentStackSize = 0
    this.subscribeBy { datas ->
        view.visibility = if (datas.isEmpty()) View.GONE else View.VISIBLE
        post {
            val newData = datas.lastOrNull()
            val newStackSize = datas.size
            if (currentData == newData) return@post
            view.swap(
                newData?.generate(dependency), when {
                    currentStackSize == 0 || newStackSize == 0 -> ViewTransitionUnidirectional.FADE
                    newStackSize > currentStackSize -> ViewTransitionUnidirectional.PUSH
                    newStackSize < currentStackSize -> ViewTransitionUnidirectional.POP
                    else -> ViewTransitionUnidirectional.FADE
                }
            )
            currentData = newData
            currentStackSize = newStackSize
        }
    }.addTo(view.removed)
    return this
}

@JvmName("showWithTransitionIn")
fun <T : ViewGenerator, SOURCE : Observable<List<Pair<T, ViewTransition>>>> SOURCE.showIn(
    view: SwapView,
    dependency: ActivityAccess
): SOURCE {
    var currentData: Pair<T, ViewTransition>? = null
    var currentStackSize = 0
    this.subscribeBy { datas ->
        view.visibility = if (datas.isEmpty()) View.GONE else View.VISIBLE
        post {
            val newData = datas.lastOrNull()
            val newStackSize = datas.size
            if (currentData == newData) return@post
            view.swap(
                newData?.first?.generate(dependency), when {
                    currentStackSize == 0 || newStackSize == 0 -> newData?.second?.neutral ?: ViewTransitionUnidirectional.FADE
                    newStackSize > currentStackSize -> newData?.second?.push ?: ViewTransitionUnidirectional.PUSH
                    newStackSize < currentStackSize -> newData?.second?.pop ?: ViewTransitionUnidirectional.POP
                    else -> newData?.second?.neutral ?: ViewTransitionUnidirectional.FADE
                }
            )
            currentData = newData
            currentStackSize = newStackSize
        }
    }.addTo(view.removed)
    return this
}
