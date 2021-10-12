package com.lightningkite.rxkotlinproperty.android

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.Button
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.Subject
import java9.util.Optional
import java.text.DateFormat
import java.util.*

fun Button.bindDate(
    date: Subject<Date>,
    formatter: DateFormat = DateFormat.getDateInstance(DateFormat.SHORT)
) {
    date.subscribeBy {
        this.text = formatter.format(it)
    }.addTo(removed)
    this.setOnClickListener {
        date.firstElement().subscribeBy { start ->
            context.dateSelectorDialog(start) {
                date.onNext(it)
            }
        }
    }
}

fun Button.bindDate(
    date: Subject<Optional<Date>>,
    formatter: DateFormat = DateFormat.getDateInstance(DateFormat.SHORT),
    nullText: String
) {
    date.subscribeBy {
        this.text = it.kotlin?.let { formatter.format(it) } ?: nullText
    }.addTo(removed)

    this.setOnClickListener {
        date.firstElement().subscribeBy { start ->
            context.dateSelectorDialog(start.kotlin ?: Date()) {
                date.onNext(it.optional)
            }
        }
    }
}

fun Button.bindTime(
    date: Subject<Date>,
    formatter: DateFormat = DateFormat.getTimeInstance(DateFormat.SHORT),
) {
    date.subscribeBy {
        this.text = formatter.format(it)
    }.addTo(removed)
    this.setOnClickListener {
        date.firstElement().subscribeBy { start ->
            context.timeSelectorDialog(start) {
                date.onNext(it)
            }
        }
    }
}

fun Button.bindTime(
    date: Subject<Optional<Date>>,
    formatter: DateFormat = DateFormat.getTimeInstance(DateFormat.SHORT),
    nullText: String
) {
    date.subscribeBy {
        this.text = it.kotlin?.let { formatter.format(it) } ?: nullText
    }.addTo(removed)

    this.setOnClickListener {
        date.firstElement().subscribeBy { start ->
            context.timeSelectorDialog(start.kotlin ?: Date()) {
                date.onNext(it.optional)
            }
        }
    }
}


fun Button.bindDateTime(
    date: Subject<Date>,
    formatter: DateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT),
) {
    date.subscribeBy {
        this.text = formatter.format(it)
    }.addTo(removed)

    this.setOnClickListener {
        date.firstElement().subscribeBy { start ->
            context.dateSelectorDialog(start) {
                context.timeSelectorDialog(it) {
                    date.onNext(it)
                }
            }
        }
    }
}

fun Button.bindDateTime(
    date: Subject<Optional<Date>>,
    formatter: DateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT),
    nullText: String
) {
    date.subscribeBy {
        this.text = it.kotlin?.let { formatter.format(it) } ?: nullText
    }.addTo(removed)

    this.setOnClickListener {
        date.firstElement().subscribeBy { start ->
            context.dateSelectorDialog(start.kotlin ?: Date()) {
                context.timeSelectorDialog(it) {
                    date.onNext(it.optional)
                }
            }
        }
    }
}


fun Context.dateSelectorDialog(start: Date, onResult: (Date) -> Unit) {
    val cal = Calendar.getInstance()
    cal.time = start
    DatePickerDialog(
        this,
        { view, year, month, dayOfMonth ->
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

fun Context.timeSelectorDialog(start: Date, onResult: (Date) -> Unit) {
    val cal = Calendar.getInstance()
    cal.time = start
    TimePickerDialog(
        this,
        { view, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            onResult(cal.time)
        },
        cal.get(Calendar.HOUR_OF_DAY),
        cal.get(Calendar.MINUTE),
        false
    ).show()
}