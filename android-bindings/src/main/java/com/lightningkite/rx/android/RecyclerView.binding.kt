package com.lightningkite.rx.android

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.jakewharton.rxbinding4.internal.checkMainThread
import com.jakewharton.rxbinding4.swiperefreshlayout.refreshes
import com.lightningkite.rx.*
import io.reactivex.rxjava3.android.MainThreadDisposable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlin.reflect.KClass
import io.reactivex.rxjava3.kotlin.addTo


private fun RecyclerView.defaultLayoutManager(){
    if(layoutManager == null) {
        layoutManager = LinearLayoutManager(context)
    }
}

/**
 *
 * Binds the data in the RecyclerView to the data provided by the property.
 * makeView is the lambda that creates the view tied to each item in the list of data.
 *
 * Example
 * val data = StandardProperty(listOf(1,2,3,4,5))
 * data.showIn(recyclerView) { property ->
 *       val xml = ViewXml()
 *       val view = xml.setup(dependency)
 *       view.text.bindString(obs.map{it -> it.toString()})
 *       return view
 * }
 */

fun <SOURCE: Observable<List<T>>, T: Any> SOURCE.showIn(
    view: RecyclerView,
    makeView: (Observable<T>) -> View
): SOURCE {
    view.defaultLayoutManager()
    var lastPublished: List<T> = listOf()
    view.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        init {
            subscribeBy { it ->
                lastPublished = it
                this.notifyDataSetChanged()
            }.addTo(view.removed)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val event = PublishSubject.create<T>()
            val subview = makeView(event)
            subview.setRemovedCondition(view.removed)
            subview.tag = event
            subview.layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            return object : RecyclerView.ViewHolder(subview) {}
        }

        override fun getItemCount(): Int = lastPublished.size

        @Suppress("UNCHECKED_CAST")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder.itemView.tag as? PublishSubject<T>)?.onNext(lastPublished[position]) ?: run {
                println("Failed to find property to update")
            }
        }
    }
    return this
}

fun <SOURCE: Observable<List<T>>, T: Any> SOURCE.showIn(
    view: RecyclerView,
    determineType: (T)->Int,
    makeView: (Int, Observable<T>) -> View
): SOURCE {
    view.defaultLayoutManager()
    var lastPublished: List<T> = listOf()
    view.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        init {
            subscribeBy { it ->
                lastPublished = it
                this.notifyDataSetChanged()
            }.addTo(view.removed)
        }

        override fun getItemViewType(position: Int): Int {
            return determineType(lastPublished[position])
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val event = PublishSubject.create<T>()
            val subview = makeView(viewType, event)
            subview.setRemovedCondition(view.removed)
            subview.tag = event
            subview.layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            return object : RecyclerView.ViewHolder(subview) {}
        }

        override fun getItemCount(): Int = lastPublished.size

        @Suppress("UNCHECKED_CAST")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder.itemView.tag as? PublishSubject<T>)?.onNext(lastPublished[position]) ?: run {
                println("Failed to find property to update")
            }
        }
    }
    return this
}

