//! This file will translate using Khrysalis.
package com.lightningkite.rx.android.resources

import android.graphics.Bitmap
import android.net.Uri

/**
 * Image is a way to consolidate all the ways an image is described, handled, or created in the app.
 * Images can be a Uri reference, a remote URL, a bitmap, a Raw Byte Array, or an Android resource.
 */
sealed class Image
data class ImageReference(val uri: Uri): Image()
data class ImageBitmap(val bitmap: Bitmap): Image()
data class ImageRaw(val data: ByteArray): Image()
data class ImageRemoteUrl(val url: String): Image()
data class ImageResource(val resource: DrawableResource): Image()

fun String.asImage(): Image = ImageRemoteUrl(this)
fun Uri.asImage(): Image = ImageReference(this)
fun Bitmap.asImage(): Image = ImageBitmap(this)
fun DrawableResource.asImage(): Image = ImageResource(this)
