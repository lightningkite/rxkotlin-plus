package com.lightningkite.rx.okhttp

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.lightningkite.rx.android.resources.*
import androidx.core.graphics.scale
import io.reactivex.rxjava3.core.Single
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.lightningkite.rx.android.staticApplicationContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSink
import okio.Okio
import okio.source
import java.io.ByteArrayOutputStream

/**
 * Makes a [RequestBody] out of an [Image].
 * You can set maximum dimension and file size limits for upload.
 */
fun Image.toRequestBody(maxDimension: Int = 2048, maxBytes: Long = 10_000_000): Single<RequestBody> = Single.create { em ->
    val glide = Glide.with(staticApplicationContext).asBitmap()
    val task = when (this) {
        is ImageReference -> glide.load(this.uri)
        is ImageBitmap -> glide.load(this.bitmap)
        is ImageRaw -> glide.load(this.data)
        is ImageRemoteUrl -> glide.load(this.url)
        is ImageResource -> glide.load(this.resource)
    }
    task
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
                em.onSuccess(resource.toRequestBody(maxDimension, maxBytes))
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                em.onError(Exception())
            }
        })
}

/**
 * Makes a [RequestBody] out of an [Bitmap].
 * You can set maximum dimension and file size limits for upload.
 */
fun Bitmap.toRequestBody(maxDimension: Int = 2048, maxBytes: Long = 10_000_000): RequestBody {
    var qualityToTry = 100
    val ratio = this.width.toFloat()/this.height.toFloat()
    val scaledBimap = this.scale(maxDimension, (maxDimension/ratio).toInt())
    var data = ByteArrayOutputStream().use {
        scaledBimap.compress(Bitmap.CompressFormat.JPEG, qualityToTry, it)
        it.toByteArray()
    }
    while (data.size > maxBytes) {
        qualityToTry -= 5
        data = ByteArrayOutputStream().use {
            scaledBimap.compress(Bitmap.CompressFormat.JPEG, qualityToTry, it)
            it.toByteArray()
        }
    }
    return data.toRequestBody(MediaType.JPEG, 0, data.size)
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