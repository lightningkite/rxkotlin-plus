package com.lightningkite.rxkotlinviewgenerators.views

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.lightningkite.rxkotlinandroid.removed
import com.lightningkite.rxkotlinproperty.until
import com.lightningkite.rxkotlinviewgenerators.views.widget.SwapView
import com.lightningkite.rxkotlinproperty.ObservableStack
import com.lightningkite.rxkotlinproperty.Property
import com.lightningkite.rxkotlinproperty.subscribeBy
import com.lightningkite.rxkotlinviewgenerators.android.ActivityAccess
import com.lightningkite.rxkotlinviewgenerators.android.runKeyboardUpdate


/**
 *
 * Binds the view in the swap view to the top ViewGenerator in the ObservableStack.
 * Any changes to the top of the stack will manifest in the swap view.
 *
 */

fun <T: ViewGenerator>SwapView.bindStack(dependency: ActivityAccess, obs: ObservableStack<T>) {
    var currentData = obs.stack.lastOrNull()
    var currentStackSize = obs.stack.size
    var currentView = currentData?.generate(dependency) ?: View(context)
    addView(
        currentView, FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    )
    obs.subscribeBy { datas ->
        visibility = if(datas.isEmpty()) View.GONE else View.VISIBLE
        post {
            if (currentData == datas.lastOrNull()) return@post

            val oldView = currentView
            val oldStackSize = currentStackSize

            var newView = obs.stack.lastOrNull()?.generate(dependency)
            if (newView == null) {
                newView = View(context)
            }
            addView(
                newView, FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            val newStackSize = datas.size

            when {
                oldStackSize == 0 -> {
                    oldView.animate().alpha(0f)
                    newView.alpha = 0f
                    newView.animate().alpha(1f)
                }
                oldStackSize > newStackSize -> {
                    oldView.animate().translationX(width.toFloat())
                    newView.translationX = -width.toFloat()
                    newView.animate().translationX(0f)
                }
                oldStackSize < newStackSize -> {
                    oldView.animate().translationX(-width.toFloat())
                    newView.translationX = width.toFloat()
                    newView.animate().translationX(0f)
                }
                else -> {
                    oldView.animate().alpha(0f)
                    newView.alpha = 0f
                    newView.animate().alpha(1f)
                }
            }
            oldView.animate().withEndAction { removeView(oldView) }

            currentData = datas.lastOrNull()
            currentView = newView
            currentStackSize = newStackSize

            post {
                runKeyboardUpdate(dependency, newView, oldView)
            }
        }
    }.until(this.removed)
}

fun <T: ViewGenerator>SwapView.bindStackWithAnimation(dependency: ActivityAccess, obs: ObservableStack<Pair<T, ViewTransition>>) {
    var currentData = obs.stack.lastOrNull()
    var currentStackSize = obs.stack.size
    var currentView = currentData?.first?.generate(dependency) ?: View(context)

    addView(currentView, FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT))

    obs.subscribeBy { datas ->
        if (currentData == datas.lastOrNull()) return@subscribeBy

        val oldView = currentView
        val oldStackSize = currentStackSize

        var newView = obs.stack.lastOrNull()?.first?.generate(dependency)
        val transition = obs.stack.lastOrNull()?.second

        if (newView == null) {
            newView = View(context)
            visibility = View.GONE
        } else {
            visibility = View.VISIBLE
        }

        val newStackSize = datas.size

        post {
            addView(
                newView, FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT))

            when {
                oldStackSize == 0 -> {
                    transition?.enterPush?.invoke(newView)?.start()
                    transition?.exitPush?.invoke(oldView)?.withEndAction {
                        removeView(oldView)
                    }
                }
                oldStackSize > newStackSize -> {
                    currentData?.second?.enterPop?.invoke(newView)?.start()
                    currentData?.second?.exitPop?.invoke(oldView)?.withEndAction {
                        removeView(oldView)
                    }
                }
                oldStackSize < newStackSize -> {
                    transition?.enterPush?.invoke(newView)?.start()
                    transition?.exitPush?.invoke(oldView)?.withEndAction {
                        removeView(oldView)
                    }
                }
                else -> {
                    transition?.enterPush?.invoke(newView)?.start()
                    transition?.exitPush?.invoke(oldView)?.withEndAction {
                        removeView(oldView)
                    }
                }
            }

            currentData = datas.lastOrNull()
            currentView = newView
            currentStackSize = newStackSize
        }
    }.until(this.removed)
}

/**
 *
 * Binds the view in the swap view to the top ViewGenerator in the ObservableStack.
 * Any changes to the top of the stack will manifest in the swap view.
 *
 */

fun SwapView.bindList(dependency: ActivityAccess, vgs: List<ViewGenerator>, index: Property<Int>) {
    var currentData = vgs.getOrNull(index.value)
    var currentIndex = index.value
    var currentView = currentData?.generate(dependency) ?: View(context)
    addView(
        currentView, FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    )
    index.subscribeBy { newIndex ->
        post {
            if (currentIndex == newIndex) return@post

            val oldView = currentView
            val oldIndex = currentIndex

            var newView = vgs.getOrNull(newIndex)?.generate(dependency)
            if (newView == null) {
                newView = View(context)
                visibility = View.GONE
            } else {
                visibility = View.VISIBLE
            }
            addView(
                newView, FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            val newStackSize = newIndex

            when {
//                oldStackSize == 0 -> {
//                    oldView.animate().alpha(0f)
//                    newView.alpha = 0f
//                    newView.animate().alpha(1f)
//                }
                oldIndex > newStackSize -> {
                    oldView.animate().translationX(width.toFloat())
                    newView.translationX = -width.toFloat()
                    newView.animate().translationX(0f)
                }
                oldIndex < newStackSize -> {
                    oldView.animate().translationX(-width.toFloat())
                    newView.translationX = width.toFloat()
                    newView.animate().translationX(0f)
                }
                else -> {
                    oldView.animate().alpha(0f)
                    newView.alpha = 0f
                    newView.animate().alpha(1f)
                }
            }
            oldView.animate().withEndAction { removeView(oldView) }

            currentData = vgs.getOrNull(newIndex)
            currentView = newView
            currentIndex = newStackSize
        }
    }.until(this.removed)

}
