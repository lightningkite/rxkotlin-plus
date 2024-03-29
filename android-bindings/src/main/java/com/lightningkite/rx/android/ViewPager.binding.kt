package com.lightningkite.rx.android

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.lightningkite.rx.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.Subject
import io.reactivex.rxjava3.kotlin.addTo

/**
 * Will display the contents of this in the ViewPager using the makeView provided for each item.
 * The page shown is tied to the showIndex Subject.
 *
 * Example:
 * val data = ValueSubject<List<Int>>(listOf(1,2,3,4,5,6,7,8,9,0))
 * val showing = ValueSubject(0)
 * data.showIn(viewPagerView, showing) { obs -> ... return view }
 */
fun <SOURCE: Observable<out List<T>>, T: Any> SOURCE.showIn(
    viewPager2: ViewPager2,
    showIndex: Subject<Int> = ValueSubject(0),
    makeView: (Observable<T>)->View
): SOURCE {
    var lastSubmitted = listOf<T>()
    viewPager2.adapter = object : ObservableRVA<T>(viewPager2.removed, { 0 }, { _, obs -> makeView(obs) }) {
        init {
            observeOn(RequireMainThread).subscribeBy { it ->
                val new = it.toList()
                lastPublished = new
                this.notifyDataSetChanged()
            }.addTo(viewPager2.removed)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val s = super.onCreateViewHolder(parent, viewType)
            s.itemView.layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            return s
        }
    }
    observeOn(RequireMainThread).subscribeBy { list ->
        lastSubmitted = list
        viewPager2.adapter!!.notifyDataSetChanged()
        viewPager2.currentItem
    }.addTo(viewPager2.removed)
    showIndex.observeOn(RequireMainThread).subscribeBy { value ->
        viewPager2.currentItem = value
    }.addTo(viewPager2.removed)
    viewPager2.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            showIndex.onNext(position)
        }
    })
    return this
}

