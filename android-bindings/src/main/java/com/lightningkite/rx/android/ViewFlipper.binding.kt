@file:Suppress("NAME_SHADOWING")

package com.lightningkite.rx.android

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.AlphaAnimation
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.ViewFlipper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.kotlin.addTo
import java.util.*

/**
 * Flips between views 0 and 1 in the ViewFlipper based on the value of this.
 * If the ViewFlipper only has one view a ProgressBar will automatically
 * be created and displayed as view 1.
 *
 * Example:
 * val value = ValueSubject<Boolean>(false)
 * value.showLoading(viewFlipper)
 */
@Deprecated("Simplified to use subscribeAutoDispose", ReplaceWith("this.subscribeAutoDispose(viewFlipper, ViewFlipper::showLoading)", "com.lightningkite.rx.android.showLoading", "com.lightningkite.rx.android.subscribeAutoDispose"))
fun <SOURCE: Observable<Boolean>> SOURCE.showLoading(viewFlipper: ViewFlipper, color: ColorResource? = null): SOURCE {
    defaults(viewFlipper, color)
    observeOn(RequireMainThread).subscribeBy { it ->
        viewFlipper.displayedChild = if (it) 1 else 0
    }.addTo(viewFlipper.removed)
    return this
}

/**
 * Shows the progress indicator if set to true.
 *
 * Used best with `subscribeAutoDispose` like so:
 *
 * isWorking.subscribeAutoDispose(viewFlipper, ViewFlipper::showLoading)
 */
var ViewFlipper.showLoading: Boolean
    get() = displayedChild == 1
    set(value) {
        defaults(this, null)
        displayedChild = if (value) 1 else 0
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


/**
 * Flips between views 1 and 0 in the ViewFlipper when this is subscribed to and on termination respectively
 * If the ViewFlipper only has one view a ProgressBar will automatically
 * be created and displayed as view 1.
 *
 * Example:
 * val value = Single.just<Boolean>(false)
 * value.showLoading(viewFlipper)
 */
fun <T: Any> Single<T>.showLoading(viewFlipper: ViewFlipper, color: ColorResource? = null): Single<T> {
    defaults(viewFlipper, color)
    return this.doOnSubscribe { viewFlipper.loadCount++ }.doOnTerminate { viewFlipper.loadCount-- }
}

/**
 * Flips between views 1 and 0 in the ViewFlipper when this is subscribed to and on termination respectively
 * If the ViewFlipper only has one view a ProgressBar will automatically
 * be created and displayed as view 1.
 *
 * Example:
 * val value: Maybe<Boolean> ...
 * value.showLoading(viewFlipper)
 */
fun <T: Any> Maybe<T>.showLoading(viewFlipper: ViewFlipper, color: ColorResource? = null): Maybe<T> {
    defaults(viewFlipper, color)
    return this.doOnSubscribe { viewFlipper.loadCount++ }.doOnTerminate { viewFlipper.loadCount-- }
}
