package com.lightningkite.rx.android.resources

import android.content.Context
import android.graphics.PointF
import android.media.MediaMetadataRetriever
import android.os.Build
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.single
import com.badoo.reaktive.single.subscribeOn

/**
 * Returns a Single<Image> that is a thumbnail from the video.
 */
fun Video.thumbnail(context: Context, timeMs: Long = 2000L, size: PointF? = null): Single<Image> {
    return single{ em ->
        try {
            val mMMR = when (this) {
                is VideoReference -> {
                    val mMMR = MediaMetadataRetriever()
                    mMMR.setDataSource(context, this.uri)
                    mMMR
                }
                is VideoRemoteUrl -> {
                    val mMMR = MediaMetadataRetriever()
                    mMMR.setDataSource(this.url, HashMap<String, String>())
                    mMMR
                }
            }
            if (size != null && Build.VERSION.SDK_INT >= 27) {
                val frame = mMMR.getScaledFrameAtTime(
                    timeMs ,
                    MediaMetadataRetriever.OPTION_CLOSEST_SYNC,
                    size.x.toInt(),
                    size.y.toInt()
                )
                if(frame != null){
                    em.onSuccess(ImageBitmap(frame))
                } else {
                    em.onError(Exception("No frame could be retrieved"))
                }
            } else {
                val frame = mMMR.getFrameAtTime(timeMs)
                if(frame != null){
                    em.onSuccess(ImageBitmap(frame))
                } else {
                    em.onError(Exception("No frame could be retrieved"))
                }
            }
        } catch (e: Exception) {
            em.onError(e)
        }
    }
        .subscribeOn(Schedulers.io())
}