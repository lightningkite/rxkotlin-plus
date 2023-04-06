package com.lightningkite.rx.viewgenerators

import android.content.Intent
import android.os.Bundle
import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.asCompletable
import com.badoo.reaktive.single.map
import com.badoo.reaktive.single.single

/**
 * Request a permission on an activity, but as a [Single].
 * Permissions come from [android.Manifest.permission].
 */
fun ActivityAccess.requirePermission(permission: String): Completable = requestPermission(permission)
    .map {
        if (!it) throw SecurityException("Permission $permission not granted")
        it
    }
    .asCompletable()

/**
 * Request a permission on an activity, but as a [Single].
 * Permissions come from [android.Manifest.permission].
 */
fun ActivityAccess.requirePermissions(permissions: Array<String>): Completable = requestPermissions(permissions)
    .map {
        val ungranted = permissions.toSet() - it
        if (ungranted.isNotEmpty()) throw SecurityException("Permissions ${ungranted.joinToString()} not granted")
        it
    }
    .asCompletable()

/**
 * A result from an activity completing.
 */
data class ActivityResult(val code: Int, val data: Intent?)

/**
 * Creates a single that starts an intent and listens for the result.
 */
fun ActivityAccess.startIntent(
    intent: Intent,
    options: Bundle = android.os.Bundle()
) = activity.startActivity(intent, options)

/**
 * Creates a single that starts an intent and listens for the result.
 */
fun ActivityAccess.startActivityForResult(
    intent: Intent,
    options: Bundle = android.os.Bundle()
): Single<ActivityResult> = single { em ->
    activity.startActivityForResult(intent, prepareOnResult(onResult = { code, data ->
        em.onSuccess(ActivityResult(code, data))
    }), options)
}