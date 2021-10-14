package com.lightningkite.rx.android

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lightningkite.rx.*
import io.reactivex.rxjava3.core.Observable
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
 * recycler.bind(
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

fun <T: Any> RecyclerView.bind(
    data: Observable<List<T>>,
    makeView: (Observable<T>) -> View
) {
    defaultLayoutManager()
    var lastPublished: List<T> = listOf()
    adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        init {
            data.subscribeBy { it ->
                lastPublished = it
                this.notifyDataSetChanged()
            }.addTo(this@bind.removed)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val event = PublishSubject.create<T>()
            val subview = makeView(event)
            subview.setRemovedCondition(this@bind.removed)
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
}

fun <T: Any> RecyclerView.bindMulti(
    data: Observable<List<T>>,
    determineType: (T)->Int,
    makeView: (Int, Observable<T>) -> View
) {
    defaultLayoutManager()
    var lastPublished: List<T> = listOf()
    adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        init {
            data.subscribeBy { it ->
                lastPublished = it
                this.notifyDataSetChanged()
            }.addTo(this@bindMulti.removed)
        }

        override fun getItemViewType(position: Int): Int {
            return determineType(lastPublished[position])
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val event = PublishSubject.create<T>()
            val subview = makeView(viewType, event)
            subview.setRemovedCondition(this@bindMulti.removed)
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
}


/**
 *
 *
 *
 */

fun RecyclerView.bindRefresh(
    loading: Observable<Boolean>,
    refresh: () -> Unit
) {
    (this.parent as? SwipeRefreshLayout)?.run {
        loading.subscribeBy { value ->
            this.post {
                this.isRefreshing = value
            }
        }.addTo(this@bindRefresh.removed)
        setOnRefreshListener {
            refresh()
        }
    }
}

