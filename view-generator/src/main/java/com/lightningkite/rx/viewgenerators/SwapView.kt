package com.lightningkite.rx.viewgenerators

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.FrameLayout
import androidx.core.view.children
import androidx.core.view.descendants
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Transition
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.lightningkite.rx.viewgenerators.transition.TransitionTriple

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

    private val oldInfo = HashMap<Any, SparseArray<Parcelable>>()

    /**
     * Swaps from the current view to another one with the given [transition].
     */
    fun swap(to: View?, transition: TransitionTriple) {
        val oldView = currentView
        var newView = to
        hasCurrentView = newView != null

        if (newView == null) {
            newView = View(context)
        } else {
            visibility = View.VISIBLE
        }

        val oldElements = (oldView as? ViewGroup)?.descendants?.mapNotNull { it.transitionName }?.toSet() ?: setOf()
        val newElements = (newView as? ViewGroup)?.descendants?.mapNotNull { it.transitionName }?.toSet() ?: setOf()
        val sharedNames = oldElements.intersect(newElements)
        fun View.unshared(list: MutableList<View> = ArrayList<View>()): List<View> {
            when {
                this.transitionName in sharedNames -> {
                    return list
                }
                this is RecyclerView -> list.add(this)
                this is ViewGroup -> {
                    if (this.isTransitionGroup) list.add(this)
                    else this.children.forEach { it.unshared(list) }
                }
                else -> {
                    list.add(this)
                }
            }
            return list
        }

        val newUnshared = newView.unshared()
        val oldUnshared = oldView?.unshared()

        TransitionManager.beginDelayedTransition(this, TransitionSet().apply {
            ordering = TransitionSet.ORDERING_TOGETHER
            transition.takeIf { sharedNames.isNotEmpty() }?.shared?.invoke()?.apply {
                setMatchOrder(Transition.MATCH_NAME, Transition.MATCH_INSTANCE)
                sharedNames.forEach {
                    addTarget(it)
                }
            }?.let { addTransition(it) }
            newUnshared.takeIf { it.isNotEmpty() }?.let {
                transition.enter.invoke()?.apply {
                    setMatchOrder(Transition.MATCH_INSTANCE)
                    newUnshared.forEach { addTarget(it) }
                }?.let { addTransition(it) }
            }
            oldUnshared?.takeIf { it.isNotEmpty() }?.let { oldUnshared ->
                transition.exit.invoke()?.apply {
                    setMatchOrder(Transition.MATCH_INSTANCE)
                    oldUnshared.forEach { addTarget(it) }
                }?.let { addTransition(it) }
            }
            addListener(object : Transition.TransitionListener{
                override fun onTransitionEnd(transition: Transition) {
                    if(to == null && !hasCurrentView) {
                        this@SwapView.visibility = View.GONE
                    }
                }
                override fun onTransitionStart(transition: Transition) = Unit
                override fun onTransitionCancel(transition: Transition) = Unit
                override fun onTransitionPause(transition: Transition)  = Unit
                override fun onTransitionResume(transition: Transition)  = Unit
            })
        })

        oldView?.let {
            val tag = it.tag ?: return@let
            val ar = SparseArray<Parcelable>()
            it.saveHierarchyState(ar)
            oldInfo[it.tag] = ar
        }
        removeView(oldView)
        addView(
            newView, FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        newView.tag?.let { tag ->
            oldInfo[tag]?.let {
                newView.restoreHierarchyState(it)
            }
        }

        currentView = newView
        post {
            runKeyboardUpdate(newView, oldView)
        }
    }
}
