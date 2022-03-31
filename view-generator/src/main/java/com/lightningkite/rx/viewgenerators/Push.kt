package com.lightningkite.rx.viewgenerators

import android.animation.*
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.view.ViewCompat
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionValues
import androidx.transition.Visibility


class Push(@Slide.GravityFlag val enter: Int, @Slide.GravityFlag val exit: Int): Visibility() {

    private val enterSlideCalculator: CalculateSlide = when (enter) {
        Gravity.LEFT -> sCalculateLeft
        Gravity.TOP -> sCalculateTop
        Gravity.RIGHT -> sCalculateRight
        Gravity.BOTTOM -> sCalculateBottom
        Gravity.START -> sCalculateStart
        Gravity.END -> sCalculateEnd
        else -> throw IllegalArgumentException("Invalid slide direction")
    }
    private val exitSlideCalculator: CalculateSlide = when (exit) {
        Gravity.LEFT -> sCalculateLeft
        Gravity.TOP -> sCalculateTop
        Gravity.RIGHT -> sCalculateRight
        Gravity.BOTTOM -> sCalculateBottom
        Gravity.START -> sCalculateStart
        Gravity.END -> sCalculateEnd
        else -> throw IllegalArgumentException("Invalid slide direction")
    }

    companion object {
        private val PROPNAME_SCREEN_POSITION = "android:push:screenPosition"

        private val sDecelerate: TimeInterpolator = DecelerateInterpolator()
        private val sAccelerate: TimeInterpolator = AccelerateInterpolator()

        private val sCalculateLeft: CalculateSlide = object : CalculateSlideHorizontal() {
            override fun getGoneX(sceneRoot: ViewGroup, view: View): Float {
                return view.translationX - sceneRoot.width
            }
        }

        private val sCalculateStart: CalculateSlide = object : CalculateSlideHorizontal() {
            override fun getGoneX(sceneRoot: ViewGroup, view: View): Float {
                val isRtl = (ViewCompat.getLayoutDirection(sceneRoot)
                        == ViewCompat.LAYOUT_DIRECTION_RTL)
                val x: Float
                x = if (isRtl) {
                    view.translationX + sceneRoot.width
                } else {
                    view.translationX - sceneRoot.width
                }
                return x
            }
        }

        private val sCalculateTop: CalculateSlide = object : CalculateSlideVertical() {
            override fun getGoneY(sceneRoot: ViewGroup, view: View): Float {
                return view.translationY - sceneRoot.height
            }
        }

        private val sCalculateRight: CalculateSlide = object : CalculateSlideHorizontal() {
            override fun getGoneX(sceneRoot: ViewGroup, view: View): Float {
                return view.translationX + sceneRoot.width
            }
        }

        private val sCalculateEnd: CalculateSlide = object : CalculateSlideHorizontal() {
            override fun getGoneX(sceneRoot: ViewGroup, view: View): Float {
                val isRtl = (ViewCompat.getLayoutDirection(sceneRoot)
                        == ViewCompat.LAYOUT_DIRECTION_RTL)
                val x: Float
                x = if (isRtl) {
                    view.translationX - sceneRoot.width
                } else {
                    view.translationX + sceneRoot.width
                }
                return x
            }
        }

        private val sCalculateBottom: CalculateSlide = object : CalculateSlideVertical() {
            override fun getGoneY(sceneRoot: ViewGroup, view: View): Float {
                return view.translationY + sceneRoot.height
            }
        }
    }

    private interface CalculateSlide {
        /** Returns the translation value for view when it goes out of the scene  */
        fun getGoneX(sceneRoot: ViewGroup, view: View): Float = 0f

        /** Returns the translation value for view when it goes out of the scene  */
        fun getGoneY(sceneRoot: ViewGroup, view: View): Float = 0f
    }

    private abstract class CalculateSlideHorizontal : CalculateSlide {
        override fun getGoneY(sceneRoot: ViewGroup, view: View): Float {
            return view.translationY
        }
    }

    private abstract class CalculateSlideVertical : CalculateSlide {
        override fun getGoneX(sceneRoot: ViewGroup, view: View): Float {
            return view.translationX
        }
    }


    private fun captureValues(transitionValues: TransitionValues) {
        val view = transitionValues.view
        val position = IntArray(2)
        view.getLocationOnScreen(position)
        transitionValues.values[PROPNAME_SCREEN_POSITION] = position
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        super.captureStartValues(transitionValues)
        captureValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        super.captureEndValues(transitionValues)
        captureValues(transitionValues)
    }

    override fun onAppear(
        sceneRoot: ViewGroup,
        view: View,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        if (endValues == null) {
            return null
        }
        val position = endValues.values[PROPNAME_SCREEN_POSITION] as IntArray
        val endX = view.translationX
        val endY = view.translationY
        val startX: Float = enterSlideCalculator.getGoneX(sceneRoot, view)
        val startY: Float = enterSlideCalculator.getGoneY(sceneRoot, view)
        return TranslationAnimationCreator
            .createAnimation(
                view, endValues, position[0], position[1],
                startX, startY, endX, endY, sDecelerate, this
            )
    }

    override fun onDisappear(
        sceneRoot: ViewGroup,
        view: View,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        if (startValues == null) {
            return null
        }
        val position = startValues.values[PROPNAME_SCREEN_POSITION] as IntArray
        val startX = view.translationX
        val startY = view.translationY
        val endX: Float = exitSlideCalculator.getGoneX(sceneRoot, view)
        val endY: Float = exitSlideCalculator.getGoneY(sceneRoot, view)
        return TranslationAnimationCreator
            .createAnimation(
                view, startValues, position[0], position[1],
                startX, startY, endX, endY, sDecelerate, this
            )
    }
}

