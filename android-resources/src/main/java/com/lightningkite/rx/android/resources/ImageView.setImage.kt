package com.lightningkite.rx.android.resources

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.lightningkite.rx.forever
import com.lightningkite.rx.kotlin
import com.lightningkite.rx.android.removed
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.util.Optional

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
    setImage(null)
    if (video == null) return
    video.thumbnail(context = context).subscribeBy(
        onError = {
            setImage(null)
        },
        onSuccess = {
            setImage(it)
        }
    ).forever()
}