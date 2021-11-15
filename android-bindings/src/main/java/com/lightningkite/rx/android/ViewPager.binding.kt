package com.lightningkite.rx.android

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
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
fun <T: Any> Observable<List<T>>.showIn(
    viewPager: ViewPager,
    showIndex: Subject<Int> = ValueSubject(0),
    makeView: (Observable<T>)->View
)  {
    var lastSubmitted = listOf<T>()
    viewPager.adapter = object : PagerAdapter() {
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
        viewPager.adapter!!.notifyDataSetChanged()
        viewPager.currentItem
    }.addTo(viewPager.removed)
    showIndex.subscribeBy{ value ->
        viewPager.currentItem = value
    }.addTo(viewPager.removed)
    viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(p0: Int) {}
        override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}
        override fun onPageSelected(p0: Int) {
            showIndex.onNext(p0)
        }
    })
}

/**
 * Will display the contents of this in the ViewPager using the makeView provided for each item.
 * The page shown is tied to the showIndex Subject.
 *
 * Example:
 * val data = listOf(1,2,3,4,5,6,7,8,9,0)
 * val showing = ValueSubject(0)
 * data.showIn(viewPagerView, showing) { obs -> ... return view }
 */
fun <T: Any> List<T>.showIn(
    viewPager: ViewPager,
    showIndex: Subject<Int> = ValueSubject(0),
    makeView: (T)->View
)  {
    viewPager.adapter = object : PagerAdapter() {
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
        viewPager.currentItem = value
    }.addTo(viewPager.removed)
    viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(p0: Int) {}
        override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}
        override fun onPageSelected(p0: Int) {
            showIndex.onNext(p0)
        }
    })
}
