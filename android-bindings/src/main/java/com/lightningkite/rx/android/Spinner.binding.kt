package com.lightningkite.rx.android

import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.badoo.reaktive.disposable.addTo
import com.badoo.reaktive.observable.*
import com.badoo.reaktive.scheduler.mainScheduler
import com.badoo.reaktive.subject.Subject
import com.badoo.reaktive.subject.publish.PublishSubject

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
    combineLatest(selected, this@showIn.doOnAfterNext {
            val copy = it.toList()
            lastPublishedResults = copy
            adapter.notifyDataSetChanged()
        }) { sel: T, list: List<T> -> list.indexOf(sel) }
        .observeOn(mainScheduler).subscribe{ index ->
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
fun <SOURCE: Observable<List<T>>, T: Any> SOURCE.showInObservable(
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
                val event = PublishSubject<T>()
                spinner.spinnerTextStyle?.apply(this)
                event.switchMap(toString).observeOn(mainScheduler).subscribe {
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
    combineLatest(selected, this@showInObservable.doOnAfterNext {
            val copy = it.toList()
            lastPublishedResults = copy
            adapter.notifyDataSetChanged()
        }) { sel: T, list: List<T> -> list.indexOf(sel) }
        .observeOn(mainScheduler).subscribe { index ->
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
