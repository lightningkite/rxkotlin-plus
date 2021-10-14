//! This file will translate using Khrysalis.
package com.lightningkite.rx.android

import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject

private class LinearLayoutBoundSubview<T>(val view: View, val property: PublishSubject<T>)

/**
 *
 * Binds all the subviews in the linear layout to the list of data in the property.
 * makeView is the lambda that will return the view linked to an individual item in the
 * list.
 *
 * Example
 * val data = StandardProperty(listOf(1,2,3,4,5))
 * layout.bind(
 *  data = data,
 *  defaultValue = 0,
 *  makeView = { property ->
 *       val xml = ViewXml()
 *       val view = xml.setup(dependency)
 *       view.text.bindString(obs.map{it -> it.toString()})
 *       return view
 *       }
 * )
 */

fun <SOURCE: Observable<List<T>>, T : Any> SOURCE.showIn(
    view: LinearLayout,
    makeView: (Observable<T>) -> View
): SOURCE {
    val existingViews: ArrayList<LinearLayoutBoundSubview<T>> = ArrayList()
    subscribeBy { value ->
        //Fix view count
        val excessViews = existingViews.size - value.size
        if (excessViews > 0) {
            //remove views
            for (iter in 1..excessViews) {
                val old = existingViews.removeAt(existingViews.lastIndex)
                view.removeView(old.view)
            }
        } else if (existingViews.size < value.size) {
            //add views
            for (iter in 1..(-excessViews)) {
                val prop = PublishSubject.create<T>()
                val v = makeView(prop)
                if (view.orientation == LinearLayout.VERTICAL)
                    view.addView(v, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
                else
                    view.addView(v, LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT))
                existingViews.add(LinearLayoutBoundSubview(v, prop))
            }
        }

        //Update views
        for (index in value.indices) {
            existingViews[index].property.onNext(value[index])
        }
    }.addTo(view.removed)
    return this
}

