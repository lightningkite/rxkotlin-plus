//! This file will translate using Khrysalis.
package com.lightningkite.rxexample.vg

import android.view.View
import android.widget.TextView
import com.lightningkite.rx.ValueSubject
import com.lightningkite.rx.android.into
import com.lightningkite.rx.android.removed
import com.lightningkite.rx.android.resources.Image
import com.lightningkite.rx.android.resources.ImageReference
import com.lightningkite.rx.android.resources.ImageRemoteUrl
import com.lightningkite.rx.android.resources.setImage
import com.lightningkite.rx.kotlin
import com.lightningkite.rx.mapFromNullable
import com.lightningkite.rx.okhttp.toRequestBody
import com.lightningkite.rx.optional
import com.lightningkite.rx.viewgenerators.*
import com.lightningkite.rxexample.databinding.LoadImageDemoBinding
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.util.*

class LoadImageDemoVG : ViewGenerator {

    val canUpload = ValueSubject<Optional<Boolean>>(Optional.empty())
    val currentImage = ValueSubject<Optional<Image>>(Optional.empty())

    override fun generate(dependency: ActivityAccess): View {
        val xml = LoadImageDemoBinding.inflate(dependency.layoutInflater)
        val view = xml.root

        currentImage.subscribeBy {
            canUpload.value = Optional.empty()
        }.addTo(view.removed)

        currentImage.into(xml.image) {
            xml.image.setImage(
                it.kotlin
            )
        }
        xml.camera.setOnClickListener {
            dependency.requestImageCamera().subscribeBy { url ->
                currentImage.value = ImageReference(url).optional
            }
        }
        xml.galleryMultiple.setOnClickListener {
            dependency.requestImagesGallery().subscribeBy { urls ->
                urls.firstOrNull()?.let { url ->
                    currentImage.value = ImageReference(url).optional
                }
            }
        }
        xml.gallery.setOnClickListener {
            dependency.requestImageGallery().subscribeBy { url ->
                currentImage.value = ImageReference(url).optional
            }
        }
        xml.loremPixel.setOnClickListener {
            currentImage.value = ImageRemoteUrl("https://picsum.photos/200").optional
        }
        xml.checkCanUpload.setOnClickListener {
            currentImage.value.kotlin?.let { i ->
                i.toRequestBody().subscribeBy(
                    onError = {
                        it.printStackTrace()
                        canUpload.value = false.optional
                    },
                    onSuccess = {
                        canUpload.value = true.optional
                    }
                )
            }
        }
        canUpload.mapFromNullable {
            if (it == null) "Not checked" else if (it == true) "Good to go!" else "FAILED!!!"
        }.into(xml.canUpload, TextView::setText)
        return view
    }
}
