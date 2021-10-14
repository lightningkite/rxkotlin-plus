package com.lightningkite.rx.android.resources

import android.net.Uri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import io.reactivex.rxjava3.kotlin.addTo
import com.lightningkite.rx.kotlin
import com.lightningkite.rx.android.removed
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.plusAssign
import java.util.Optional
import java.util.*

private var PlayerView_player = WeakHashMap<PlayerView, SimpleExoPlayer>()

fun PlayerView.setVideo(video: Video?, playWhenReady: Boolean = false) {
    val player = PlayerView_player.getOrPut(this) {
        val p = SimpleExoPlayer.Builder(context).build()
        removed += Disposable.fromAction {
            p.release()
        }
        p
    }
    player.stop()
    player.clearVideoSurface()
    when (video) {
        is VideoReference -> {
            val agent = Util.getUserAgent(context, context.getString(R.string.app_name))
            val factory = DefaultDataSourceFactory(context, agent)
            val source = ProgressiveMediaSource.Factory(factory).createMediaSource(MediaItem.fromUri(video.uri))
            player.prepare(source)
            player.playWhenReady = playWhenReady
        }
        is VideoRemoteUrl -> {
            val agent = Util.getUserAgent(context, context.getString(R.string.app_name))
            val factory = DefaultDataSourceFactory(context, agent)
            val source = ProgressiveMediaSource.Factory(factory).createMediaSource(MediaItem.fromUri(video.url))
            player.prepare(source)
            player.playWhenReady = playWhenReady
        }
        else -> {
        }
    }
}
