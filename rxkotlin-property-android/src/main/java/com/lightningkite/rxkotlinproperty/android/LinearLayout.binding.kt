//! This file will translate using Khrysalis.
package com.lightningkite.rxkotlinproperty.android

import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import com.lightningkite.rxkotlinproperty.Property
import com.lightningkite.rxkotlinproperty.StandardProperty
import com.lightningkite.rxkotlinproperty.subscribeBy
import com.lightningkite.rxkotlinproperty.until

private class LinearLayoutBoundSubview<T>(val view: View, val property: StandardProperty<T>)

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

fun <T> LinearLayout.bind(
    data: Property<List<T>>,
    defaultValue: T,
    makeView: (Property<T>) -> View
) {
    val existingViews: ArrayList<LinearLayoutBoundSubview<T>> = ArrayList()
    data.subscribeBy { value ->
        //Fix view count
        val excessViews = existingViews.size - value.size
        if(excessViews > 0){
            //remove views
            for(iter in 1 .. excessViews) {
                val old = existingViews.removeAt(existingViews.lastIndex)
                this.removeView(old.view)
            }
        } else if(existingViews.size < value.size) {
            //add views
            for(iter in 1 .. (-excessViews)) {
                val prop = StandardProperty(defaultValue)
                val view = makeView(prop)
                this.addView(view, LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
                existingViews.add(LinearLayoutBoundSubview(view, prop))
            }
        }

        //Update views
        for(index in value.indices){
            existingViews[index].property.value = value[index]
        }
    }.until(this.removed)
}


/**
 *
 * Binds all the subviews in the linear layout to the list of data in the property in the horizontal direction.
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
fun <T> LinearLayout.bindHorizontal(
    data: Property<List<T>>,
    defaultValue: T,
    makeView: (Property<T>) -> View
) {
    val existingViews: ArrayList<LinearLayoutBoundSubview<T>> = ArrayList()
    data.subscribeBy { value ->
        //Fix view count
        val excessViews = existingViews.size - value.size
        if(excessViews > 0){
            //remove views
            for(iter in 1 .. excessViews) {
                val old = existingViews.removeAt(existingViews.lastIndex)
                this.removeView(old.view)
            }
        } else if(existingViews.size < value.size) {
            //add views
            for(iter in 1 .. (-excessViews)) {
                val prop = StandardProperty(defaultValue)
                val view = makeView(prop)
                this.addView(view, LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT))
                existingViews.add(LinearLayoutBoundSubview(view, prop))
            }
        }

        //Update views
        for(index in value.indices){
            existingViews[index].property.value = value[index]
        }
    }.until(this.removed)
}

