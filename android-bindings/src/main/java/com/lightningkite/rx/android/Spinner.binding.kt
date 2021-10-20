package com.lightningkite.rx.android

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.lightningkite.rx.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject


var butterflySpinnerRow: Int = android.R.layout.simple_spinner_item


/**
 * Sets the values displayed by the spinner to the contents of this and selections are
 * passed to selected.
 *
 * Example:
 * val values = ValueSubject<List<String>(listOf("Hi", "Hello", "No"))
 * val result = PublishSubject<String>()
 * values.showIn(spinnerView, result, { it })
 */
fun <SOURCE: Observable<List<T>>, T> SOURCE.showIn(
    view: Spinner,
    selected: Subject<T>,
    toString: (T) -> String = { it.toString() }
): SOURCE {
    var lastPublishedResults: List<T> = listOf()
    var suppressChange = false
    val adapter = (object : BaseAdapter() {
        @Suppress("UNCHECKED_CAST")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val v = (convertView as? TextView) ?: TextView(view.context).apply {
                view.spinnerTextStyle?.apply(this)
                setRemovedCondition(view.removed)
            }
            v.text = lastPublishedResults.getOrNull(position)?.let(toString)
            return v
        }

        override fun getItem(position: Int): Any? = lastPublishedResults.getOrNull(position)
        override fun getItemId(position: Int): Long = position.toLong()
        override fun getCount(): Int = lastPublishedResults.size
    })
    view.adapter = adapter
    Observable
        .combineLatest(selected, this@showIn.doOnNext {
            val copy = it.toList()
            lastPublishedResults = copy
            adapter.notifyDataSetChanged()
        }) { sel: T, list: List<T> -> list.indexOf(sel) }
        .subscribeBy { index ->
            if (index != -1 && index != view.selectedItemPosition && !suppressChange) {
                suppressChange = true
                view.setSelection(index)
                suppressChange = false
            }
        }
        .addTo(view.removed)
    view.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if(!suppressChange) {
                suppressChange = true
                val newValue = lastPublishedResults.getOrNull(position) ?: return
                selected.onNext(newValue)
                suppressChange = false
            }
        }
    }
    return this
}

/**
 * Sets the values displayed by the spinner to the contents of this and selections are
 * passed to selected.
 *
 * Example:
 * val values = ValueSubject<List<String>(listOf("Hi", "Hello", "No"))
 * val result = PublishSubject<String>()
 * values.showIn(spinnerView, result, { ValueSubject(it) })
 */
fun <SOURCE: Observable<List<T>>, T: Any> SOURCE.showInObservable(
    view: Spinner,
    selected: Subject<T>,
    toString: (T) -> Observable<String>
): SOURCE {
    var lastPublishedResults: List<T> = listOf()
    var suppressChange = false
    val adapter = (object : BaseAdapter() {
        @Suppress("UNCHECKED_CAST")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val v = (convertView as? TextView) ?: TextView(view.context).apply {
                val event = PublishSubject.create<T>()
                view.spinnerTextStyle?.apply(this)
                event.switchMap(toString).subscribeBy {
                    text = it
                }.addTo(removed)
                setRemovedCondition(view.removed)
                tag = event
            }
            lastPublishedResults.getOrNull(position)?.let {
                (v.tag as PublishSubject<T>).onNext(it)
            }
            return v
        }

        override fun getItem(position: Int): Any? = lastPublishedResults.getOrNull(position)
        override fun getItemId(position: Int): Long = position.toLong()
        override fun getCount(): Int = lastPublishedResults.size
    })
    view.adapter = adapter
    Observable
        .combineLatest(selected, this@showInObservable.doOnNext {
            val copy = it.toList()
            lastPublishedResults = copy
            adapter.notifyDataSetChanged()
        }) { sel: T, list: List<T> -> list.indexOf(sel) }
        .subscribeBy { index ->
            if (index != -1 && index != view.selectedItemPosition && !suppressChange) {
                suppressChange = true
                view.setSelection(index)
                suppressChange = false
            }
        }
        .addTo(view.removed)
    view.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if(!suppressChange) {
                suppressChange = true
                val newValue = lastPublishedResults.getOrNull(position) ?: return
                selected.onNext(newValue)
                suppressChange = false
            }
        }
    }
    return this
}