private object TranslationAnimationCreator {
    /**
     * Creates an animator that can be used for x and/or y translations. When interrupted,
     * it sets a tag to keep track of the position so that it may be continued from position.
     *
     * @param view         The view being moved. This may be in the overlay for onDisappear.
     * @param values       The values containing the view in the view hierarchy.
     * @param viewPosX     The x screen coordinate of view
     * @param viewPosY     The y screen coordinate of view
     * @param startX       The start translation x of view
     * @param startY       The start translation y of view
     * @param endX         The end translation x of view
     * @param endY         The end translation y of view
     * @param interpolator The interpolator to use with this animator.
     * @return An animator that moves from (startX, startY) to (endX, endY) unless there was
     * a previous interruption, in which case it moves from the current position to (endX, endY).
     */
    fun createAnimation(
        view: View, values: TransitionValues,
        viewPosX: Int, viewPosY: Int, startX: Float, startY: Float, endX: Float, endY: Float,
        interpolator: TimeInterpolator?, transition: Transition
    ): Animator? {
        var startX = startX
        var startY = startY
        val terminalX = view.translationX
        val terminalY = view.translationY
        val startPosition = values.view.getTag(R.id.transition_position) as? IntArray
        if (startPosition != null) {
            startX = startPosition[0] - viewPosX + terminalX
            startY = startPosition[1] - viewPosY + terminalY
        }
        // Initial position is at translation startX, startY, so position is offset by that amount
        val startPosX = viewPosX + Math.round(startX - terminalX)
        val startPosY = viewPosY + Math.round(startY - terminalY)
        view.translationX = startX
        view.translationY = startY
        if (startX == endX && startY == endY) {
            return null
        }
        val anim = ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat(View.TRANSLATION_X, startX, endX),
            PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, startY, endY)
        )
        val listener = TransitionPositionListener(
            view, values.view,
            startPosX, startPosY, terminalX, terminalY
        )
        transition.addListener(listener)
        anim.addListener(listener)
        AnimatorUtils.addPauseListener(anim, listener)
        anim.interpolator = interpolator
        return anim
    }

    private class TransitionPositionListener internal constructor(
        private val mMovingView: View, private val mViewInHierarchy: View,
        startX: Int, startY: Int, terminalX: Float, terminalY: Float
    ) :
        AnimatorListenerAdapter(), Transition.TransitionListener {
        private val mStartX: Int
        private val mStartY: Int
        private var mTransitionPosition: IntArray?
        private var mPausedX = 0f
        private var mPausedY = 0f
        private val mTerminalX: Float
        private val mTerminalY: Float

        init {
            mStartX = startX - Math.round(mMovingView.translationX)
            mStartY = startY - Math.round(mMovingView.translationY)
            mTerminalX = terminalX
            mTerminalY = terminalY
            mTransitionPosition = mViewInHierarchy.getTag(R.id.transition_position) as? IntArray
            if (mTransitionPosition != null) {
                mViewInHierarchy.setTag(R.id.transition_position, null)
            }
        }

        override fun onAnimationCancel(animation: Animator) {
            if (mTransitionPosition == null) {
                mTransitionPosition = IntArray(2)
            }
            mTransitionPosition!![0] = Math.round(mStartX + mMovingView.translationX)
            mTransitionPosition!![1] = Math.round(mStartY + mMovingView.translationY)
            mViewInHierarchy.setTag(R.id.transition_position, mTransitionPosition)
        }

        override fun onAnimationPause(animator: Animator) {
            mPausedX = mMovingView.translationX
            mPausedY = mMovingView.translationY
            mMovingView.translationX = mTerminalX
            mMovingView.translationY = mTerminalY
        }

        override fun onAnimationResume(animator: Animator) {
            mMovingView.translationX = mPausedX
            mMovingView.translationY = mPausedY
        }

        override fun onTransitionStart(transition: Transition) {}
        override fun onTransitionEnd(transition: Transition) {
            mMovingView.translationX = mTerminalX
            mMovingView.translationY = mTerminalY
            transition.removeListener(this)
        }

        override fun onTransitionCancel(transition: Transition) {}
        override fun onTransitionPause(transition: Transition) {}
        override fun onTransitionResume(transition: Transition) {}
    }
}

private object AnimatorUtils {
    fun addPauseListener(
        animator: Animator,
        listener: AnimatorListenerAdapter
    ) {
        if (Build.VERSION.SDK_INT >= 19) {
            animator.addPauseListener(listener)
        }
    }

    fun pause(animator: Animator) {
        if (Build.VERSION.SDK_INT >= 19) {
            animator.pause()
        } else {
            val listeners = animator.listeners
            if (listeners != null) {
                var i = 0
                val size = listeners.size
                while (i < size) {
                    val listener = listeners[i]
                    if (listener is AnimatorPauseListenerCompat) {
                        (listener as AnimatorPauseListenerCompat).onAnimationPause(animator)
                    }
                    i++
                }
            }
        }
    }

    fun resume(animator: Animator) {
        if (Build.VERSION.SDK_INT >= 19) {
            animator.resume()
        } else {
            val listeners = animator.listeners
            if (listeners != null) {
                var i = 0
                val size = listeners.size
                while (i < size) {
                    val listener = listeners[i]
                    if (listener is AnimatorPauseListenerCompat) {
                        (listener as AnimatorPauseListenerCompat).onAnimationResume(animator)
                    }
                    i++
                }
            }
        }
    }

    /**
     * Listeners can implement this interface in addition to the platform AnimatorPauseListener to
     * make them compatible with API level 18 and below. Animators will not be paused or resumed,
     * but the callbacks here are invoked.
     */
    internal interface AnimatorPauseListenerCompat {
        fun onAnimationPause(animation: Animator?)
        fun onAnimationResume(animation: Animator?)
    }
}