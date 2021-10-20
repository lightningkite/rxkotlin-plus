//! This file will translate using Khrysalis.
package com.lightningkite.rx.android.resources

import android.net.Uri

/**
 * Video is a way to consolidate all the ways a Video is described, handled, or created in the app.
 * These are Uri references, or a remote Url.
 */
sealed class Video
data class VideoReference(val uri: Uri): Video()
//data class VideoRaw(val raw: Data): Video()
data class VideoRemoteUrl(val url: String): Video()

fun String.asVideo(): Video = VideoRemoteUrl(this)
fun Uri.asVideo(): Video = VideoReference(this)
