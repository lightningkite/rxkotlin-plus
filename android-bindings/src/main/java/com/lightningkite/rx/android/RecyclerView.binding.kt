package com.lightningkite.rx.android

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.subjects.BehaviorSubject


private fun RecyclerView.defaultLayoutManager(){
    if(layoutManager == null) {
        layoutManager = object : LinearLayoutManager(context) {
            override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
                return super.generateDefaultLayoutParams().apply {
                    width = MATCH_PARENT
                }
            }
        }
    }
}

internal open class ObservableRVA<T: Any>(
    val removeCondition: CompositeDisposable,
    val determineType: (T)->Int,
    val makeView: (Int, Observable<T>) -> View
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var lastPublished: List<T> = listOf()

    override fun getItemViewType(position: Int): Int {
        return determineType(lastPublished[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val event = BehaviorSubject.create<T>()
        val subview = makeView(viewType, event)
        subview.setRemovedCondition(removeCondition)
        subview.tag = event
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


/**
 * Will display the contents of this in the RecyclerView using the makeView provided for each item.
 *
 * Example:
 * val data = ValueSubject<List<Int>>(listOf(1,2,3,4,5,6,7,8,9,0))
 * data.showIn(recyclerView) { obs -> ... return view }
 */
fun <SOURCE: Observable<out List<T>>, T: Any> SOURCE.showIn(
    recyclerView: RecyclerView,
    makeView: (Observable<T>) -> View
): SOURCE {
    recyclerView.defaultLayoutManager()
    recyclerView.adapter = object : ObservableRVA<T>(recyclerView.removed, { 0 }, { _, obs -> makeView(obs) }) {
        init {
            observeOn(RequireMainThread).subscribeBy { it ->
                val new = it.toList()
                lastPublished = new
                this.notifyDataSetChanged()
            }.addTo(recyclerView.removed)
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
fun <SOURCE: Observable<out List<T>>, T: Any> SOURCE.showIn(
    recyclerView: RecyclerView,
    determineType: (T)->Int,
    makeView: (Int, Observable<T>) -> View
): SOURCE {
    recyclerView.defaultLayoutManager()
    recyclerView.adapter = object : ObservableRVA<T>(recyclerView.removed, determineType, makeView) {
        init {
            observeOn(RequireMainThread).subscribeBy { it ->
                val new = it.toList()
                lastPublished = new
                this.notifyDataSetChanged()
            }.addTo(recyclerView.removed)
        }
    }
    return this
}


/**
 * Will display the contents of this in the RecyclerView using the makeView provided for each item.
 *
 * Example:
 * val data = ValueSubject<List<Int>>(listOf(1,2,3,4,5,6,7,8,9,0))
 * data.showIn(recyclerView) { obs -> ... return view }
 */
fun <SOURCE: Observable<out List<T>>, T: Any, ID: Any> SOURCE.showIn(
    recyclerView: RecyclerView,
    getId: (T)->ID,
    makeView: (Observable<T>) -> View
): SOURCE {
    recyclerView.defaultLayoutManager()
    recyclerView.adapter = object : ObservableRVA<T>(recyclerView.removed, { 0 }, { _, obs -> makeView(obs) }) {
        init {
            observeOn(RequireMainThread).subscribeBy { it ->
                val old = lastPublished
                val new = it.toList()
                lastPublished = new
                DiffUtil.calculateDiff(object: DiffUtil.Callback() {
                    override fun getOldListSize(): Int = old.size
                    override fun getNewListSize(): Int = new.size
                    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = old[oldItemPosition].let(getId) == new[newItemPosition].let(getId)
                    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = old[oldItemPosition] == new[newItemPosition]
                }).dispatchUpdatesTo(this)
            }.addTo(recyclerView.removed)
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
fun <SOURCE: Observable<out List<T>>, T: Any, ID: Any> SOURCE.showIn(
    recyclerView: RecyclerView,
    getId: (T)->ID,
    determineType: (T)->Int,
    makeView: (Int, Observable<T>) -> View
): SOURCE {
    recyclerView.defaultLayoutManager()
    recyclerView.adapter = object : ObservableRVA<T>(recyclerView.removed, determineType, makeView) {
        init {
            observeOn(RequireMainThread).subscribeBy { it ->
                val old = lastPublished
                val new = it.toList()
                lastPublished = new
                DiffUtil.calculateDiff(object: DiffUtil.Callback() {
                    override fun getOldListSize(): Int = old.size
                    override fun getNewListSize(): Int = new.size
                    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = old[oldItemPosition].let(getId) == new[newItemPosition].let(getId)
                    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = old[oldItemPosition] == new[newItemPosition]
                }).dispatchUpdatesTo(this)
            }.addTo(recyclerView.removed)
        }
    }
    return this
}

