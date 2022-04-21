package com.lightningkite.rx.android

import android.widget.HorizontalScrollView
import android.widget.ScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding4.view.scrollChangeEvents
import com.jakewharton.rxbinding4.widget.ratingChanges
import com.lightningkite.rx.withWrite
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.Subject

fun <T : Subject<Int>> T.bindScrollPosition(scrollView: ScrollView): T =
    bindView(scrollView, scrollView.scrollChangeEvents().map { it.scrollY }.withWrite { scrollView.scrollTo(0, it) })

fun <T : Subject<Int>> T.bindScrollPosition(horizontalScrollView: HorizontalScrollView): T =
    bindView(
        horizontalScrollView,
        horizontalScrollView.scrollChangeEvents().map { it.scrollX }.withWrite { horizontalScrollView.scrollTo(it, 0) })

data class RecyclerViewScrollPosition(val item: Int, val offset: Int)

fun <T : Subject<RecyclerViewScrollPosition>> T.bindScrollPosition(recyclerView: RecyclerView): T =
    bindView(recyclerView, recyclerView.scrollChangeEvents().map {
        when (val lm = recyclerView.layoutManager) {
            is LinearLayoutManager -> {
                val pos = lm.findFirstVisibleItemPosition()
                RecyclerViewScrollPosition(pos, lm.findViewByPosition(pos)?.top ?: 0)
            }
            else -> throw UnsupportedOperationException()
        }
    }.withWrite {
        when (val lm = recyclerView.layoutManager) {
            is LinearLayoutManager -> lm.scrollToPositionWithOffset(it.item, it.offset)
            else -> throw UnsupportedOperationException()
        }
    })