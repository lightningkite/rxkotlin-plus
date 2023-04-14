package com.lightningkite.rx.dsl

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.observableOfNever
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.singleOf
import com.lightningkite.rx.viewgenerators.ActivityAccess
import com.lightningkite.rx.viewgenerators.ViewGenerator

@RxKotlinViewDsl abstract class VgPreview(context: Context, attrs: AttributeSet? = null, val vg: ViewGenerator) : FrameLayout(context, attrs) {
    init {
        addView(vg.generate(PreviewActivityAccess(context)), FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    }
}
@RxKotlinViewDsl abstract class DslPreview(context: Context, attrs: AttributeSet? = null, val generate: (dsl: ViewDsl) -> View) : FrameLayout(context, attrs) {
    init {
        addView(generate(ViewDsl(context)), FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    }
}

private class PreviewActivityAccess(override val context: Context): ActivityAccess {
    override val activity: Activity get() = throw NotImplementedError()
    override val savedInstanceState: Bundle? get() = null
    override val onResume: Observable<Unit> = observableOfNever()
    override val onPause: Observable<Unit> = observableOfNever()
    override val onSaveInstanceState: Observable<Bundle> = observableOfNever()
    override val onLowMemory: Observable<Unit> = observableOfNever()
    override val onDestroy: Observable<Unit> = observableOfNever()
    override val onActivityResult: Observable<Triple<Int, Int, Intent?>> = observableOfNever()
    override val onNewIntent: Observable<Intent> = observableOfNever()

    override fun performBackPress() {}
    override fun prepareOnResult(presetCode: Int, onResult: (Int, Intent?) -> Unit): Int = 0
    override fun requestPermissions(permission: Array<String>): Single<Set<String>> = singleOf(permission.toSet())
    override fun requestPermission(permission: String): Single<Boolean> = singleOf(true)
}