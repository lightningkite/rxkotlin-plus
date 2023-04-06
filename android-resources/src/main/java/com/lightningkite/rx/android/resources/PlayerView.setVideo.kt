package com.lightningkite.rx.android.resources

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.plusAssign
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.lightningkite.rx.android.removed
import java.util.*

private var PlayerView_player = WeakHashMap<StyledPlayerView, ExoPlayer>()

/**
 * Sets the video up in the Player view.
 */
fun StyledPlayerView.setVideo(video: Video?, playWhenReady: Boolean = false) {
    val exoPlayer = PlayerView_player.getOrPut(this) {
        val p = ExoPlayer.Builder(context).build()
        removed += Disposable {
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
