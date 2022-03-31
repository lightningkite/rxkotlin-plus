package com.lightningkite.rx.viewgenerators

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.descendants
import androidx.transition.*

/**
 * Shows a single view with animated transitions to other views.
 */
class SwapView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var windowInsetsListenerCopy: OnApplyWindowInsetsListener? = null
    override fun setOnApplyWindowInsetsListener(listener: OnApplyWindowInsetsListener?) {
        this.windowInsetsListenerCopy = listener
        super.setOnApplyWindowInsetsListener(listener)
    }

    override fun dispatchApplyWindowInsets(insets: WindowInsets): WindowInsets {
        val newInsets = this.windowInsetsListenerCopy?.onApplyWindowInsets(this, insets) ?: insets
        val count = childCount
        for (i in 0 until count) {
            getChildAt(i).dispatchApplyWindowInsets(newInsets)
        }
        return newInsets
    }

    private var currentView: View? = null
    private var hasCurrentView: Boolean = false

    /**
     * Swaps from the current view to another one with the given [transition].
     */
    fun swap(to: View?, transition: () -> Transition?) {
        val oldView = currentView
        var newView = to
        hasCurrentView = newView != null

        if (newView == null) {
            newView = View(context)
        } else {
            visibility = View.VISIBLE
        }

        transition()?.let {
            val oldElements = (oldView as? ViewGroup)?.descendants?.mapNotNull { it.transitionName }?.toSet() ?: setOf()
            val newElements = (newView as? ViewGroup)?.descendants?.mapNotNull { it.transitionName }?.toSet() ?: setOf()
            val sharedTransitionNames: Set<String> = oldElements.intersect(newElements)
            val sharedViews = (((oldView as? ViewGroup)?.descendants?.filter { it.transitionName in sharedTransitionNames } ?: sequenceOf()) +
            ((newView as? ViewGroup)?.descendants?.filter { it.transitionName in sharedTransitionNames } ?: sequenceOf())).toSet()

            sharedViews.forEach {
                generateSequence(it.parent as? ViewGroup) { it.parent as? ViewGroup }
                    .takeWhile { it != this }
                    .forEach { isTransitionGroup = false }
            }
            println("Shared elements: ${sharedViews.joinToString()}")
            TransitionManager.beginDelayedTransition(this, TransitionSet().apply {
                ordering = TransitionSet.ORDERING_TOGETHER
                addTransition(TransitionSet().apply {
                    ordering = TransitionSet.ORDERING_TOGETHER
                    addTransition(ChangeBounds())
                    addTransition(ChangeTransform())
                    addTransition(ChangeImageTransform())
                    addTransition(ChangeClipBounds())
                    for (v in sharedViews) {
                        addTarget(v)
                    }
                })
                addTransition(it.apply {
                    for (v in sharedViews) {
                        println("Excluded $v from normal transition $this")
                        println("Parent groups are ${generateSequence(v) { it.parent as? View }.filterIsInstance<ViewGroup>().joinToString() { it.isTransitionGroup.toString() }}")
                        excludeTarget(v, true)
                    }
//                    excludeTarget(TextView::class.java, true)
//                    addTarget(TextView::class.java)
                })
            })
        }
        addView(
            newView, FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        removeView(oldView)

        currentView = newView
        post {
            runKeyboardUpdate(newView, oldView)
        }
    }
}

