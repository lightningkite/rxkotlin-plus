package com.lightningkite.rx.viewgenerators

import android.animation.*
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.view.ViewCompat
import androidx.transition.*
import androidx.transition.Slide.GravityFlag


/**
 * A combination of three [Transition]s to choose animations based on pushing and popping.
 */
data class ViewTransition(
    val push: () -> Transition?,
    val pop: () -> Transition?,
    val neutral: () -> Transition?,
) {
    companion object {
        /**
         * A normal push/pop transition style.
         */
        val PUSH_POP = ViewTransition({ Push(Gravity.RIGHT, Gravity.LEFT) }, { Push(Gravity.LEFT, Gravity.RIGHT) }, { Fade() })

        /**
         * Push and pop, but vertical.
         */
        val UP_DOWN = ViewTransition({ Push(Gravity.BOTTOM, Gravity.TOP) }, { Push(Gravity.TOP, Gravity.BOTTOM) }, { Fade() })

        /**
         * Only use fades.
         */
        val FADE_IN_OUT = ViewTransition({ Fade() },
            { Fade() }, { Fade() })

        /**
         * Don't animate.
         */
        val NONE = ViewTransition({ null }, { null }, { null })
    }
}