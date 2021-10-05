package com.lightningkite.rxkotlinproperty.viewgenerators

import android.view.View
import android.view.ViewPropertyAnimator

typealias TransitionAnimation = (View) -> ViewPropertyAnimator

enum class ViewTransition(
    val enterPush: TransitionAnimation,
    val exitPush: TransitionAnimation,
    val enterPop: TransitionAnimation,
    val exitPop: TransitionAnimation
) {
    PUSH_POP(enterPush = { view ->
        view.translationX = (view.parent as View).width.toFloat()
        view.animate()
            .translationX(0f)
    }, exitPush = { view ->
        view.animate()
            .translationX(-1f * view.width.toFloat())
    }, enterPop = { view ->
        view.translationX = -1f * (view.parent as View).width.toFloat()
        view.animate()
            .translationX(0f)
    }, exitPop = { view ->
        view.animate()
            .translationX(view.width.toFloat())
    }),

    UP_DOWN(enterPush = { view ->
        view.translationY = (view.parent as View).height.toFloat()
        view.animate()
            .translationY(0f)
    }, exitPush = { view ->
        view.animate()
            .translationY(-1f * view.height.toFloat())
    }, enterPop = { view ->
        view.translationY = -1f * (view.parent as View).height.toFloat()
        view.animate()
            .translationY(0f)
    }, exitPop = { view ->
        view.animate()
            .translationY(view.height.toFloat())
    }),

    FADE_IN_OUT(enterPush = { view ->
        view.alpha = 0f
        view.visibility = View.VISIBLE
        view.animate()
            .alpha(1f)
    }, exitPush = { view ->
        view.animate()
            .alpha(0f)
    }, enterPop = { view ->
        view.alpha = 0f
        view.animate()
            .alpha(1f)
    }, exitPop = { view ->
        view.animate()
            .alpha(0f)
    }),

    NONE(enterPush = { view ->
        view.animate()
            .translationX(0f)
    }, exitPush = { view ->
        view.animate()
            .translationX(0f)
    }, enterPop = { view ->
        view.animate()
            .translationX(0f)
    }, exitPop = { view ->
        view.animate()
            .translationX(0f)
    }
    )
}