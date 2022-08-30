package com.lightningkite.rx.android.resources

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import io.reactivex.rxjava3.core.Single


/**
 * Loads the image and return a Single<Bitmap>. This works for all the types of Images available.
 */
fun Image.load(context: Context): Single<Bitmap> {
    return try {
        when (this) {
            is ImageRaw -> Single.just(BitmapFactory.decodeByteArray(this.data, 0, this.data.size))
            is ImageReference -> load(context = context)
            is ImageBitmap -> Single.just(this.bitmap)
            is ImageRemoteUrl -> load(context = context)
            is ImageResource -> {
                val drawable = ResourcesCompat.getDrawable(context.resources, resource, null)!!
                if (drawable is BitmapDrawable) {
                    Single.just(drawable.bitmap)
                } else {
                    val bitmap = Bitmap.createBitmap(
                        drawable.intrinsicWidth,
                        drawable.intrinsicHeight,
                        Bitmap.Config.ARGB_8888
                    )
                    val canvas = Canvas(bitmap)
                    drawable.setBounds(0, 0, canvas.width, canvas.height)
                    drawable.draw(canvas)
                    Single.just(bitmap)
                }
            }
        }
    } catch (e: Exception) {
        Single.error(e)
    }
}

private fun ImageReference.load(context: Context, maxDimension: Int = 2048): Single<Bitmap> {
    return Single.create { emitter ->
        var emitted = false
        Glide.with(context)
            .asBitmap()
            .load(this.uri)
            .override(maxDimension)
            .downsample(DownsampleStrategy.AT_MOST)
            .into(object : CustomTarget<Bitmap>() {

                override fun onLoadCleared(placeholder: Drawable?) {
                    if(!emitted) {
                        emitted = true
                        emitter.onError(Exception("Failed to load drawable"))
                    }
                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    if(!emitted) {
                        emitted = true
                        emitter.onSuccess(resource)
                    }
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    if(!emitted) {
                        emitted = true
                        emitter.onError(Exception())
                    }
                }
            })
    }
}

private fun ImageRemoteUrl.load(context: Context): Single<Bitmap> {
    return Single.create { emitter ->
        var emitted = false
        Glide.with(context)
            .asBitmap()
            .load(this.url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                    if(!emitted) {
                        emitted = true
                        emitter.onError(Exception("Failed to load drawable"))
                    }
                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    if(!emitted) {
                        emitted = true
                        emitter.onSuccess(resource)
                    }
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    if(!emitted) {
                        emitted = true
                        emitter.onError(Exception())
                    }
                }
            })
    }
}
