package com.lightningkite.rx.viewgenerators.fcm

import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.badoo.reaktive.single.subscribe
import com.badoo.reaktive.subject.behavior.BehaviorSubject
import com.google.firebase.messaging.FirebaseMessaging
import com.lightningkite.rx.android.staticApplicationContext
import com.lightningkite.rx.viewgenerators.ActivityAccess
import java.util.*

object Notifications {
    val notificationToken: BehaviorSubject<String?> = BehaviorSubject(null)

    fun configure(dependency:ActivityAccess) {
        var permissionFlag = true
        if(Build.VERSION.SDK_INT >= 33){
            permissionFlag = false
            dependency.requestPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                .subscribe {
                    if(it){
                        permissionFlag = true
                        configure(dependency )
                    }
                }
        }

        if(permissionFlag){
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                it.result?.let { notificationToken.onNext(it) }
            }

            // This section is meant to silence the current notifications when the app runs.
            val notificationManager =
                staticApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val meta = staticApplicationContext.packageManager.getApplicationInfo(
                staticApplicationContext.packageName,
                PackageManager.GET_META_DATA
            ).metaData

            notificationManager.cancelAll()
        }
    }
}