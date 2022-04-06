package com.lightningkite.rx.viewgenerators

import android.view.Gravity
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.transition.*

typealias TransitionGenerator = () -> Transition?

object TransitionGenerators {
    val decelerate = DecelerateInterpolator()
    val shared: () -> Transition? = {
        AutoTransition()
    }
    val none: () -> Transition? = { null }
    val fade: () -> Transition? = { Fade() }
    fun slide(direction: Int): () -> Transition? = {
        SlideFixed(direction).apply {
            interpolator = decelerate
            propagation = null
        }
    }
}

data class TransitionTriple(
    val enter: () -> Transition?,
    val exit: () -> Transition?,
    val shared: () -> Transition?
) {
    companion object {
        val PUSH = with(TransitionGenerators) { TransitionTriple(slide(Gravity.RIGHT), slide(Gravity.LEFT), shared) }
        val POP = with(TransitionGenerators) { TransitionTriple(slide(Gravity.LEFT), slide(Gravity.RIGHT), shared) }
        val PULL_DOWN =
            with(TransitionGenerators) { TransitionTriple(slide(Gravity.TOP), slide(Gravity.BOTTOM), shared) }
        val PULL_UP = with(TransitionGenerators) { TransitionTriple(slide(Gravity.BOTTOM), slide(Gravity.TOP), shared) }
        val FADE = with(TransitionGenerators) { TransitionTriple(fade, fade, shared) }
        val NONE = with(TransitionGenerators) { TransitionTriple(none, none, none) }
    }
}

/**
 * A combination of three [TransitionTriple]s to choose animations based on pushing and popping.
 */
data class StackTransition(
    val push: TransitionTriple,
    val pop: TransitionTriple,
    val neutral: TransitionTriple,
) {
    companion object {
        /**
         * A normal push/pop transition style.
         */
        val PUSH_POP = StackTransition(TransitionTriple.PUSH, TransitionTriple.POP, TransitionTriple.FADE)

        /**
         * Push and pop, but vertical.
         */
        val PULL_UP = StackTransition(TransitionTriple.PULL_UP, TransitionTriple.PULL_DOWN, TransitionTriple.FADE)

        /**
         * Only use fades.
         */
        val FADE_IN_OUT = StackTransition(TransitionTriple.FADE, TransitionTriple.FADE, TransitionTriple.FADE)

        /**
         * Don't animate.
         */
        val NONE = StackTransition(TransitionTriple.NONE, TransitionTriple.NONE, TransitionTriple.NONE)
    }
}