package com.lightningkite.rx.viewgenerators

import android.view.View
import android.view.ViewPropertyAnimator

/**
 * A typealias for a function that creates a ViewPropertyAnimator on a view.
 *
 * Typically, an instance looks something like this:
 *
 * ```
 * { view -> view.animate().translationX(view.width.toFloat()) }
 * ```
 */
typealias TransitionAnimation = (View) -> ViewPropertyAnimator

/**
 * A combination of an enter and exit animation, so we can do an animation to another view.
 */
data class ViewTransitionUnidirectional(
    val enter: TransitionAnimation,
    val exit: TransitionAnimation
) {
    companion object {

        /**
         * The new view enters in from the left, and the old view exits to the right.
         * Typical for an app going to a new view.
         */
        val PUSH = ViewTransitionUnidirectional(enter = { view ->
            view.translationX = (view.parent as View).width.toFloat()
            view.animate()
                .translationX(0f)
        }, exit = { view ->
            view.animate()
                .translationX(-1f * view.width.toFloat())
        })

        /**
         * The new view enters in from the right, and the old view exits to the left.
         * Typical for an app going back an old view.
         */
        val POP = ViewTransitionUnidirectional(enter = { view ->
            view.translationX = -1f * (view.parent as View).width.toFloat()
            view.animate()
                .translationX(0f)
        }, exit = { view ->
            view.animate()
                .translationX(view.width.toFloat())
        })

        /**
         * The new view enters in from the bottom, and the old view exits to the top.
         */
        val UP = ViewTransitionUnidirectional(enter = { view ->
            view.translationY = (view.parent as View).height.toFloat()
            view.animate()
                .translationY(0f)
        }, exit = { view ->
            view.animate()
                .translationY(-1f * view.height.toFloat())
        })

        /**
         * The new view enters in from the top, and the old view exits to the bottom.
         */
        val DOWN = ViewTransitionUnidirectional(enter = { view ->
            view.translationY = -1f * (view.parent as View).height.toFloat()
            view.animate()
                .translationY(0f)
        }, exit = { view ->
            view.animate()
                .translationY(view.height.toFloat())
        })

        /**
         * The views fade from the old view to the new view.
         */
        val FADE = ViewTransitionUnidirectional(enter = { view ->
            view.alpha = 0f
            view.visibility = View.VISIBLE
            view.animate()
                .alpha(1f)
        }, exit = { view ->
            view.animate()
                .alpha(0f)
        })

        /**
         * No animation used.
         */
        val NONE = ViewTransitionUnidirectional(enter = { view ->
            view.animate()
                .translationX(0f)
        }, exit = { view ->
            view.animate()
                .translationX(0f)
        })
    }
}

/**
 * A combination of three [ViewTransitionUnidirectional]s to choose animations based on pushing and popping.
 */
data class ViewTransition(
    val push: ViewTransitionUnidirectional,
    val pop: ViewTransitionUnidirectional,
    val neutral: ViewTransitionUnidirectional,
) {
    companion object {
        /**
         * A normal push/pop transition style.
         */
        val PUSH_POP = ViewTransition(ViewTransitionUnidirectional.PUSH, ViewTransitionUnidirectional.POP, ViewTransitionUnidirectional.FADE)

        /**
         * Push and pop, but vertical.
         */
        val UP_DOWN = ViewTransition(ViewTransitionUnidirectional.UP, ViewTransitionUnidirectional.DOWN, ViewTransitionUnidirectional.FADE)

        /**
         * Only use fades.
         */
        val FADE_IN_OUT = ViewTransition(ViewTransitionUnidirectional.FADE, ViewTransitionUnidirectional.FADE, ViewTransitionUnidirectional.FADE)

        /**
         * Don't animate.
         */
        val NONE = ViewTransition(ViewTransitionUnidirectional.NONE, ViewTransitionUnidirectional.NONE, ViewTransitionUnidirectional.NONE)
    }
}