package com.lightningkite.rx.okhttp

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.lightningkite.rx.android.resources.*
import androidx.core.graphics.scale
import com.bumptech.glide.RequestBuilder
import io.reactivex.rxjava3.core.Single
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.lightningkite.rx.android.staticApplicationContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSink
import okio.source
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import kotlin.math.max
import kotlin.math.min

@Suppress("DEPRECATION")
private val String.stringToCompressFormat: CompressFormat
    get() = when (this) {
        "jpeg" -> CompressFormat.JPEG
        "png" -> CompressFormat.PNG
        "webp" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) CompressFormat.WEBP_LOSSLESS else CompressFormat.WEBP
        else -> CompressFormat.PNG
    }

@Suppress("DEPRECATION")
private val CompressFormat.toSubType: String
    get() = when {
        this == CompressFormat.JPEG -> "jpg"
        this == CompressFormat.PNG -> "png"
        this == CompressFormat.WEBP ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && (this == CompressFormat.WEBP_LOSSY || this == CompressFormat.WEBP_LOSSLESS)) -> "webp"
        else -> "png"
    }

/**
 * Makes a [RequestBody] out of an [Image].
 * You can set maximum dimension and file size limits for upload.
 */
fun Image.toRequestBody(maxDimension: Int = 2048, maxBytes: Long = 10_000_000): Single<RequestBody> =
    Single.create { em ->
        val glide = Glide.with(staticApplicationContext).asBitmap()
        val task: Pair<RequestBuilder<Bitmap>, CompressFormat> = when (this) {
            is ImageReference ->
                glide.load(this.uri) to (staticApplicationContext.contentResolver.getType(this.uri)
                    ?: "image/png").toMediaType().subtype.stringToCompressFormat
            is ImageBitmap -> glide.load(this.bitmap) to CompressFormat.PNG
            is ImageRaw -> glide.load(this.data) to CompressFormat.PNG
            is ImageRemoteUrl -> glide.load(this.url) to this.url.substringAfterLast(".").stringToCompressFormat
            is ImageResource -> glide.load(this.resource) to CompressFormat.PNG
        }
        task.first
            .addListener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    isFirstResource: Boolean
                ): Boolean {
                    e?.printStackTrace() ?: Exception().printStackTrace()
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

            })
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {}
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    em.onSuccess(resource.toRequestBody(maxDimension, maxBytes, task.second))
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    em.onError(Exception())
                }
            })
    }

private fun Bitmap.resize(maxDimension: Int): Bitmap {
    val ratio = this.width.toFloat() / this.height.toFloat()
    return if(height > width) this.scale((maxDimension * ratio).toInt(), maxDimension) else this.scale(maxDimension, (maxDimension / ratio).toInt())
}

/**
 * Makes a [RequestBody] out of an [Bitmap].
 * You can set maximum dimension and file size limits for upload.
 */
fun Bitmap.toRequestBody(
    maxDimension: Int = 2048,
    maxBytes: Long = 10_000_000,
    format: CompressFormat
): RequestBody {
    var quality = 100
    var data: ByteArray
    var realDimension = min(max(height, width), maxDimension)
    var scaledBimap = resize(realDimension)
    do {
        data = ByteArrayOutputStream().use {
            @Suppress("DEPRECATION")
            when {
                format == CompressFormat.JPEG ||
                        format == CompressFormat.WEBP ||
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && format == CompressFormat.WEBP_LOSSY -> {
                    scaledBimap.compress(format, quality, it)
                    quality -= 5
                }
                else -> {
                    scaledBimap = resize(realDimension)
                    scaledBimap.compress(format, quality, it)
                    realDimension = (realDimension * 0.95f).toInt()
                }

            }
            it.toByteArray()
        }
    } while (data.size > maxBytes)

    return data.toRequestBody("image/${format.toSubType}".toMediaType(), 0, data.size)
}

/**
 * Makes a [RequestBody] out of a [Uri].
 */
fun Uri.toRequestBody(): Single<RequestBody> {
    val type = (staticApplicationContext.contentResolver.getType(this) ?: "application/octet-stream").toMediaType()
    return Single.just<RequestBody>(object : RequestBody() {
        override fun contentType(): MediaType = type

        override fun writeTo(sink: BufferedSink) {
            staticApplicationContext.contentResolver.openInputStream(this@toRequestBody)?.use {
                it.source().use { source ->
                    sink.writeAll(source)
                }
            } ?: throw IllegalStateException("URI (${this@toRequestBody}) could not be opened")
        }
    })
}