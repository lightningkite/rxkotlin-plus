package com.lightningkite.rxkotlinproperty.android

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.Button
import com.lightningkite.rxkotlinproperty.MutableProperty
import com.lightningkite.rxkotlinproperty.Property
import com.lightningkite.rxkotlinproperty.subscribeBy
import com.lightningkite.rxkotlinproperty.until
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*


/**
 *
 * Bind isEnabled with the provided observable<Boolean>. This will turn the button on and off, or allow it to be tapped,
 * according to the value in the observable. As well you can provide a color resource for each state.
 * The colors will set the background color as it changes. By default colors don't change.
 *
 * Example
 * val active = StandardObservableProperty<Boolean>(true)
 * button.bindSelect(active, R.color.blue, R.color.grey)
 * If active is true the button can be clicked, otherwise it is not enabled, and cannot be clicked.
 * when active the button will be the blue color, otherwise the button will be grey.
 */
fun Button.bindActive(
    observable: Property<Boolean>,
    activeColorResource: ColorResource? = null,
    inactiveColorResource: ColorResource? = null
) {
    observable.subscribeBy { it ->
        this.isEnabled = it
        if (it) {
            activeColorResource?.let { color ->
                this.setBackgroundResource(color)
            }
        } else {
            inactiveColorResource?.let { color ->
                this.setBackgroundResource(color)
            }
        }
    }.until(this.removed)
}

/**
 *
 * Bind Active when provided an property<Boolean> will turn the button on and off, or allow it to be tapped,
 * according to the value in the property. As well you can provide a drawable for each state.
 * The background drawable will change as the state does. By Default drawable doesn't change.
 *
 *  * Example
 * val active = StandardProperty<Boolean>(true)
 * button.bindSelect(active, R.drawable.blue_border, R.drawable.grey_border)
 * If active is true the button can be clicked, otherwise it is not enabled, and cannot be clicked.
 * when active the button background will be the blue border, otherwise the button will be the grey border.
 */
fun Button.bindActive(
    date: Property<Boolean>,
    activeBackground: Drawable,
    inactiveBackground: Drawable
) {
    date.subscribeBy { it ->
        this.isEnabled = it
        if (it) {
            this.background = activeBackground
        } else {
            this.background = inactiveBackground
        }
    }.until(this.removed)
}


fun Button.bindDate(
    date: MutableProperty<Date>,
) {
    val formatter = DateFormat.getDateInstance(DateFormat.SHORT)
    date.subscribeBy {
        this.text = formatter.format(it)
    }.until(removed)
    this.setOnClickListener {
        context.dateSelectorDialog(date.value) {
            date.value = it
        }
    }
}

fun Button.bindDate(
    date: MutableProperty<Date?>,
    minuteInterval: Int = 1,
    nullText: String
) {
    val formatter = DateFormat.getDateInstance(DateFormat.SHORT)
    date.subscribeBy {
        this.text = it?.let { formatter.format(it) } ?: nullText
    }.until(removed)

    this.setOnClickListener {
        context.dateSelectorDialog(date.value ?: Date()) {
            date.value = it
        }
    }
}

fun Button.bindTime(
    date: MutableProperty<Date>,
    minuteInterval: Int = 1
) {
    val formatter = DateFormat.getTimeInstance(DateFormat.SHORT)
    date.subscribeBy {
        this.text = formatter.format(it)
    }.until(removed)
    this.setOnClickListener {
        context.timeSelectorDialog(date.value, minuteInterval) {
            date.value = it
        }
    }
}

fun Button.bindTime(
    date: MutableProperty<Date?>,
    minuteInterval: Int = 1,
    nullText: String
) {
    val formatter = DateFormat.getTimeInstance(DateFormat.SHORT)
    date.subscribeBy {
        this.text = it?.let { formatter.format(it) } ?: nullText
    }.until(removed)

    this.setOnClickListener {
        context.timeSelectorDialog(date.value ?: Date(), minuteInterval) {
            date.value = it
        }
    }
}


fun Button.bindDateTime(
    date: MutableProperty<Date>,
    minuteInterval: Int = 1
) {
    val formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
    date.subscribeBy {
        this.text = formatter.format(it)
    }.until(removed)

    this.setOnClickListener {
        context.dateSelectorDialog(date.value) {
            context.timeSelectorDialog(it, minuteInterval) {
                date.value = it
            }
        }
    }
}

fun Button.bindDateTime(
    date: MutableProperty<Date?>,
    minuteInterval: Int = 1,
    nullText: String
) {
    val formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
    date.subscribeBy {
        this.text = it?.let { formatter.format(it) } ?: nullText
    }.until(removed)

    this.setOnClickListener {
        context.dateSelectorDialog(date.value ?: Date()) {
            context.timeSelectorDialog(it, minuteInterval) {
                date.value = it
            }
        }
    }
}


fun Context.dateSelectorDialog(start: Date, onResult: (Date) -> Unit) {
    val cal = Calendar.getInstance()
    cal.time = start
    DatePickerDialog(
        this,
        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            onResult(cal.time)
        },
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH)
    ).show()
}

fun Context.timeSelectorDialog(start: Date, minuteInterval: Int = 1, onResult: (Date) -> Unit) {
    val cal = Calendar.getInstance()
    cal.time = start
    TimePickerDialog(
        this,
        TimePickerDialog.OnTimeSetListener { view, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            onResult(cal.time)
        },
        cal.get(Calendar.HOUR_OF_DAY),
        cal.get(Calendar.MINUTE),
        false
    ).show()
}