package com.lightningkite.rxkotlinproperty.android

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.lightningkite.rxkotlinproperty.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.Subject
import io.reactivex.rxjava3.kotlin.addTo

/**
 *
 * Binds the items in the ViewPager to the list provided, and the showing index to the property provided.
 * Any changes to the property will change the current page. AS well updating the pager will update the property.
 *
 */
fun <T> ViewPager.bind(
    items: List<T>,
    showIndex: Subject<Int> = BehaviorSubject.createDefault(0),
    makeView: (T)->View
) {
    adapter = object : PagerAdapter() {

        override fun isViewFromObject(p0: View, p1: Any): Boolean = p1 == p0

        override fun getCount(): Int = items.size

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val data = items[position]
            val view = makeView(data)
            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }

    showIndex.subscribeBy{ value ->
        this.currentItem = value
    }.addTo(this.removed)
    this.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(p0: Int) {}
        override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}
        override fun onPageSelected(p0: Int) {
            showIndex.onNext(p0)
        }
    })
}

/**
 *
 * Binds the items in the ViewPager to the list provided, and the showing index to the property provided.
 * Any changes to the property will change the current page. AS well updating the pager will update the property.
 *
 */
fun <T> ViewPager.bind(
    data: Observable<List<T>>,
    defaultValue: T,
    showIndex: Subject<Int> = BehaviorSubject.createDefault(0),
    makeView: (Observable<T>)->View
) {
    var lastSubmitted = listOf<T>()
    adapter = object : PagerAdapter() {
        override fun isViewFromObject(p0: View, p1: Any): Boolean = p1 == p0
        override fun getCount(): Int = lastSubmitted.size
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = makeView(data.map { it.getOrElse(position){ defaultValue } })
            container.addView(view)
            return view
        }
        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }
    data.subscribeBy { list ->
        lastSubmitted = list
        adapter!!.notifyDataSetChanged()
        this.currentItem
    }.addTo(this.removed)
    showIndex.subscribeBy{ value ->
        this.currentItem = value
    }.addTo(this.removed)
    this.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(p0: Int) {}
        override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}
        override fun onPageSelected(p0: Int) {
            showIndex.onNext(p0)
        }
    })
}
