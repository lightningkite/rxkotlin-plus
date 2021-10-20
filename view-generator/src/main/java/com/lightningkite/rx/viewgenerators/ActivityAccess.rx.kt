package com.lightningkite.rx.viewgenerators

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.Subject

fun ActivityAccess.requestPermission(permission: String): Single<Boolean> = Single.create { em ->
    this.requestPermission(permission) { em.onSuccess(it) }
}

data class ActivityResult(val code: Int, val data: Intent?)
fun ActivityAccess.startIntentRx(
    intent: Intent,
    options: Bundle = android.os.Bundle()
): Single<ActivityResult> = Single.create { em ->
    activity.startActivityForResult(intent, prepareOnResult(onResult = { code, data ->
        em.onSuccess(ActivityResult(code, data))
    }), options)
}