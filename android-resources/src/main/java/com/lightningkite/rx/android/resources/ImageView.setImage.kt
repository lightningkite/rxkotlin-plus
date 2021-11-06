package com.lightningkite.rx.android.resources

import android.graphics.BitmapFactory
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.lightningkite.rx.forever
import io.reactivex.rxjava3.kotlin.subscribeBy

/**
 * Loads and sets the image into the ImageView.
 */
fun ImageView.setImage(image: Image?) {
    post {
        image?.let { image ->
            when (image) {
                is ImageRaw -> this.setImageBitmap(BitmapFactory.decodeByteArray(image.data, 0, image.data.size))
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
 * Loads a thumbnail from the video into the ImageView
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