package com.lightningkite.rx.android

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.widget.Button
import androidx.annotation.RequiresApi
import com.lightningkite.rx.kotlin
import com.lightningkite.rx.optional
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.Subject
import java.util.Optional
import java.text.DateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

@JvmName("bindDate")
fun <SOURCE: Subject<LocalDate>> SOURCE.bind(
    view: Button,
    formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
): SOURCE {
    this.subscribeBy {
        view.text = formatter.format(it)
    }.addTo(view.removed)
    view.setOnClickListener {
        this.firstElement().subscribeBy { start ->
            view.context.dateSelectorDialog(start) {
                this.onNext(it)
            }
        }
    }
    return this
}

@JvmName("bindDate")
fun <SOURCE: Subject<Optional<LocalDate>>> SOURCE.bind(
    view: Button,
    formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT),
    nullText: String
): SOURCE {
    this.subscribeBy {
        view.text = it.kotlin?.let { formatter.format(it) } ?: nullText
    }.addTo(view.removed)

    view.setOnClickListener {
        this.firstElement().subscribeBy { start ->
            view.context.dateSelectorDialog(start.kotlin ?: LocalDate.now()) {
                this.onNext(it.optional)
            }
        }
    }
    view.setOnLongClickListener {
        this.onNext(Optional.empty())
        true
    }
    return this
}

@JvmName("bindTime")
fun <SOURCE: Subject<LocalTime>> SOURCE.bind(
    view: Button,
    formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
): SOURCE {
    this.subscribeBy {
        view.text = formatter.format(it)
    }.addTo(view.removed)
    view.setOnClickListener {
        this.firstElement().subscribeBy { start ->
            view.context.timeSelectorDialog(start) {
                this.onNext(it)
            }
        }
    }
    return this
}

@JvmName("bindTime")
fun <SOURCE: Subject<Optional<LocalTime>>> SOURCE.bind(
    view: Button,
    formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT),
    nullText: String
): SOURCE {
    this.subscribeBy {
        view.text = it.kotlin?.let { formatter.format(it) } ?: nullText
    }.addTo(view.removed)

    view.setOnClickListener {
        this.firstElement().subscribeBy { start ->
            view.context.timeSelectorDialog(start.kotlin ?: LocalTime.now()) {
                this.onNext(it.optional)
            }
        }
    }
    view.setOnLongClickListener {
        this.onNext(Optional.empty())
        true
    }
    return this
}


@JvmName("bindDateTime")
fun <SOURCE: Subject<LocalDateTime>> SOURCE.bind(
    view: Button,
    formatter: DateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT),
): SOURCE {
    this.subscribeBy {
        view.text = formatter.format(it)
    }.addTo(view.removed)

    view.setOnClickListener {
        this.firstElement().subscribeBy { start ->
            view.context.dateSelectorDialog(start.toLocalDate()) { d ->
                view.context.timeSelectorDialog(start.toLocalTime()) { t ->
                    this.onNext(LocalDateTime.of(d, t))
                }
            }
        }
    }
    return this
}

@JvmName("bindDateTime")
fun <SOURCE: Subject<Optional<LocalDateTime>>> SOURCE.bind(
    view: Button,
    formatter: DateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT),
    nullText: String
): SOURCE {
    this.subscribeBy {
        view.text = it.kotlin?.let { formatter.format(it) } ?: nullText
    }.addTo(view.removed)

    view.setOnClickListener {
        this.firstElement().subscribeBy { it ->
            val start: LocalDateTime = it.kotlin ?: LocalDateTime.now()
            view.context.dateSelectorDialog(start.toLocalDate()) { d ->
                view.context.timeSelectorDialog(start.toLocalTime()) { t ->
                    this.onNext(LocalDateTime.of(d, t).optional)
                }
            }
        }
    }
    view.setOnLongClickListener {
        this.onNext(Optional.empty())
        true
    }
    return this
}


fun Context.dateSelectorDialog(start: LocalDate, onResult: (LocalDate) -> Unit) {
    DatePickerDialog(
        this,
        { view, year, month, dayOfMonth ->
            onResult(LocalDate.of(year, month, dayOfMonth))
        },
        start.year,
        start.monthValue - 1,
        start.dayOfMonth
    ).show()
}

fun Context.timeSelectorDialog(start: LocalTime, onResult: (LocalTime) -> Unit) {
    TimePickerDialog(
        this,
        { view, hour, minute ->
            onResult(LocalTime.of(hour, minute))
        },
        start.hour,
        start.minute,
        false
    ).show()
}