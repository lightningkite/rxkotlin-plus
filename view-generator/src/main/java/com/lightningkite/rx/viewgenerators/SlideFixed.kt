package com.lightningkite.rx.viewgenerators

import android.animation.*
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.annotation.IntDef
import androidx.annotation.RestrictTo
import androidx.core.view.ViewCompat
import androidx.transition.*

/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
/**
 * This transition tracks changes to the visibility of target views in the
 * start and end scenes and moves views in or out from one of the edges of the
 * scene. Visibility is determined by both the
 * [View.setVisibility] state of the view as well as whether it
 * is parented in the current view hierarchy. Disappearing Views are
 * limited as described in [Visibility.onDisappear].
 */
class SlideFixed() : Visibility() {
    private var mSlideCalculator: SlideFixed.CalculateSlide = SlideFixed.Companion.sCalculateBottom
    private var mSlideEdge = Gravity.BOTTOM

    constructor(@GravityFlag slideEdge: Int):this() {
        this.slideEdge = slideEdge
    }

    /** @hide
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
    @IntDef(Gravity.LEFT, Gravity.TOP, Gravity.RIGHT, Gravity.BOTTOM, Gravity.START, Gravity.END)
    annotation class GravityFlag
    private interface CalculateSlide {
        /** Returns the translation value for view when it goes out of the scene  */
        fun getGoneX(sceneRoot: ViewGroup, view: View): Float = 0f

        /** Returns the translation value for view when it goes out of the scene  */
        fun getGoneY(sceneRoot: ViewGroup, view: View): Float = 0f
    }

    private abstract class CalculateSlideHorizontal : SlideFixed.CalculateSlide {
        override fun getGoneY(sceneRoot: ViewGroup, view: View): Float {
            return view.translationY
        }
    }

    private abstract class CalculateSlideVertical : SlideFixed.CalculateSlide {
        override fun getGoneX(sceneRoot: ViewGroup, view: View): Float {
            return view.translationX
        }
    }

    private fun captureValues(transitionValues: TransitionValues) {
        val view = transitionValues.view
        val position = IntArray(2)
        view.getLocationOnScreen(position)
        transitionValues.values[SlideFixed.Companion.PROPNAME_SCREEN_POSITION] = position
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        super.captureStartValues(transitionValues)
        captureValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        super.captureEndValues(transitionValues)
        captureValues(transitionValues)
    }
    /**
     * Returns the edge that Views appear and disappear from.
     *
     * @return the edge of the scene to use for Views appearing and disappearing. One of
     * [android.view.Gravity.LEFT], [android.view.Gravity.TOP],
     * [android.view.Gravity.RIGHT], [android.view.Gravity.BOTTOM],
     * [android.view.Gravity.START], [android.view.Gravity.END].
     */
    /**
     * Change the edge that Views appear and disappear from.
     *
     * @param slideEdge The edge of the scene to use for Views appearing and disappearing. One of
     * [android.view.Gravity.LEFT], [android.view.Gravity.TOP],
     * [android.view.Gravity.RIGHT], [android.view.Gravity.BOTTOM],
     * [android.view.Gravity.START], [android.view.Gravity.END].
     */
    @get:SlideFixed.GravityFlag
    var slideEdge: Int
        get() = mSlideEdge
        set(slideEdge) {
            mSlideCalculator = when (slideEdge) {
                Gravity.LEFT -> SlideFixed.Companion.sCalculateLeft
                Gravity.TOP -> SlideFixed.Companion.sCalculateTop
                Gravity.RIGHT -> SlideFixed.Companion.sCalculateRight
                Gravity.BOTTOM -> SlideFixed.Companion.sCalculateBottom
                Gravity.START -> SlideFixed.Companion.sCalculateStart
                Gravity.END -> SlideFixed.Companion.sCalculateEnd
                else -> throw IllegalArgumentException("Invalid slide direction")
            }
            mSlideEdge = slideEdge
            val propagation = SidePropagation()
            propagation.setSide(slideEdge)
            setPropagation(propagation)
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
        val position = endValues.values[SlideFixed.Companion.PROPNAME_SCREEN_POSITION] as IntArray
        val endX = view.translationX
        val endY = view.translationY
        val startX = mSlideCalculator.getGoneX(sceneRoot, view)
        val startY = mSlideCalculator.getGoneY(sceneRoot, view)
        return TranslationAnimationCreator
            .createAnimation(
                view, endValues, position[0], position[1],
                startX, startY, endX, endY, interpolator, this
            )!!
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
        val position = startValues.values[SlideFixed.Companion.PROPNAME_SCREEN_POSITION] as IntArray
        val startX = view.translationX
        val startY = view.translationY
        val endX = mSlideCalculator.getGoneX(sceneRoot, view)
        val endY = mSlideCalculator.getGoneY(sceneRoot, view)
        return TranslationAnimationCreator
            .createAnimation(
                view, startValues, position[0], position[1],
                startX, startY, endX, endY, interpolator, this
            )!!
    }

    companion object {
        private const val PROPNAME_SCREEN_POSITION = "android:slide:screenPosition"
        private val sCalculateLeft: SlideFixed.CalculateSlide = object : SlideFixed.CalculateSlideHorizontal() {
            override fun getGoneX(sceneRoot: ViewGroup, view: View): Float {
                return view.translationX - sceneRoot.width
            }
        }
        private val sCalculateStart: SlideFixed.CalculateSlide = object : SlideFixed.CalculateSlideHorizontal() {
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
        private val sCalculateTop: SlideFixed.CalculateSlide = object : SlideFixed.CalculateSlideVertical() {
            override fun getGoneY(sceneRoot: ViewGroup, view: View): Float {
                return view.translationY - sceneRoot.height
            }
        }
        private val sCalculateRight: SlideFixed.CalculateSlide = object : SlideFixed.CalculateSlideHorizontal() {
            override fun getGoneX(sceneRoot: ViewGroup, view: View): Float {
                return view.translationX + sceneRoot.width
            }
        }
        private val sCalculateEnd: SlideFixed.CalculateSlide = object : SlideFixed.CalculateSlideHorizontal() {
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
        private val sCalculateBottom: SlideFixed.CalculateSlide = object : SlideFixed.CalculateSlideVertical() {
            override fun getGoneY(sceneRoot: ViewGroup, view: View): Float {
                return view.translationY + sceneRoot.height
            }
        }
    }


    /**
     * This class is used by Slide and Explode to create an animator that goes from the start
     * position to the end position. It takes into account the canceled position so that it
     * will not blink out or shift suddenly when the transition is interrupted.
     */
    internal object TranslationAnimationCreator {
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
            addPauseListener(anim, listener)
            anim.interpolator = interpolator
            return anim
        }

        fun addPauseListener(
            animator: Animator,
            listener: AnimatorListenerAdapter
        ) {
            if (Build.VERSION.SDK_INT >= 19) {
                animator.addPauseListener(listener)
            }
        }

        private class TransitionPositionListener constructor(
            private val mMovingView: View, private val mViewInHierarchy: View,
            startX: Int, startY: Int, terminalX: Float, terminalY: Float
        ) :
            AnimatorListenerAdapter(), TransitionListener {
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

}