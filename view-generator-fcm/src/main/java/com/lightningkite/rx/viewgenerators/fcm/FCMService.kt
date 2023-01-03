package com.lightningkite.rx.viewgenerators.fcm


import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.annotation.AnyRes
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lightningkite.rx.optional
import com.lightningkite.rx.viewgenerators.ViewGenerator
import java.util.*

abstract class VGFCMService : FirebaseMessagingService() {
    abstract val icon: Int

    companion object {
        var main: ViewGenerator? = null
        const val FROM_NOTIFICATION: String = "fromNotification"
    }

    override fun onMessageReceived(message: RemoteMessage) {
        if ((main as? ForegroundNotificationHandler)?.handleNotificationInForeground(message.data) == ForegroundNotificationHandlerResult.SuppressNotification) return
        try {
            message.notification?.let { notification ->
                val meta = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA).metaData
                val builder =
                    NotificationCompat.Builder(
                        this,
                        notification.channelId
                            ?: meta.getString(
                                "com.google.firebase.messaging.default_notification_channel_id"
                            ) ?: "default"
                    )
                builder.setSmallIcon(icon)
                meta.getInt("com.google.firebase.messaging.default_notification_icon", 0)
                    .takeUnless { it == 0 }
                    ?.let { builder.setSmallIcon(it) }
                meta.getInt("com.google.firebase.messaging.default_notification_color", 0)
                    .takeUnless { it == 0 }
                    ?.let { builder.setColor(it) }
                notification.title?.let { it -> builder.setContentTitle(it) }
                notification.body?.let { it -> builder.setContentText(it) }

                builder.setContentIntent(
                    PendingIntent.getActivity(
                        this,
                        0,
                        packageManager.getLaunchIntentForPackage(packageName)!!.apply {
                            this.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            for ((key, value) in message.data) {
                                this.putExtra(key, value)
                            }
                            this.putExtra(FROM_NOTIFICATION, true)
                        },
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_ONE_SHOT
                    )
                )
                notification.sound?.let { Uri.parse(it) }?.let { builder.setSound(it) }
                notification.vibrateTimings?.let { builder.setVibrate(it) }
                notification.notificationPriority?.let { builder.setPriority(it) }
                builder.setAutoCancel(true)
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                NotificationManagerCompat.from(this)
                    .notify(notification.tag?.hashCode() ?: message.hashCode(), builder.build())
            }
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onNewToken(token: String) {
        Notifications.notificationToken.value = token.optional
    }
}

internal fun Context.getResourceUri(@AnyRes resourceId: Int): Uri = Uri.Builder()
    .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
    .authority(packageName)
    .path(resourceId.toString())
    .build()