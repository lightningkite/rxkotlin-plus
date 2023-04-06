//! This file will translate using Khrysalis.
//
// VideoDemoVG.swift
// Created by Butterfly Prototype Generator
// Sections of this file can be replaces if the marker, '(overwritten on flow generation)', is left in place.
//
package com.lightningkite.rxexample.vg

//--- Imports

import android.view.View
import com.badoo.reaktive.maybe.subscribe
import com.badoo.reaktive.subject.behavior.BehaviorSubject
import com.lightningkite.rx.android.into
import com.lightningkite.rx.android.resources.Video
import com.lightningkite.rx.android.resources.VideoReference
import com.lightningkite.rx.android.resources.VideoRemoteUrl
import com.lightningkite.rx.android.resources.setVideo
import com.lightningkite.rx.viewgenerators.*
import com.lightningkite.rxexample.databinding.VideoDemoBinding
import java.util.*

//--- Name (overwritten on flow generation)
@Suppress("NAME_SHADOWING")
class VideoDemoVG : ViewGenerator {

    //--- Properties
    val currentVideo =
        BehaviorSubject<Video?>(VideoRemoteUrl("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"))
    val timesPlayPressed = BehaviorSubject<Int>(0)

    //--- Generate Start (overwritten on flow generation)
    override fun generate(dependency: ActivityAccess): View {
        val xml = VideoDemoBinding.inflate(dependency.layoutInflater)
        val view = xml.root

        //--- Set Up xml.video
        currentVideo.into(xml.video) {
            setVideo(it)
        }

        //--- Set Up xml.play (overwritten on flow generation)
        xml.play.setOnClickListener { this.playClick() }

        //--- Set Up xml.gallery
        xml.gallery.setOnClickListener {
            dependency.requestVideoGallery().subscribe {
                currentVideo.onNext(VideoReference(it))
            }
        }

        //--- Set Up xml.camera
        xml.camera.setOnClickListener {
            dependency.requestVideoCamera().subscribe {
                currentVideo.onNext(VideoReference(it))
            }
        }

        //--- Set Up xml.galleryMulti
        xml.galleryMulti.setOnClickListener {
            dependency.requestVideosGallery().subscribe {
                it.firstOrNull()?.let {
                    currentVideo.onNext(VideoReference(it))
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
        timesPlayPressed.onNext(timesPlayPressed.value + 1)
        when (timesPlayPressed.value % 3) {
            0 -> currentVideo.onNext(VideoRemoteUrl("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"))
            1 -> currentVideo.onNext(VideoRemoteUrl("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"))
            2 -> currentVideo.onNext(null)
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
