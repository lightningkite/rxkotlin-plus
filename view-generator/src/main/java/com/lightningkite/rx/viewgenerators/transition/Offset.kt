package com.lightningkite.rx.viewgenerators.transition

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import android.view.ViewGroup
import androidx.transition.TransitionValues
import androidx.transition.Visibility

class Offset(val x: Float, val y: Float): Visibility() {
    companion object {
        private const val PROPNAME_TRANSLATIONX = "android:translation:scaleX"
        private const val PROPNAME_TRANSLATIONY = "android:translation:scaleY"
    }

    private fun captureValues(transitionValues: TransitionValues) {
        val view = transitionValues.view
        transitionValues.values[PROPNAME_TRANSLATIONX] = view.translationX
        transitionValues.values[PROPNAME_TRANSLATIONY] = view.translationY
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
        if(endValues == null) return null
        val endX = endValues.values[PROPNAME_TRANSLATIONX] as Float
        val endY = endValues.values[PROPNAME_TRANSLATIONY] as Float
        return ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat(View.TRANSLATION_X, endX - x, endX),
            PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, endY - y, endY)
        )
    }

    override fun onDisappear(
        sceneRoot: ViewGroup,
        view: View,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        if(startValues == null) return null
        val startX = startValues.values[PROPNAME_TRANSLATIONX] as Float
        val startY = startValues.values[PROPNAME_TRANSLATIONY] as Float
        return ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat(View.TRANSLATION_X, startX, startX + x),
            PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, startY, startY + y)
        )
    }
}