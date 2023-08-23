package com.lightningkite.rx.viewgenerators

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

/**
 * An interface for accessing activities in a decentralized way, where multiple listeners can listen
 * to event like [onPause], [onResume], etc, and most importantly, can use [prepareOnResult] to set
 * up lambdas to occur when a result code is received.
 */
interface ActivityAccess {
    /**
     * Gets the activity.
     */
    val activity: Activity
    /**
     * Gets the context.
     * If you need to preview a view generator using Android Studio, it can be useful to have this separate from [Activity].
     */
    val context: Context

    /**
     * Gets the [savedInstanceState] of the activity.
     */
    val savedInstanceState: Bundle?

    /**
     * Listens to the [onResume] callback of the activity.
     */
    val onResume: Observable<Unit>

    /**
     * Listens to the [onPause] callback of the activity.
     */
    val onPause: Observable<Unit>

    /**
     * Listens to the [onSaveInstanceState] callback of the activity.
     */
    val onSaveInstanceState: Observable<Bundle>

    /**
     * Listens to the [onLowMemory] callback of the activity.
     */
    val onLowMemory: Observable<Unit>

    /**
     * Listens to the [onDestroy] callback of the activity.
     */
    val onDestroy: Observable<Unit>

    /**
     * Listens to the [onActivityResult] callback of the activity.
     */
    val onActivityResult: Observable<Triple<Int, Int, Intent?>>

    /**
     * Listens to the [onNewIntent] callback of the activity.
     */
    val onNewIntent: Observable<Intent>

    /**
     * Performs a back button press on the activity.
     */
    fun performBackPress()

    /**
     * Returns a request code for starting an intent where the [onResult] function will be called upon completion.
     * Use this instead of overriding [Activity.onActivityResult].
     */
    fun prepareOnResult(
        presetCode: Int = (Math.random() * Short.MAX_VALUE).toInt(),
        onResult: (Int, Intent?) -> Unit = { _, _ -> }
    ): Int

    /**
     * Requests permissions, but with a callback response.
     */
    fun requestPermissions(permission: Array<String>): Single<Set<String>>

    /**
     * Requests a permission, but with a callback response.
     */
    fun requestPermission(permission: String): Single<Boolean>
}
