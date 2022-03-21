package com.lightningkite.rx.android.resources

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.lightningkite.rx.android.removed
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.plusAssign
import java.util.*

private var PlayerView_player = WeakHashMap<StyledPlayerView, ExoPlayer>()

/**
 * Sets the video up in the Player view.
 */
fun StyledPlayerView.setVideo(video: Video?, playWhenReady: Boolean = false) {
    val exoPlayer = PlayerView_player.getOrPut(this) {
        val p = ExoPlayer.Builder(context).build()
        removed += Disposable.fromAction {
            p.release()
        }
        p
    }
    exoPlayer.stop()
    player = exoPlayer
    when (video) {
        is VideoReference -> {
            MediaItem.fromUri(video.uri)
        }
        is VideoRemoteUrl -> {
            MediaItem.fromUri(video.url)
        }
        else -> null
    }
        ?.also { item ->
            exoPlayer.setMediaItem(item)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = playWhenReady
        }
}
