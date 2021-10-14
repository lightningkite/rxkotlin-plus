package com.lightningkite.rx.viewgenerators

import android.view.View
import com.lightningkite.rx.ValueSubject
import io.reactivex.rxjava3.kotlin.addTo
import com.lightningkite.rx.android.removed
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.BehaviorSubject


/**
 *
 * Binds the view in the swap view to the top ViewGenerator in the PropertyStack.
 * Any changes to the top of the stack will manifest in the swap view.
 *
 */

fun <T : ViewGenerator> SwapView.bindStack(dependency: ActivityAccess, obs: StackSubject<T>) {
    var currentData = obs.value.lastOrNull()
    var currentStackSize = obs.value.size
    obs.subscribeBy { datas ->
        visibility = if (datas.isEmpty()) View.GONE else View.VISIBLE
        post {
            val newData = datas.lastOrNull()
            val newStackSize = datas.size
            if (currentData == newData) return@post
            swap(newData?.generate(dependency), when {
                newStackSize > currentStackSize -> ViewTransitionUnidirectional.PUSH
                newStackSize < currentStackSize -> ViewTransitionUnidirectional.POP
                else -> ViewTransitionUnidirectional.FADE
            })
            currentData = newData
            currentStackSize = newStackSize
        }
    }.addTo(this.removed)
}

fun <T : ViewGenerator> SwapView.bindStackWithAnimation(
    dependency: ActivityAccess,
    obs: StackSubject<Pair<T, ViewTransition>>
) {
    var currentData = obs.value.lastOrNull()
    var currentStackSize = obs.value.size
    obs.subscribeBy { datas ->
        visibility = if (datas.isEmpty()) View.GONE else View.VISIBLE
        post {
            val newData = datas.lastOrNull()
            val newStackSize = datas.size
            if (currentData == newData) return@post
            swap(newData?.first?.generate(dependency), when {
                newStackSize > currentStackSize -> newData?.second?.push ?: ViewTransitionUnidirectional.NONE
                newStackSize < currentStackSize -> newData?.second?.pop ?: ViewTransitionUnidirectional.NONE
                else -> newData?.second?.neutral ?: ViewTransitionUnidirectional.FADE
            })
            currentData = newData
            currentStackSize = newStackSize
        }
    }.addTo(this.removed)
}

/**
 *
 * Binds the view in the swap view to the top ViewGenerator in the PropertyStack.
 * Any changes to the top of the stack will manifest in the swap view.
 *
 */

fun SwapView.bindList(dependency: ActivityAccess, vgs: List<ViewGenerator>, index: ValueSubject<Int>) {
    var currentData = vgs.getOrNull(index.value)
    var currentIndex = index.value
    index.subscribeBy { newIndex ->
        post {
            val newData = vgs.getOrNull(newIndex)
            if (currentData == newData) return@post
            swap(newData?.generate(dependency), when {
                newIndex > currentIndex -> ViewTransitionUnidirectional.PUSH
                newIndex < currentIndex -> ViewTransitionUnidirectional.POP
                else -> ViewTransitionUnidirectional.FADE
            })
            currentData = newData
            currentIndex = newIndex
        }
    }.addTo(this.removed)
}
