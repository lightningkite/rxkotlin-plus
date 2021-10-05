package com.lightningkite.rxkotlinproperty.android

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.lightningkite.rxkotlinproperty.Property
import com.lightningkite.rxkotlinproperty.subscribeBy
import com.lightningkite.rxkotlinproperty.until

/**
 *
 * Binds the imageview internal image to the image provided by the property.
 * Any changes to the property will cause a reload of the image to match the change.
 * An image can be from multiple sources, such as the web, an android image reference,
 * and a direct bitmap. It will handle all cases and load the image.
 *
 */

fun ImageView.bindImageUri(image: Property<Uri?>){
    image.subscribeBy { it ->
        post{
            if(it != null){
                Glide.with(this).load(it).into(this)
            }
        }
    }.until(this.removed)
}

fun ImageView.bindImageData(image: Property<ByteArray?>){
    image.subscribeBy { it ->
        post{
            if(it != null) {
                this.setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
            }
        }
    }.until(this.removed)
}

fun ImageView.bindImageBitmap(image: Property<Bitmap?>){
    image.subscribeBy { it ->
        post{
            if(it != null) {
                this.setImageBitmap(it)
            }
        }
    }.until(this.removed)
}

fun ImageView.bindImageUrl(image: Property<String?>){
    image.subscribeBy { it ->
        post{
            if(it != null) {
                Glide.with(this).load(it).into(this)
            }
        }
    }.until(this.removed)
}

fun ImageView.bindImageResource(image: Property<DrawableResource?>){
    image.subscribeBy { it ->
        post{
            if(it != null) {
                this.setImageResource(it)
            }
        }
    }.until(this.removed)
}
