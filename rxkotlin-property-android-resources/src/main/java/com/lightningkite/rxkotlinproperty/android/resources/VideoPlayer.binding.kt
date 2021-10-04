package com.lightningkite.rxkotlinproperty.android.resources

import android.net.Uri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.lightningkite.rxkotlinproperty.Property
import com.lightningkite.rxkotlinproperty.android.removed
import com.lightningkite.rxkotlinproperty.observable
import com.lightningkite.rxkotlinproperty.until

fun VideoPlayer.bind(video: Property<Video?>){
    bindVideoToView(this, video)
}

fun PlayerView.bind(video: Property<Video?>) {
    bindVideoToView(this, video)
}

private fun bindVideoToView(view:PlayerView, video: Property<Video?>){
    val player: SimpleExoPlayer = SimpleExoPlayer.Builder(view.context).build()
    view.player = player
    video.observable.doOnDispose { player.release() }.subscribe { videoBox ->
        val video = videoBox.value
        if(video == null) {
            player.stop()
            player.clearVideoSurface()
            return@subscribe
        }
        when (video) {
            is VideoReference -> {
                val agent = Util.getUserAgent(view.context, view.context.getString(R.string.app_name))
                val factory = DefaultDataSourceFactory(view.context, agent)
                val source = ProgressiveMediaSource.Factory(factory).createMediaSource(MediaItem.fromUri(video.uri))
                player.prepare(source)
            }
            is VideoRemoteUrl -> {
                val agent = Util.getUserAgent(view.context, view.context.getString(R.string.app_name))
                val factory = DefaultDataSourceFactory(view.context, agent)
                val source = ProgressiveMediaSource.Factory(factory).createMediaSource(MediaItem.fromUri(video.url))
                player.prepare(source)
            }
            else -> {
            }
        }
    }.until(view.removed)
}

fun VideoPlayer.bindAndStart(video: Property<Video?>){
    bindVideoToViewAndStart(this, video)
}

fun PlayerView.bindAndStart(video: Property<Video?>) {
    bindVideoToViewAndStart(this, video)
}

private fun bindVideoToViewAndStart(view:PlayerView, video:Property<Video?>){
    val player: SimpleExoPlayer = SimpleExoPlayer.Builder(view.context).build()
    view.player = player
    video.observable.doOnDispose { player.release() }.subscribe { videoBox ->
        val video = videoBox.value
        if(video == null) {
            player.stop()
            player.clearVideoSurface()
            return@subscribe
        }
        when (video) {
            is VideoReference -> {
                val agent = Util.getUserAgent(view.context, view.context.getString(R.string.app_name))
                val factory = DefaultDataSourceFactory(view.context, agent)
                val source = ProgressiveMediaSource.Factory(factory).createMediaSource(MediaItem.fromUri(video.uri))
                player.playWhenReady = true
                player.prepare(source)
            }
            is VideoRemoteUrl -> {
                val agent = Util.getUserAgent(view.context, view.context.getString(R.string.app_name))
                val factory = DefaultDataSourceFactory(view.context, agent)
                val source = ProgressiveMediaSource.Factory(factory).createMediaSource(MediaItem.fromUri(video.url))
                player.playWhenReady = true
                player.prepare(source)
            }
            else -> {
            }
        }
    }.until(view.removed)
}