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

fun <SOURCE: Observable<List<T>>, T> SOURCE.showIn(
    view: Spinner,
    selected: Subject<T>,
    toString: (T) -> String = { it.toString() }
): SOURCE {
    var lastPublishedResults: List<T> = listOf()
    view.setAdapter(object : BaseAdapter() {
        init {
            subscribeBy {
                val copy = it.toList()
                view.post {
                    lastPublishedResults = copy
                    notifyDataSetChanged()
                }
            }.addTo(view.removed)
        }

        @Suppress("UNCHECKED_CAST")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = (convertView as? TextView) ?: TextView(view.context).apply {
                val subview = LayoutInflater.from(view.context).inflate(butterflySpinnerRow, parent, false)
                val padding = (context.resources.displayMetrics.density * 8).toInt()
                subview.setPadding(padding,padding,padding,padding)
                val textView = subview.findViewById<TextView>(android.R.id.text1)
                textView.setTextColor(view.spinnerTextColor)
                textView.textSize = view.spinnerTextSize.toFloat()
                subview.setRemovedCondition(view.removed)
                return subview
            }
            view.text = lastPublishedResults.getOrNull(position)?.let(toString)
            return view
        }

        override fun getItem(position: Int): Any? = lastPublishedResults.getOrNull(position)
        override fun getItemId(position: Int): Long = position.toLong()
        override fun getCount(): Int = lastPublishedResults.size
    })
    view.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val newValue = lastPublishedResults.getOrNull(position) ?: return
            selected.onNext(newValue)
        }
    }
    return this
}

fun <SOURCE: Observable<List<T>>, T: Any> SOURCE.showInObservable(
    view: Spinner,
    selected: Subject<T>,
    toString: (T) -> Observable<String>
): SOURCE {
    var lastPublishedResults: List<T> = listOf()
    view.setAdapter(object : BaseAdapter() {
        init {
            subscribeBy {
                val copy = it.toList()
                view.post {
                    lastPublishedResults = copy
                    notifyDataSetChanged()
                }
            }.addTo(view.removed)
        }

        @Suppress("UNCHECKED_CAST")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = (convertView as? TextView) ?: TextView(view.context).apply {
                val event = PublishSubject.create<T>()
                val subview = LayoutInflater.from(view.context).inflate(butterflySpinnerRow, parent, false)
                val padding = (context.resources.displayMetrics.density * 8).toInt()
                subview.setPadding(padding,padding,padding,padding)
                val textView = subview.findViewById<TextView>(android.R.id.text1)
                textView.setTextColor(view.spinnerTextColor)
                textView.textSize = view.spinnerTextSize.toFloat()
                event.switchMap(toString).subscribeBy {
                    textView.text = it
                }.addTo(removed)
                subview.setRemovedCondition(view.removed)
                subview.tag = event
                return subview
            }
            lastPublishedResults.getOrNull(position)?.let {
                (view.tag as PublishSubject<T>).onNext(it)
            }
            return view
        }

        override fun getItem(position: Int): Any? = lastPublishedResults.getOrNull(position)
        override fun getItemId(position: Int): Long = position.toLong()
        override fun getCount(): Int = lastPublishedResults.size
    })
    view.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val newValue = lastPublishedResults.getOrNull(position) ?: return
            selected.onNext(newValue)
        }
    }
    return this
}
