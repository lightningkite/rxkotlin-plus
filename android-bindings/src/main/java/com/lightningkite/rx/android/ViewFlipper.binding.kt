@file:Suppress("NAME_SHADOWING")

package com.lightningkite.rx.android

import android.graphics.LightingColorFilter
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

/**
 *
 * bindLoading will flip the view between any built in views, and the native android loading animation.
 * If the value in loading is false it will display whatever view it normally holds.
 * If the value in loading is true it will hide any views it holds and display the loading animation.
 * Color will set the color of the loading animation to whatever resource is provided.
 *
 */

fun <SOURCE: Observable<Boolean>> SOURCE.bindLoading(view: ViewFlipper, color: ColorResource? = null): SOURCE {
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
        color?.let{ color ->
            //TODO: Make this actually show the proper color. Currently it only makes it white.
            val colorValue = view.resources.getColor(color)
            spinner.indeterminateDrawable.colorFilter = LightingColorFilter( 0xFFFFFFFF.toInt() - colorValue, colorValue)
        }
        view.addView(spinner, 1, FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, Gravity.CENTER))
    }
    subscribeBy { it ->
        view.displayedChild = if (it) 1 else 0
    }.addTo(view.removed)
    return this
}

fun <T: Any> Single<T>.bindLoading(view: ViewFlipper, color: ColorResource? = null): Single<T> {
    val sub = BehaviorSubject.createDefault(false)
    sub.bindLoading(view, color)
    return this.doOnSubscribe { sub.onNext(true) }.doOnTerminate { sub.onNext(false); sub.onComplete() }
}
fun <T: Any> Maybe<T>.bindLoading(view: ViewFlipper, color: ColorResource? = null): Maybe<T> {
    val sub = BehaviorSubject.createDefault(false)
    sub.bindLoading(view, color)
    return this.doOnSubscribe { sub.onNext(true) }.doOnTerminate { sub.onNext(false); sub.onComplete() }
}
