package com.lightningkite.rx.android

import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy

/**
 * Filters the contents of this based on the inputs of the view and then displays
 * the filtered results in the drop down. Any selections made are passed to onItemSelected.
 *
 * Example:
 * val values = ValueSubject<List<String>(listOf("Hi", "Hello", "No")
 * val result = PublishSubject<String>()
 * values.showIn(autoCompleteView, result, { it })
 */
@JvmName("showInWithObserver")
fun <SOURCE: Observable<List<T>>, T: Any> SOURCE.showIn(
    autoCompleteTextView: AutoCompleteTextView,
    onItemSelected: Observer<T>,
    toString: (T) -> String = { it.toString() }
): SOURCE = showIn(autoCompleteTextView, { onItemSelected.onNext(it) }, toString)


/**
 * Filters the contents of this based on the inputs of the view and then displays
 * the filtered results in the drop down. Any selections made are passed to onItemSelected.
 *
 * Example:
 * val values = ValueSubject<List<String>(listOf("Hi", "Hello", "No")
 * val result = ""
 * values.showIn(autoCompleteView,{ result = it }, { it })
 */
fun <SOURCE: Observable<List<T>>, T> SOURCE.showIn(
    autoCompleteTextView: AutoCompleteTextView,
    onItemSelected: (T) -> Unit = { autoCompleteTextView.setText(toString(it)) },
    toString: (T) -> String = { it.toString() }
): SOURCE {
    var lastPublishedResults: List<T> = listOf()
    autoCompleteTextView.setAdapter(object : BaseAdapter(), Filterable {
        init {
            observeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).subscribeBy {
                val copy = it.toList()
                autoCompleteTextView.post {
                    lastPublishedResults = copy
                    notifyDataSetChanged()
                }
            }.addTo(autoCompleteTextView.removed)
        }

        override fun getFilter(): Filter = object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults = FilterResults().apply {
                this.values = lastPublishedResults
                this.count = lastPublishedResults.size
            }

            @Suppress("UNCHECKED_CAST")
            override fun convertResultToString(resultValue: Any?): CharSequence {
                return (resultValue as? T)?.let(toString) ?: ""
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                notifyDataSetChanged()
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val v = (convertView as? TextView) ?: TextView(autoCompleteTextView.context).apply {
                setTextColor(autoCompleteTextView.textColors)
                textSize = autoCompleteTextView.textSize / resources.displayMetrics.scaledDensity
                maxLines = autoCompleteTextView.maxLines
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    letterSpacing = autoCompleteTextView.letterSpacing
                }
                val size = (context.resources.displayMetrics.density * 8).toInt()
                setPadding(size, size, size, size)
            }
            v.text = lastPublishedResults.getOrNull(position)?.let(toString)
            return v
        }

        override fun getItem(position: Int): Any? = lastPublishedResults.getOrNull(position)
        override fun getItemId(position: Int): Long = position.toLong()
        override fun getCount(): Int = lastPublishedResults.size
    })
    autoCompleteTextView.setOnItemClickListener { _, _, index, _ ->
        lastPublishedResults.getOrNull(index)?.let(onItemSelected)
    }
    return this
}
