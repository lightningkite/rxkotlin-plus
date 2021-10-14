package com.lightningkite.rx.android

import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy


fun <T> AutoCompleteTextView.bind(
    options: Observable<List<T>>,
    toString: (T) -> String,
    onItemSelected: (T) -> Unit
) {
    var lastPublishedResults: List<T> = listOf()
    this.setAdapter(object : BaseAdapter(), Filterable {
        init {
            options.subscribeBy {
                post {
                    lastPublishedResults = it.toList()
                    notifyDataSetChanged()
                }
            }.addTo(removed)
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
            val view = (convertView as? TextView) ?: TextView(context).apply {
                setTextColor(this@bind.textColors)
                textSize = this@bind.textSize / resources.displayMetrics.scaledDensity
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    maxLines = this@bind.maxLines
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    letterSpacing = this@bind.letterSpacing
                }
                val size = (context.resources.displayMetrics.density * 8).toInt()
                setPadding(size, size, size, size)
            }
            view.text = lastPublishedResults.getOrNull(position)?.let(toString)
            return view
        }

        override fun getItem(position: Int): Any? = lastPublishedResults.getOrNull(position)
        override fun getItemId(position: Int): Long = position.toLong()
        override fun getCount(): Int = lastPublishedResults.size
    })
    this.setOnItemClickListener { adapterView, view, index, id ->
        lastPublishedResults.getOrNull(index)?.let(onItemSelected)
    }
}
