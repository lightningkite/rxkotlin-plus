package com.lightningkite.rx.viewgenerators.fcm

import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.google.firebase.messaging.FirebaseMessaging
import com.lightningkite.rx.ValueSubject
import com.lightningkite.rx.android.into
import com.lightningkite.rx.android.staticApplicationContext
import com.lightningkite.rx.forever
import com.lightningkite.rx.optional
import com.lightningkite.rx.viewgenerators.ActivityAccess
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.util.*

object Notifications {
    var notificationToken: ValueSubject<Optional<String>> = ValueSubject(Optional.empty())

    var permissionFlag = false
    fun configure(dependency: ActivityAccess, customization: (NotificationManager) -> Unit = {}) {
        if (Build.VERSION.SDK_INT >= 33 && !permissionFlag) {
            dependency.requestPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                .subscribeBy {
                    if (it) {
                        permissionFlag = true
                        configure(dependency, customization)
                    }
                }
                .forever()
        }

        if (permissionFlag) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                it.result?.let { notificationToken.value = it.optional }
            }

            // This section is meant to silence the current notifications when the app runs.
            val notificationManager =
                staticApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            customization(notificationManager)

            notificationManager.cancelAll()
        }
    }
}