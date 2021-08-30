package com.lightningkite.rxkotlinproperty.android

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.Spinner
import android.widget.TextView
import com.lightningkite.rxkotlinproperty.*


/**
 *
 * Binds the available options to the options Observable
 * Binds the selected item to the selected observable.
 * makeView is a lambda that return the view for each item in the drop down.
 *
 */

@JvmName("bindComplex")
@Deprecated("Doesn't translate to web")
fun <T> Spinner.bind(
    options: Property<List<T>>,
    selected: MutableProperty<T>,
    makeView: (Property<T>) -> View
) {
    adapter = object : BaseAdapter() {
        init {
            options.subscribeBy { _ ->
                this.notifyDataSetChanged()
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: run {
                val event = StandardProperty<T>(
                    options.value.getOrNull(position) ?: selected.value
                )
                val subview = makeView(event)
                subview.setRemovedCondition(this@bind.removed)
                subview.tag = event
                return subview
            }
            (view.tag as? StandardProperty<T>)?.let {
                it.value = options.value.getOrNull(position) ?: selected.value
            } ?: throw IllegalStateException()
            return view
        }

        override fun getItem(position: Int): Any? = options.value.getOrNull(position)
        override fun getItemId(position: Int): Long = position.toLong()
        override fun getCount(): Int = options.value.size
    }
    selected.subscribeBy { it ->
        val index = options.value.indexOf(it)
        if (index != -1 && index != selectedItemPosition) {
            setSelection(index)
        }
    }.until(this.removed)
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val newValue = options.value.getOrNull(position) ?: return
            if (selected.value != newValue) {
                selected.value = newValue
            }
        }
    }
}

var butterflySpinnerRow: Int = android.R.layout.simple_spinner_item

fun <T> Spinner.bind(
    options: Property<List<T>>,
    selected: MutableProperty<T>,
    toString: (T) -> String = { it.toString() }
) {
    adapter = object : BaseAdapter() {
        init {
            options.subscribeBy { _ ->
                this.notifyDataSetChanged()
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: run {
                val event = StandardProperty<T>(
                    options.value.getOrNull(position) ?: selected.value
                )
                val subview = LayoutInflater.from(this@bind.context).inflate(butterflySpinnerRow, parent, false)
                subview.setRemovedCondition(this@bind.removed)
                val padding = (context.resources.displayMetrics.density * 8).toInt()
                subview.setPadding(padding,padding,padding,padding)
                val textView = subview.findViewById<TextView>(android.R.id.text1)
                textView.setTextColor(this@bind.spinnerTextColor)
                textView.textSize = this@bind.spinnerTextSize.toFloat()
                textView.bindString(event.map(toString))
                subview.tag = event
                return subview
            }
            (view.tag as? StandardProperty<T>)?.let {
                it.value = options.value.getOrNull(position) ?: selected.value
            } ?: throw IllegalStateException()
            return view
        }

        override fun getItem(position: Int): Any? = options.value.getOrNull(position)
        override fun getItemId(position: Int): Long = position.toLong()
        override fun getCount(): Int = options.value.size
    }
    selected.subscribeBy { it ->
        val index = options.value.indexOf(it)
        if (index != -1 && index != selectedItemPosition) {
            setSelection(index)
        }
    }.until(this.removed)
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val newValue = options.value.getOrNull(position) ?: return
            if (selected.value != newValue) {
                selected.value = newValue
            }
        }
    }
}


fun <T> Spinner.bindString(
    options: Property<List<T>>,
    selected: MutableProperty<T>,
    toString: (T) -> Property<String>
) {
    adapter = object : BaseAdapter() {
        init {
            options.subscribeBy { _ ->
                this.notifyDataSetChanged()
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: run {
                val event = StandardProperty<T>(
                    options.value.getOrNull(position) ?: selected.value
                )
                val subview = LayoutInflater.from(this@bindString.context).inflate(butterflySpinnerRow, parent, false)
                val padding = (context.resources.displayMetrics.density * 8).toInt()
                subview.setPadding(padding,padding,padding,padding)
                val textView = subview.findViewById<TextView>(android.R.id.text1)
                textView.setTextColor(this@bindString.spinnerTextColor)
                textView.textSize = this@bindString.spinnerTextSize.toFloat()
                textView.bindString(event.flatMap(toString))
                subview.tag = event
                subview.setRemovedCondition(this@bindString.removed)
                return subview
            }
            (view.tag as? StandardProperty<T>)?.let {
                it.value = options.value.getOrNull(position) ?: selected.value
            } ?: throw IllegalStateException()
            return view
        }

        override fun getItem(position: Int): Any? = options.value.getOrNull(position)
        override fun getItemId(position: Int): Long = position.toLong()
        override fun getCount(): Int = options.value.size
    }
    selected.subscribeBy { it ->
        val index = options.value.indexOf(it)
        if (index != -1 && index != selectedItemPosition) {
            setSelection(index)
        }
    }.until(this.removed)
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val newValue = options.value.getOrNull(position) ?: return
            if (selected.value != newValue) {
                selected.value = newValue
            }
        }
    }
}
