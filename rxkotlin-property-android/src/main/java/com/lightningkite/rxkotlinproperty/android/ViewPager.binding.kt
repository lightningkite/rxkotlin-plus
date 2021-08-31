package com.lightningkite.rxkotlinproperty.android

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.lightningkite.rxkotlinproperty.*

/**
 *
 * Binds the items in the ViewPager to the list provided, and the showing index to the property provided.
 * Any changes to the property will change the current page. AS well updating the pager will update the property.
 *
 */
fun <T> ViewPager.bind(
    items: List<T>,
    showIndex: MutableProperty<Int> = StandardProperty(0),
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
    }.until(this.removed)
    this.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(p0: Int) {}
        override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}
        override fun onPageSelected(p0: Int) {
            showIndex.value = p0
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
    data: Property<List<T>>,
    defaultValue: T,
    showIndex: MutableProperty<Int> = StandardProperty(0),
    makeView: (Property<T>)->View
) {
    adapter = object : PagerAdapter() {

        override fun isViewFromObject(p0: View, p1: Any): Boolean = p1 == p0

        override fun getCount(): Int = data.value.size

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
        adapter!!.notifyDataSetChanged()
        this.currentItem
    }.until(this.removed)
    showIndex.subscribeBy{ value ->
        this.currentItem = value
    }.until(this.removed)
    this.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(p0: Int) {}
        override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}
        override fun onPageSelected(p0: Int) {
            showIndex.value = p0
        }
    })
}
