package com.lightningkite.rx.viewgenerators

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.Subject

/**
 * Request a permission on an activity, but as a [Single].
 * Permissions come from [android.Manifest.permission].
 */
fun ActivityAccess.requestPermission(permission: String): Single<Boolean> = Single.create { em ->
    this.requestPermission(permission) { em.onSuccess(it) }
}

/**
 * A result from an activity completing.
 */
data class ActivityResult(val code: Int, val data: Intent?)

/**
 * Creates a single that starts an intent and listens for the result.
 */
fun ActivityAccess.startIntentRx(
    intent: Intent,
    options: Bundle = android.os.Bundle()
): Single<ActivityResult> = Single.create { em ->
    activity.startActivityForResult(intent, prepareOnResult(onResult = { code, data ->
        em.onSuccess(ActivityResult(code, data))
    }), options)
}