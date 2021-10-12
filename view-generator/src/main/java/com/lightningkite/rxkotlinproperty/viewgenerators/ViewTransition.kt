package com.lightningkite.rxkotlinproperty.viewgenerators

import android.view.View
import android.view.ViewPropertyAnimator

typealias TransitionAnimation = (View) -> ViewPropertyAnimator

enum class ViewTransitionUnidirectional(
    val enter: TransitionAnimation,
    val exit: TransitionAnimation
) {
    PUSH(enter = { view ->
        view.translationX = (view.parent as View).width.toFloat()
        view.animate()
            .translationX(0f)
    }, exit = { view ->
        view.animate()
            .translationX(-1f * view.width.toFloat())
    }),
    POP(enter = { view ->
        view.translationX = -1f * (view.parent as View).width.toFloat()
        view.animate()
            .translationX(0f)
    }, exit = { view ->
        view.animate()
            .translationX(view.width.toFloat())
    }),
    UP(enter = { view ->
        view.translationY = (view.parent as View).height.toFloat()
        view.animate()
            .translationY(0f)
    }, exit = { view ->
        view.animate()
            .translationY(-1f * view.height.toFloat())
    }),
    DOWN(enter = { view ->
        view.translationY = -1f * (view.parent as View).height.toFloat()
        view.animate()
            .translationY(0f)
    }, exit = { view ->
        view.animate()
            .translationY(view.height.toFloat())
    }),
    FADE(enter = { view ->
        view.alpha = 0f
        view.visibility = View.VISIBLE
        view.animate()
            .alpha(1f)
    }, exit = { view ->
        view.animate()
            .alpha(0f)
    }),
    NONE(enter = { view ->
        view.animate()
            .translationX(0f)
    }, exit = { view ->
        view.animate()
            .translationX(0f)
    })
}

enum class ViewTransition(
    val push: ViewTransitionUnidirectional,
    val pop: ViewTransitionUnidirectional,
    val neutral: ViewTransitionUnidirectional,
) {
    PUSH_POP(ViewTransitionUnidirectional.PUSH, ViewTransitionUnidirectional.POP, ViewTransitionUnidirectional.FADE),
    UP_DOWN(ViewTransitionUnidirectional.UP, ViewTransitionUnidirectional.DOWN, ViewTransitionUnidirectional.FADE),
    FADE_IN_OUT(ViewTransitionUnidirectional.FADE, ViewTransitionUnidirectional.FADE, ViewTransitionUnidirectional.FADE),
    NONE(ViewTransitionUnidirectional.NONE, ViewTransitionUnidirectional.NONE, ViewTransitionUnidirectional.NONE),
}