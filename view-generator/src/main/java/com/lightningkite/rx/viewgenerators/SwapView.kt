package com.lightningkite.rx.viewgenerators

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.FrameLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet

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
            TransitionManager.beginDelayedTransition(this, it)
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

