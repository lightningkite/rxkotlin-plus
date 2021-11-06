package com.lightningkite.rx.android

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.subjects.BehaviorSubject


private fun RecyclerView.defaultLayoutManager(){
    if(layoutManager == null) {
        layoutManager = LinearLayoutManager(context)
    }
}


/**
 * Will display the contents of this in the RecyclerView using the makeView provided for each item.
 *
 * Example:
 * val data = ValueSubject<List<Int>>(listOf(1,2,3,4,5,6,7,8,9,0))
 * data.showIn(recyclerView) { obs -> ... return view }
 */
fun <SOURCE: Observable<List<T>>, T: Any> SOURCE.showIn(
    recyclerView: RecyclerView,
    makeView: (Observable<T>) -> View
): SOURCE {
    recyclerView.defaultLayoutManager()
    var lastPublished: List<T> = listOf()
    recyclerView.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        init {
            subscribeBy { it ->
                lastPublished = it
                this.notifyDataSetChanged()
            }.addTo(recyclerView.removed)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val event = BehaviorSubject.create<T>()
            val subview = makeView(event)
            subview.setRemovedCondition(recyclerView.removed)
            subview.tag = event
            subview.layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            return object : RecyclerView.ViewHolder(subview) {}
        }

        override fun getItemCount(): Int = lastPublished.size

        @Suppress("UNCHECKED_CAST")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder.itemView.tag as? BehaviorSubject<T>)?.onNext(lastPublished[position]) ?: run {
                println("Failed to find property to update")
            }
        }
    }
    return this
}


/**
 * Will display the contents of this in the RecyclerView.
 * determineType is meant to map the object to an Int, which is passed into the makeView.
 * makeView is meant to create different views for the item based on the int provided.
 *
 * Example:
 * val data = ValueSubject<List<Int>>(listOf(1,2,3,4,5,6,7,8,9,0))
 * data.showIn(recyclerView, {item -> if(item < 5) 0 else 1 } ) { type, obs -> ... return view }
 */
fun <SOURCE: Observable<List<T>>, T: Any> SOURCE.showIn(
    recyclerView: RecyclerView,
    determineType: (T)->Int,
    makeView: (Int, Observable<T>) -> View
): SOURCE {
    recyclerView.defaultLayoutManager()
    var lastPublished: List<T> = listOf()
    recyclerView.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        init {
            subscribeBy { it ->
                lastPublished = it
                this.notifyDataSetChanged()
            }.addTo(recyclerView.removed)
        }

        override fun getItemViewType(position: Int): Int {
            return determineType(lastPublished[position])
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val event = BehaviorSubject.create<T>()
            val subview = makeView(viewType, event)
            subview.setRemovedCondition(recyclerView.removed)
            subview.tag = event
            subview.layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            return object : RecyclerView.ViewHolder(subview) {}
        }

        override fun getItemCount(): Int = lastPublished.size

        @Suppress("UNCHECKED_CAST")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder.itemView.tag as? BehaviorSubject<T>)?.onNext(lastPublished[position]) ?: run {
                println("Failed to find property to update")
            }
        }
    }
    return this
}

