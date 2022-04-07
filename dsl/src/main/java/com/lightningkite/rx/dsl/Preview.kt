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
import com.lightningkite.rx.viewgenerators.ActivityAccess
import com.lightningkite.rx.viewgenerators.ViewGenerator
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

abstract class VgPreview(context: Context, val vg: ViewGenerator) : FrameLayout(context) {
    init {
        addView(vg.generate(PreviewActivityAccess(context)), FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    }
}
abstract class DslPreview(context: Context, val generate: (dsl: ViewDsl) -> View) : FrameLayout(context) {
    init {
        addView(generate(ViewDsl(context)), FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    }
}

class PreviewActivityAccess(override val context: Context): ActivityAccess {
    override val activity: Activity get() = throw NotImplementedError()
    override val savedInstanceState: Bundle? get() = null
    override val onResume: Observable<Unit> = Observable.never()
    override val onPause: Observable<Unit> = Observable.never()
    override val onSaveInstanceState: Observable<Bundle> = Observable.never()
    override val onLowMemory: Observable<Unit> = Observable.never()
    override val onDestroy: Observable<Unit> = Observable.never()
    override val onActivityResult: Observable<Triple<Int, Int, Intent?>> = Observable.never()
    override val onNewIntent: Observable<Intent> = Observable.never()

    override fun performBackPress() {}
    override fun prepareOnResult(presetCode: Int, onResult: (Int, Intent?) -> Unit): Int = 0
    override fun requestPermissions(permission: Array<String>): Single<Set<String>> = Single.just(permission.toSet())
    override fun requestPermission(permission: String): Single<Boolean> = Single.just(true)
}