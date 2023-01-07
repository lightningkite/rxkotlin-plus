package com.lightningkite.rx.android.resources

import android.app.Activity
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.TransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.lightningkite.rx.forever
import io.reactivex.rxjava3.kotlin.subscribeBy

/**
 * Loads and sets the image into the ImageView.
 */
fun ImageView.setImage(image: Image?) {
    post {
        image?.let { image ->
            Glide.with(this).request(image).into(this)
        }
        if (image == null) {
            this.setImageDrawable(null)
        }
    }
}

private fun RequestManager.request(image: Image): RequestBuilder<Drawable> {
    return when (image) {
        is ImageRaw -> this.load(image.data)
        is ImageReference -> this.load(image.uri)
        is ImageBitmap -> this.load(image.bitmap)
        is ImageRemoteUrl -> this.load(image.url)
        is ImageResource -> this.load(image.resource)
    }
}

/**
 * Loads a series of
 */
fun ImageView.setImages(images: List<Image>) {
    post {
        if (images.isEmpty()) setImageDrawable(null)
        else {
            val basis = Glide.with(this)
            basis.request(images.last())
                .thumbnail(
                    images.dropLast(1).reversed().map {
                        basis.request(it)
                    }
                )
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(this)
        }
    }
}

