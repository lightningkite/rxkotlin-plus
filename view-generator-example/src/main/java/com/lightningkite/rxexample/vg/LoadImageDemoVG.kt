//! This file will translate using Khrysalis.
package com.lightningkite.rxexample.vg

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.lightningkite.rx.android.resources.Image
import com.lightningkite.rx.android.resources.ImageReference
import com.lightningkite.rx.android.resources.ImageRemoteUrl
import com.lightningkite.rx.viewgenerators.ActivityAccess
import com.lightningkite.rx.okhttp.toHttpBody
import com.lightningkite.rx.ValueSubject
import com.lightningkite.rx.android.bindImage
import com.lightningkite.rx.android.bindString

import io.reactivex.rxjava3.kotlin.subscribeBy
import com.lightningkite.rx.android.removed
import io.reactivex.rxjava3.kotlin.addTo
import com.lightningkite.rx.viewgenerators.*
import com.lightningkite.rx.android.*
import com.lightningkite.rx.android.resources.*
import com.lightningkite.rxexample.databinding.LoadImageDemoBinding
import com.lightningkite.rx.kotlin
import com.lightningkite.rx.mapFromNullable
import com.lightningkite.rx.okhttp.toRequestBody
import com.lightningkite.rx.optional
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.util.*

class LoadImageDemoVG : ViewGenerator {
    override val titleString: ViewString get() = ViewStringRaw("Load Image Demo")

    val canUpload = ValueSubject<Optional<Boolean>>(Optional.empty())
    val currentImage = ValueSubject<Optional<Image>>(Optional.empty())

    override fun generate(dependency: ActivityAccess): View {
        val xml = LoadImageDemoBinding.inflate(dependency.layoutInflater)
        val view = xml.root

        currentImage.subscribeBy {
            canUpload.value = Optional.empty()
        }.addTo(view.removed)

        currentImage.subscribeAutoDispose<Observable<Optional<Image>>, ImageView, Optional<Image>>(xml.image) { xml.image.setImage(
            it.kotlin
        ) }
        xml.camera.onClick {
            dependency.requestImageCamera().subscribeBy { url ->
                currentImage.value = ImageReference(url).optional
            }
        }
        xml.galleryMultiple.onClick {
            dependency.requestImagesGallery().subscribeBy { urls ->
                urls.firstOrNull()?.let { url ->
                    currentImage.value = ImageReference(url).optional
                }
            }
        }
        xml.gallery.onClick {
            dependency.requestImageGallery().subscribeBy { url ->
                currentImage.value = ImageReference(url).optional
            }
        }
        xml.loremPixel.onClick {
            currentImage.value = ImageRemoteUrl("https://picsum.photos/200").optional
        }
        xml.checkCanUpload.onClick {
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
        }.subscribeAutoDispose(xml.canUpload, TextView::setText)
        return view
    }
}
