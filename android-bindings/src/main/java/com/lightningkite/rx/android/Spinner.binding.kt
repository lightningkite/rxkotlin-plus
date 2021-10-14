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

fun <T> Spinner.bind(
    options: Observable<List<T>>,
    selected: Subject<T>,
    toString: (T) -> String = { it.toString() }
) {
    var lastPublishedResults: List<T> = listOf()
    this.setAdapter(object : BaseAdapter() {
        init {
            options.subscribeBy {
                post {
                    lastPublishedResults = it.toList()
                    notifyDataSetChanged()
                }
            }.addTo(removed)
        }

        @Suppress("UNCHECKED_CAST")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = (convertView as? TextView) ?: TextView(context).apply {
                val subview = LayoutInflater.from(this@bind.context).inflate(butterflySpinnerRow, parent, false)
                val padding = (context.resources.displayMetrics.density * 8).toInt()
                subview.setPadding(padding,padding,padding,padding)
                val textView = subview.findViewById<TextView>(android.R.id.text1)
                textView.setTextColor(this@bind.spinnerTextColor)
                textView.textSize = this@bind.spinnerTextSize.toFloat()
                subview.setRemovedCondition(this@bind.removed)
                return subview
            }
            view.text = lastPublishedResults.getOrNull(position)?.let(toString)
            return view
        }

        override fun getItem(position: Int): Any? = lastPublishedResults.getOrNull(position)
        override fun getItemId(position: Int): Long = position.toLong()
        override fun getCount(): Int = lastPublishedResults.size
    })
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val newValue = lastPublishedResults.getOrNull(position) ?: return
            selected.onNext(newValue)
        }
    }
}

fun <T: Any> Spinner.bindObservable(
    options: Observable<List<T>>,
    selected: Subject<T>,
    toString: (T) -> Observable<String>
) {
    var lastPublishedResults: List<T> = listOf()
    this.setAdapter(object : BaseAdapter() {
        init {
            options.subscribeBy {
                post {
                    lastPublishedResults = it.toList()
                    notifyDataSetChanged()
                }
            }.addTo(removed)
        }

        @Suppress("UNCHECKED_CAST")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = (convertView as? TextView) ?: TextView(context).apply {
                val event = PublishSubject.create<T>()
                val subview = LayoutInflater.from(this@bindObservable.context).inflate(butterflySpinnerRow, parent, false)
                val padding = (context.resources.displayMetrics.density * 8).toInt()
                subview.setPadding(padding,padding,padding,padding)
                val textView = subview.findViewById<TextView>(android.R.id.text1)
                textView.setTextColor(this@bindObservable.spinnerTextColor)
                textView.textSize = this@bindObservable.spinnerTextSize.toFloat()
                event.flatMap(toString).subscribeBy {
                    textView.text = it
                }.addTo(removed)
                subview.setRemovedCondition(this@bindObservable.removed)
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
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val newValue = lastPublishedResults.getOrNull(position) ?: return
            selected.onNext(newValue)
        }
    }
}
