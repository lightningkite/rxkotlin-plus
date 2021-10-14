package com.lightningkite.rx.android

import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.viewpager.widget.ViewPager
import com.lightningkite.rx.ValueSubject
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.Subject

fun <SOURCE: Observable<List<T>>, T> SOURCE.showIn(
    view: AutoCompleteTextView,
    onItemSelected: (T) -> Unit = { view.setText(toString(it)) },
    toString: (T) -> String,
): SOURCE {
    var lastPublishedResults: List<T> = listOf()
    view.setAdapter(object : BaseAdapter(), Filterable {
        init {
            subscribeBy {
                val copy = it.toList()
                view.post {
                    lastPublishedResults = copy
                    notifyDataSetChanged()
                }
            }.addTo(view.removed)
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
            val v = (convertView as? TextView) ?: TextView(view.context).apply {
                setTextColor(view.textColors)
                textSize = view.textSize / resources.displayMetrics.scaledDensity
                maxLines = view.maxLines
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    letterSpacing = view.letterSpacing
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
    view.setOnItemClickListener { adapterView, v, index, id ->
        lastPublishedResults.getOrNull(index)?.let(onItemSelected)
    }
    return this
}
