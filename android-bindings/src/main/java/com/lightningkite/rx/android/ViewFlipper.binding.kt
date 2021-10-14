@file:Suppress("NAME_SHADOWING")

package com.lightningkite.rx.android

import android.graphics.LightingColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.AlphaAnimation
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.ViewFlipper
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.*

/**
 *
 * bindLoading will flip the view between any built in views, and the native android loading animation.
 * If the value in loading is false it will display whatever view it normally holds.
 * If the value in loading is true it will hide any views it holds and display the loading animation.
 * Color will set the color of the loading animation to whatever resource is provided.
 *
 */

fun <SOURCE: Observable<Boolean>> SOURCE.showLoading(view: ViewFlipper, color: ColorResource? = null): SOURCE {
    defaults(view, color)
    subscribeBy { it ->
        view.displayedChild = if (it) 1 else 0
    }.addTo(view.removed)
    return this
}

private fun defaults(view: ViewFlipper, color: ColorResource?) {
    if (view.inAnimation == null)
        view.inAnimation = AlphaAnimation(0f, 1f).apply {
            duration = 250
            interpolator = LinearInterpolator()
        }
    if (view.outAnimation == null)
        view.outAnimation = AlphaAnimation(1f, 0f).apply {
            duration = 250
            interpolator = LinearInterpolator()
        }
    if (view.childCount == 1) {
        val spinner = ProgressBar(view.context)
        color?.let { color ->
            spinner.indeterminateDrawable.colorFilter = PorterDuffColorFilter(view.resources.getColor(color), PorterDuff.Mode.MULTIPLY)
        }
        view.addView(spinner, 1, FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.CENTER))
    }
}

private val ViewFlipper_loadCount = WeakHashMap<ViewFlipper, Int>()
var ViewFlipper.loadCount: Int
    get() = ViewFlipper_loadCount[this] ?: 0
    set(value) {
        ViewFlipper_loadCount[this] = value
        displayedChild = if (value > 0) 1 else 0
    }

fun <T: Any> Single<T>.showLoading(view: ViewFlipper, color: ColorResource? = null): Single<T> {
    return this.doOnSubscribe { view.loadCount++ }.doOnTerminate { view.loadCount-- }
}
fun <T: Any> Maybe<T>.showLoading(view: ViewFlipper, color: ColorResource? = null): Maybe<T> {
    return this.doOnSubscribe { view.loadCount++ }.doOnTerminate { view.loadCount-- }
}
