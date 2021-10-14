package com.lightningkite.rx.viewgenerators

import android.view.View
import android.view.ViewPropertyAnimator

typealias TransitionAnimation = (View) -> ViewPropertyAnimator

data class ViewTransitionUnidirectional(
    val enter: TransitionAnimation,
    val exit: TransitionAnimation
) {
    companion object {
        val PUSH = ViewTransitionUnidirectional(enter = { view ->
            view.translationX = (view.parent as View).width.toFloat()
            view.animate()
                .translationX(0f)
        }, exit = { view ->
            view.animate()
                .translationX(-1f * view.width.toFloat())
        })
        val POP = ViewTransitionUnidirectional(enter = { view ->
            view.translationX = -1f * (view.parent as View).width.toFloat()
            view.animate()
                .translationX(0f)
        }, exit = { view ->
            view.animate()
                .translationX(view.width.toFloat())
        })
        val UP = ViewTransitionUnidirectional(enter = { view ->
            view.translationY = (view.parent as View).height.toFloat()
            view.animate()
                .translationY(0f)
        }, exit = { view ->
            view.animate()
                .translationY(-1f * view.height.toFloat())
        })
        val DOWN = ViewTransitionUnidirectional(enter = { view ->
            view.translationY = -1f * (view.parent as View).height.toFloat()
            view.animate()
                .translationY(0f)
        }, exit = { view ->
            view.animate()
                .translationY(view.height.toFloat())
        })
        val FADE = ViewTransitionUnidirectional(enter = { view ->
            view.alpha = 0f
            view.visibility = View.VISIBLE
            view.animate()
                .alpha(1f)
        }, exit = { view ->
            view.animate()
                .alpha(0f)
        })
        val NONE = ViewTransitionUnidirectional(enter = { view ->
            view.animate()
                .translationX(0f)
        }, exit = { view ->
            view.animate()
                .translationX(0f)
        })
    }
}

data class ViewTransition(
    val push: ViewTransitionUnidirectional,
    val pop: ViewTransitionUnidirectional,
    val neutral: ViewTransitionUnidirectional,
) {
    companion object {
        val PUSH_POP = ViewTransition(ViewTransitionUnidirectional.PUSH, ViewTransitionUnidirectional.POP, ViewTransitionUnidirectional.FADE)
        val UP_DOWN = ViewTransition(ViewTransitionUnidirectional.UP, ViewTransitionUnidirectional.DOWN, ViewTransitionUnidirectional.FADE)
        val FADE_IN_OUT = ViewTransition(ViewTransitionUnidirectional.FADE, ViewTransitionUnidirectional.FADE, ViewTransitionUnidirectional.FADE)
        val NONE = ViewTransition(ViewTransitionUnidirectional.NONE, ViewTransitionUnidirectional.NONE, ViewTransitionUnidirectional.NONE)
    }
}