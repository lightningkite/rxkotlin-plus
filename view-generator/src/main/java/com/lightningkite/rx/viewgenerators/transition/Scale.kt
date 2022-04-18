package com.lightningkite.rx.viewgenerators.transition

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import android.view.ViewGroup
import androidx.transition.TransitionValues
import androidx.transition.Visibility

class Scale(val times: Float): Visibility() {
    companion object {
        private const val PROPNAME_SCALEX = "android:grow:scaleX"
        private const val PROPNAME_SCALEY = "android:grow:scaleY"
    }

    private fun captureValues(transitionValues: TransitionValues) {
        val view = transitionValues.view
        transitionValues.values[PROPNAME_SCALEX] = view.scaleX
        transitionValues.values[PROPNAME_SCALEY] = view.scaleY
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
        val endX = endValues.values[PROPNAME_SCALEX] as Float
        val endY = endValues.values[PROPNAME_SCALEY] as Float
        return ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat(View.SCALE_X, endX / times, endX),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, endY / times, endY)
        )
    }

    override fun onDisappear(
        sceneRoot: ViewGroup,
        view: View,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        if(startValues == null) return null
        val startX = startValues.values[PROPNAME_SCALEX] as Float
        val startY = startValues.values[PROPNAME_SCALEY] as Float
        return ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat(View.SCALE_X, startX, startX * times),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, startY, startY * times)
        )
    }
}