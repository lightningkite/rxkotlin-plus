//! This file will translate using Khrysalis.
package com.lightningkite.rx.android

import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import com.badoo.reaktive.disposable.addTo
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.observeOn
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.subject.publish.PublishSubject

private class LinearLayoutBoundSubview<T>(val view: View, val property: PublishSubject<T>)

/**
 * Will display the contents of this in the LinearLayout using the makeView provided for each item.
 *
 * Example:
 * val data = ValueSubject<List<Int>>(listOf(1,2,3,4,5,6,7,8,9,0))
 * data.showIn(linearLayoutView) { obs -> ... return view }
 */
fun <SOURCE: Observable<List<T>>, T> SOURCE.showIn(
    linearLayout: LinearLayout,
    makeView: (Observable<T>) -> View
): SOURCE {
    val existingViews: ArrayList<LinearLayoutBoundSubview<T>> = ArrayList()
    observeOn(RequireMainThread).subscribe{ value: List<T> ->
        //Fix view count
        val excessViews = existingViews.size - value.size
        if (excessViews > 0) {
            //remove views
            for (iter in 1..excessViews) {
                val old = existingViews.removeAt(existingViews.lastIndex)
                linearLayout.removeView(old.view)
            }
        } else if (existingViews.size < value.size) {
            //add views
            for (iter in 1..(-excessViews)) {
                val prop = PublishSubject<T>()
                val v = makeView(prop)
                if(v.layoutParams == null) {
                    v.layoutParams = if(linearLayout.orientation == LinearLayout.VERTICAL)
                        LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                    else
                        LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
                }
                linearLayout.addView(v)
                existingViews.add(LinearLayoutBoundSubview(v, prop))
            }
        }

        //Update views
        for (index in value.indices) {
            existingViews[index].property.onNext(value[index])
        }
    }.addTo(linearLayout.removed)
    return this
}

