package com.lightningkite.rx.viewgenerators


import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import com.lightningkite.rx.android.resources.Image
import com.lightningkite.rx.android.resources.ImageReference
import com.lightningkite.rx.android.resources.ImageRemoteUrl
import java.time.ZonedDateTime

/**
 * Open a sharing dialog.
 */
fun ActivityAccess.share(title: String, message: String? = null, url: String? = null, image: Image? = null) {
    val i = Intent(Intent.ACTION_SEND)
    i.type = "text/plain"
    i.putExtra(Intent.EXTRA_TITLE, title)
    listOfNotNull(message, url).joinToString("\n").let { i.putExtra(Intent.EXTRA_TEXT, it) }
    if (image != null) {
        when (image) {
            is ImageReference -> {
                i.setType("image/jpeg")
                i.putExtra(Intent.EXTRA_STREAM, image.uri)
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            is ImageRemoteUrl -> {
                i.setType("image/jpeg")
                i.putExtra(Intent.EXTRA_STREAM, Uri.parse(image.url))
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }
    }
    context.startActivity(Intent.createChooser(i, title))
}

/**
 * Open a URL
 */
fun ActivityAccess.openUrl(url: String, newWindow: Boolean = true): Boolean {
    val intent = Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) }
    startIntent(intent = intent)
    return true
}

/**
 * Open a particular app by package name, or the app store if it is not present.
 */
fun ActivityAccess.openAndroidAppOrStore(packageName: String) {
    val mgr = context.packageManager
    val intent = mgr.getLaunchIntentForPackage(packageName)
    if (intent != null) {
        startIntent(intent = intent)
    } else {
        openUrl("market://details?id=$packageName")
    }
}

/**
 * Shortcut to open the map to a particular location.
 */
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

/**
 * Shortcut to open an event form in your calendar, prepopulated.
 */
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
