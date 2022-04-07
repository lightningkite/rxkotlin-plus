package com.lightningkite.rx.viewgenerators.transition

import android.content.res.Resources
import android.view.Gravity
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
    val growFade: () -> Transition? = {
        TransitionSet().apply {
            ordering = TransitionSet.ORDERING_TOGETHER
            addTransition(Scale(0.75f))
            addTransition(Offset(0f, -50f * Resources.getSystem().displayMetrics.density))
            addTransition(Fade())
        }
    }
    val shrinkFade: () -> Transition? = {
        TransitionSet().apply {
            ordering = TransitionSet.ORDERING_TOGETHER
            addTransition(Scale(1.33f))
            addTransition(Offset(0f, -50f * Resources.getSystem().displayMetrics.density))
            addTransition(Fade())
        }
    }
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
        val GROW_FADE = with(TransitionGenerators) { TransitionTriple(growFade, growFade, shared) }
        val SHRINK_FADE = with(TransitionGenerators) { TransitionTriple(growFade, growFade, shared) }
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
         * A modal-y transition, where the view fades in and grows.
         */
        val MODAL = StackTransition(TransitionTriple.GROW_FADE, TransitionTriple.SHRINK_FADE, TransitionTriple.FADE)

        /**
         * Don't animate.
         */
        val NONE = StackTransition(TransitionTriple.NONE, TransitionTriple.NONE, TransitionTriple.NONE)
    }
}

