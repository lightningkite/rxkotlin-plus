package com.lightningkite.rx.viewgenerators


import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import com.lightningkite.rx.android.resources.Image
import com.lightningkite.rx.android.resources.ImageReference
import com.lightningkite.rx.android.resources.ImageRemoteUrl
import com.lightningkite.rx.viewgenerators.ActivityAccess
import java.time.ZonedDateTime
import java.util.*

/**
 * Starts an intent with a direct callback.
 */
fun ActivityAccess.startIntent(
    intent: Intent,
    options: Bundle = android.os.Bundle(),
    onResult: (Int, Intent?) -> Unit = { _, _ -> }
) {
    activity.startActivityForResult(intent, prepareOnResult(onResult = onResult), options)
}

fun ActivityAccess.share(shareTitle: String, message: String? = null, url: String? = null, image: Image? = null) {
    val i = Intent(Intent.ACTION_SEND)
    i.type = "text/plain"
    listOfNotNull(message, url).joinToString("\n").let { i.putExtra(Intent.EXTRA_TEXT, it) }
    if (image != null) {
        when (image) {
            is ImageReference -> {
                i.setType("image/jpeg")
                i.putExtra(Intent.EXTRA_STREAM, image.uri)
            }
            is ImageRemoteUrl -> {
                i.setType("image/jpeg")
                i.putExtra(Intent.EXTRA_STREAM, Uri.parse(image.url))
            }
        }
    }
    context.startActivity(Intent.createChooser(i, shareTitle))
}

fun ActivityAccess.openUrl(url: String, newWindow: Boolean = true): Boolean {
    val mgr = context.packageManager
    val intent = Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) }
    val list = mgr.queryIntentActivities(
        intent,
        PackageManager.MATCH_DEFAULT_ONLY
    )
    return if (list.size > 0) {
        startIntent(intent = intent)
        true
    } else {
        false
    }
}

fun ActivityAccess.openAndroidAppOrStore(packageName: String) {
    val mgr = context.packageManager
    val intent = mgr.getLaunchIntentForPackage(packageName)
    if (intent != null) {
        startIntent(intent = intent)
    } else {
        openUrl("market://details?id=$packageName")
    }
}

fun ActivityAccess.openIosStore(numberId: String) {
    openUrl("https://apps.apple.com/us/app/taxbot/id$numberId")
}

fun ActivityAccess.openMap(latitude: Double, longitude: Double, label: String? = null, zoom: Float? = null) {
    startIntent(
        intent = Intent(Intent.ACTION_VIEW).apply {
            if (label == null) {
                if (zoom == null) {
                    data = Uri.parse("geo:${latitude},${longitude}")
                } else {
                    data = Uri.parse("geo:${latitude},${longitude}?z=$zoom")
                }
            } else {
                if (zoom == null) {
                    data = Uri.parse("geo:${latitude},${longitude}?q=${Uri.encode(label)}")
                } else {
                    data =
                        Uri.parse("geo:${latitude},${longitude}?q=${Uri.encode(label)}&z=$zoom")
                }
            }
        }
    )
}

fun ActivityAccess.openEvent(title: String, description: String, location: String, start: ZonedDateTime, end: ZonedDateTime) {
    startIntent(
        intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, title)
            putExtra(CalendarContract.Events.DESCRIPTION, description)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start.toInstant().toEpochMilli())
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end.toInstant().toEpochMilli())
            putExtra(CalendarContract.Events.EVENT_LOCATION, location)
        }
    )
}
