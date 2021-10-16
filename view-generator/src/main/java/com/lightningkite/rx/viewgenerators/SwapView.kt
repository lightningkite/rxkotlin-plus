package com.lightningkite.rx.viewgenerators

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.FrameLayout

class SwapView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var windowInsetsListenerCopy: OnApplyWindowInsetsListener? = null
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

    private var starting = true
    var currentView: View? = null

    fun swap(to: View?, transition: ViewTransitionUnidirectional) {
        val oldView = currentView
        var newView = to

        if (newView == null) {
            newView = View(context)
            visibility = View.GONE
        } else {
            visibility = View.VISIBLE
        }

        post {
            addView(
                newView, FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )

            if(starting) {
                starting = false
            } else {
                transition.enter.invoke(newView).start()
                if (oldView != null){
                    transition.exit.invoke(oldView).withEndAction {
                        removeView(oldView)
                    }
                }
            }
            currentView = newView
        }
    }
}

