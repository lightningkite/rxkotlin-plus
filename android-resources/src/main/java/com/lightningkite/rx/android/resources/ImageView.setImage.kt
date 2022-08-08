package com.lightningkite.rx.android.resources

import android.app.Activity
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
                    if((context as? Activity)?.isDestroyed == true) return@let
                    Glide.with(this).load(image.uri).into(this)
                }
                is ImageBitmap -> this.setImageBitmap(image.bitmap)
                is ImageRemoteUrl -> {
                    if((context as? Activity)?.isDestroyed == true) return@let
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

