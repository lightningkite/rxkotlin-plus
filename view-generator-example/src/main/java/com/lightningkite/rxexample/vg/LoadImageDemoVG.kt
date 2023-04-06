//! This file will translate using Khrysalis.
package com.lightningkite.rxexample.vg

import android.view.View
import android.widget.TextView
import com.badoo.reaktive.disposable.addTo
import com.badoo.reaktive.maybe.subscribe
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.single.subscribe
import com.badoo.reaktive.subject.behavior.BehaviorSubject
import com.lightningkite.rx.android.into
import com.lightningkite.rx.android.removed
import com.lightningkite.rx.android.resources.Image
import com.lightningkite.rx.android.resources.ImageReference
import com.lightningkite.rx.android.resources.ImageRemoteUrl
import com.lightningkite.rx.android.resources.setImage
import com.lightningkite.rx.okhttp.toRequestBody
import com.lightningkite.rx.viewgenerators.*
import com.lightningkite.rxexample.databinding.LoadImageDemoBinding
import java.util.*

class LoadImageDemoVG : ViewGenerator {

    val canUpload = BehaviorSubject<Boolean?>(null)
    val currentImage = BehaviorSubject<Image?>(null)

    override fun generate(dependency: ActivityAccess): View {
        val xml = LoadImageDemoBinding.inflate(dependency.layoutInflater)
        val view = xml.root

        currentImage.subscribe {
            canUpload.onNext(null)
        }.addTo(view.removed)

        currentImage.into(xml.image) {
            xml.image.setImage(
                it
            )
        }
        xml.camera.setOnClickListener {
            dependency.requestImageCamera().subscribe { url ->
                currentImage.onNext(ImageReference(url))
            }
        }
        xml.galleryMultiple.setOnClickListener {
            dependency.requestImagesGallery().subscribe { urls ->
                urls.firstOrNull()?.let { url ->
                    currentImage.onNext(ImageReference(url))
                }
            }
        }
        xml.gallery.setOnClickListener {
            dependency.requestImageGallery().subscribe { url ->
                currentImage.onNext(ImageReference(url))
            }
        }
        xml.loremPixel.setOnClickListener {
            currentImage.onNext(ImageRemoteUrl("https://picsum.photos/200"))
        }
        xml.checkCanUpload.setOnClickListener {
            currentImage.value?.let { i ->
                i.toRequestBody().subscribe(
                    onError = {
                        it.printStackTrace()
                        canUpload.onNext(false)
                    },
                    onSuccess = {
                        canUpload.onNext(true)
                    }
                )
            }
        }
        canUpload.map{
            if (it == null) "Not checked" else if (it == true) "Good to go!" else "FAILED!!!"
        }.into(xml.canUpload, TextView::setText)
        return view
    }
}
