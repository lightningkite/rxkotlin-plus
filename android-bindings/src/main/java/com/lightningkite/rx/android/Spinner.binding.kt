package com.lightningkite.rx.android

import android.view.View
import android.view.ViewGroup
import android.widget.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
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
fun <SOURCE: Observable<out List<T>>, T> SOURCE.showIn(
    spinner: Spinner,
    selected: Subject<T>,
    toString: (T) -> String = { it.toString() }
): SOURCE {
    var lastPublishedResults: List<T> = listOf()
    var suppressChange = false
    val adapter = (object : BaseAdapter() {
        @Suppress("UNCHECKED_CAST")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val v = (convertView as? TextView) ?: TextView(spinner.context).apply {
                spinner.spinnerTextStyle?.apply(this)
                setRemovedCondition(spinner.removed)
            }
            v.text = lastPublishedResults.getOrNull(position)?.let(toString)
            return v
        }

        override fun getItem(position: Int): Any? = lastPublishedResults.getOrNull(position)
        override fun getItemId(position: Int): Long = position.toLong()
        override fun getCount(): Int = lastPublishedResults.size
    })
    spinner.adapter = adapter
    Observable
        .combineLatest(selected, this@showIn.doOnNext {
            val copy = it.toList()
            lastPublishedResults = copy
            adapter.notifyDataSetChanged()
        }) { sel: T, list: List<T> -> list.indexOf(sel) }
        .observeOn(RequireMainThread).subscribeBy { index ->
            if (index != -1 && index != spinner.selectedItemPosition && !suppressChange) {
                suppressChange = true
                spinner.setSelection(index)
                suppressChange = false
            }
        }
        .addTo(spinner.removed)
    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
fun <SOURCE: Observable<out List<T>>, T: Any> SOURCE.showInObservable(
    spinner: Spinner,
    selected: Subject<T>,
    toString: (T) -> Observable<String>
): SOURCE {
    var lastPublishedResults: List<T> = listOf()
    var suppressChange = false
    val adapter = (object : BaseAdapter() {
        @Suppress("UNCHECKED_CAST")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val v = (convertView as? TextView) ?: TextView(spinner.context).apply {
                val event = PublishSubject.create<T>()
                spinner.spinnerTextStyle?.apply(this)
                event.switchMap(toString).observeOn(RequireMainThread).subscribeBy {
                    text = it
                }.addTo(removed)
                setRemovedCondition(spinner.removed)
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
    spinner.adapter = adapter
    Observable
        .combineLatest(selected, this@showInObservable.doOnNext {
            val copy = it.toList()
            lastPublishedResults = copy
            adapter.notifyDataSetChanged()
        }) { sel: T, list: List<T> -> list.indexOf(sel) }
        .observeOn(RequireMainThread).subscribeBy { index ->
            if (index != -1 && index != spinner.selectedItemPosition && !suppressChange) {
                suppressChange = true
                spinner.setSelection(index)
                suppressChange = false
            }
        }
        .addTo(spinner.removed)
    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
