package com.lightningkite.rxkotlinproperty.android.resources

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.lightningkite.rxkotlinproperty.android.forever
import com.lightningkite.rxkotlinproperty.android.kotlin
import com.lightningkite.rxkotlinproperty.android.removed
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import java9.util.Optional

/**
 *
 * Binds the imageview internal image to the image provided by the observable.
 * Any changes to the observable will cause a reload of the image to match the change.
 * An image can be from multiple sources, such as the web, an android image reference,
 * and a direct bitmap. It will handle all cases and load the image.
 *
 */
fun ImageView.bindImage(image: Observable<Image>) {
    image.subscribeBy { it ->
        post {
            this.setImage(it)
        }
    }.addTo(this.removed)
}

fun ImageView.bindVideoThumbnail(video: Observable<Video>) {
    video.subscribeBy {
        post {
            this.setFromVideoThumbnail(it)
        }
    }.addTo(removed)
}

@JvmName("bindImageOptional")
fun ImageView.bindImage(image: Observable<Optional<Image>>) {
    image.subscribeBy { it ->
        post {
            this.setImage(it.kotlin)
        }
    }.addTo(this.removed)
}

@JvmName("bindVideoThumbnailOptional")
fun ImageView.bindVideoThumbnail(video: Observable<Optional<Video>>) {
    video.subscribeBy {
        post {
            this.setFromVideoThumbnail(it.kotlin)
        }
    }.addTo(removed)
}

/**
 *
 * Loads the image into the imageview the function is called on.
 * An image can be from multiple sources, such as the web, an android image reference,
 * and a direct bitmap. It will handle all cases and load the image.
 *
 */
fun ImageView.setImage(image: Image?) {
    post {
        image?.let { image ->
            when (image) {
                is ImageRaw -> this.setImageBitmap(BitmapFactory.decodeByteArray(image.raw, 0, image.raw.size))
                is ImageReference -> {
                    Glide.with(this).load(image.uri).into(this)
                }
                is ImageBitmap -> this.setImageBitmap(image.bitmap)
                is ImageRemoteUrl -> {
                    Glide.with(this).load(image.url).into(this)
                }
                is ImageResource -> this.setImageResource(image.resource)
            }
        }
        if (image == null) {
            this.setImageDrawable(null)
        }
    }
}


/**
 *
 * Loads a thumbnail from the video into the imageview the function is called on.
 * Video can be from a local reference or a URL.
 *
 */
fun ImageView.setFromVideoThumbnail(video: Video?) {
    if (video == null) return
    setImage(null)
    video.thumbnail(context = context).subscribeBy(
        onError = {
            setImage(null)
        },
        onSuccess = {
            setImage(it)
        }
    ).forever()
}