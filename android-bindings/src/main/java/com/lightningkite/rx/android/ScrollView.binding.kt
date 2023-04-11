package com.lightningkite.rx.android

import android.os.Build
import android.view.View
import android.view.View.OnScrollChangeListener
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import androidx.annotation.CheckResult
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.subject.Subject
import com.lightningkite.rx.withWrite


data class ViewScrollChangeEvent(
    /** The view from which this event occurred.  */
    val view: View,
    val scrollX: Int,
    val scrollY: Int,
    val oldScrollX: Int,
    val oldScrollY: Int
)

@RequiresApi(23)
fun View.scrollChangeEvents(): Observable<ViewScrollChangeEvent> {
    return ViewScrollChangeEventObservable(this)
}

@RequiresApi(23)
private class ViewScrollChangeEventObservable(
    private val view: View
) : Observable<ViewScrollChangeEvent> {

    override fun subscribe(observer: ObservableObserver<ViewScrollChangeEvent>) {
        val listener = Listener(view, observer)
        observer.onSubscribe(listener)
        view.setOnScrollChangeListener(listener)
    }

    private class Listener(
        private val view: View,
        private val observer: ObservableObserver<ViewScrollChangeEvent>
    ) : OnScrollChangeListener, Disposable{

        override fun onScrollChange(
            v: View,
            scrollX: Int,
            scrollY: Int,
            oldScrollX: Int,
            oldScrollY: Int
        ) {
            if (!isDisposed) {
                observer.onNext(ViewScrollChangeEvent(v, scrollX, scrollY, oldScrollX, oldScrollY))
            }
        }

        private var disposed: Boolean = false
        override val isDisposed: Boolean
            get() = disposed

        override fun dispose() {
            disposed = true
            view.setOnLongClickListener(null)
        }
    }
}



@RequiresApi(Build.VERSION_CODES.M)
fun <T : Subject<Int>> T.bindScrollPosition(scrollView: ScrollView): T =
    bindView(scrollView, scrollView.scrollChangeEvents().map { it.scrollY }.withWrite { scrollView.scrollTo(0, it) })

@RequiresApi(Build.VERSION_CODES.M)
fun <T : Subject<Int>> T.bindScrollPosition(horizontalScrollView: HorizontalScrollView): T =
    bindView(
        horizontalScrollView,
        horizontalScrollView.scrollChangeEvents().map { it.scrollX }.withWrite { horizontalScrollView.scrollTo(it, 0) })

data class RecyclerViewScrollPosition(val item: Int, val offset: Int)

@RequiresApi(Build.VERSION_CODES.M)
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