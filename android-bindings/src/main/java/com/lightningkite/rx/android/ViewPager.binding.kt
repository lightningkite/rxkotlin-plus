package com.lightningkite.rx.android

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.lightningkite.rx.*
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
fun <T: Any> Observable<List<T>>.showIn(
    view: ViewPager,
    showIndex: Subject<Int> = ValueSubject(0),
    makeView: (Observable<T>)->View
)  {
    var lastSubmitted = listOf<T>()
    view.adapter = object : PagerAdapter() {
        override fun isViewFromObject(p0: View, p1: Any): Boolean = p1 == p0
        override fun getCount(): Int = lastSubmitted.size
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val v = makeView(this@showIn.mapNotNull { it.getOrNull(position) })
            container.addView(v)
            return v
        }
        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }
    subscribeBy { list ->
        lastSubmitted = list
        view.adapter!!.notifyDataSetChanged()
        view.currentItem
    }.addTo(view.removed)
    showIndex.subscribeBy{ value ->
        view.currentItem = value
    }.addTo(view.removed)
    view.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
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
fun <T: Any> List<T>.showIn(
    view: ViewPager,
    showIndex: Subject<Int> = ValueSubject(0),
    makeView: (T)->View
)  {
    view.adapter = object : PagerAdapter() {
        override fun isViewFromObject(p0: View, p1: Any): Boolean = p1 == p0
        override fun getCount(): Int = this@showIn.size
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val v = makeView(this@showIn.get(position))
            container.addView(v)
            return v
        }
        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }
    showIndex.subscribeBy{ value ->
        view.currentItem = value
    }.addTo(view.removed)
    view.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(p0: Int) {}
        override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}
        override fun onPageSelected(p0: Int) {
            showIndex.onNext(p0)
        }
    })
}
