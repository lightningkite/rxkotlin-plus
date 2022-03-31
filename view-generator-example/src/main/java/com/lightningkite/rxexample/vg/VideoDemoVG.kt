//! This file will translate using Khrysalis.
//
// VideoDemoVG.swift
// Created by Butterfly Prototype Generator
// Sections of this file can be replaces if the marker, '(overwritten on flow generation)', is left in place.
//
package com.lightningkite.rxexample.vg

//--- Imports

import android.view.View
import android.widget.ImageView
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.lightningkite.rx.android.resources.Video
import com.lightningkite.rx.android.resources.VideoReference
import com.lightningkite.rx.android.resources.VideoRemoteUrl
import com.lightningkite.rx.viewgenerators.ActivityAccess
import com.lightningkite.rx.ValueSubject
import com.lightningkite.rx.android.bind
import com.lightningkite.rx.android.bindVideoThumbnail
import com.lightningkite.rx.viewgenerators.*
import com.lightningkite.rx.android.*
import com.lightningkite.rx.android.resources.*
import com.lightningkite.rxexample.databinding.VideoDemoBinding
import com.lightningkite.rx.kotlin
import com.lightningkite.rx.optional
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.util.*

//--- Name (overwritten on flow generation)
@Suppress("NAME_SHADOWING")
class VideoDemoVG(
    //--- Dependencies (overwritten on flow generation)
    //--- Extends (overwritten on flow generation)
) : ViewGenerator {

    //--- Properties
    val currentVideo =
        ValueSubject<Optional<Video>>(VideoRemoteUrl("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4").optional)
    val timesPlayPressed = ValueSubject<Int>(0)

    //--- Generate Start (overwritten on flow generation)
    override fun generate(dependency: ActivityAccess): View {
        val xml = VideoDemoBinding.inflate(dependency.layoutInflater)
        val view = xml.root

        //--- Set Up xml.video
        currentVideo.into(xml.video) {
            setVideo(it.kotlin)
        }

        //--- Set Up xml.play (overwritten on flow generation)
        xml.play.onClick { this.playClick() }

        //--- Set Up xml.gallery
        xml.gallery.onClick {
            dependency.requestVideoGallery().subscribeBy {
                currentVideo.value = VideoReference(it).optional
            }
        }

        //--- Set Up xml.camera
        xml.camera.onClick {
            dependency.requestVideoCamera().subscribeBy {
                currentVideo.value = VideoReference(it).optional
            }
        }

        //--- Set Up xml.galleryMulti
        xml.galleryMulti.onClick {
            dependency.requestVideosGallery().subscribeBy {
                it.firstOrNull()?.let {
                    currentVideo.value = VideoReference(it).optional
                }
            }
        }

        //--- Generate End (overwritten on flow generation)

        return view
    }

    //--- Init

    init {
        //--- Init End
    }

    //--- Actions

    //--- Action playClick
    fun playClick() {
        timesPlayPressed.value++
        when (timesPlayPressed.value % 3) {
            0 -> currentVideo.value =
                VideoRemoteUrl("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4").optional
            1 -> currentVideo.value =
                VideoRemoteUrl("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4").optional
            2 -> currentVideo.value = Optional.empty()
        }
    }

    //--- Action galleryClick

    //--- Action cameraClick (overwritten on flow generation)
    fun cameraClick() {
    }

    //--- Action galleryMultiClick (overwritten on flow generation)
    fun galleryMultiClick() {
    }

    //--- Body End
}
