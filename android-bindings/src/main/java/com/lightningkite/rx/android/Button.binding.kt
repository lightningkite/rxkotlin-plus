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


/**
 * Displays this on the buttons text using the formatter.
 * Pressing the button will launch the Android DatePickerDialog
 * and the results will be pass to this.
 *
 * Example:
 * val value = ValueSubject<LocalDate>(LocalDate.of(1,1,1))
 * value.bind(buttonView)
 */
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

/**
 * Displays this on the buttons text using the formatter.
 * Pressing the button will launch the Android DatePickerDialog
 * and the results will be pass to this. If this is an empty Optional
 * the null text is used instead for the button.
 *
 * Example:
 * val value = ValueSubject<Optional<LocalDate>>(Optional.empty())
 * value.bind(buttonView, nullText = "Select Date")
 */
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


/**
 * Displays this on the buttons text using the formatter.
 * Pressing the button will launch the Android TimePickerDialog
 * and the results will be pass to this.
 *
 * Example:
 * val value = ValueSubject<LocalTime>(LocalTime.of(1,1,1))
 * value.bind(buttonView)
 */
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

/**
 * Displays this on the buttons text using the formatter.
 * Pressing the button will launch the Android TimePickerDialog
 * and the results will be pass to this. If this is an empty Optional
 * the null text is used instead for the button.
 *
 * Example:
 * val value = ValueSubject<Optional<LocalTime>>(Optional.empty())
 * value.bind(buttonView, nullText = "Select Time")
 */
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


/**
 * Displays this on the buttons text using the formatter.
 * Pressing the button will launch the Android TimePickerDialog
 * and DatePickerDialog and the results will be pass to this.
 *
 * Example:
 * val value = ValueSubject<LocalDateTime>(LocalDateTime.now())
 * value.bind(buttonView)
 */
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

/**
 * Displays this on the buttons text using the formatter.
 * Pressing the button will launch the Android TimePickerDialog
 * and DatePickerDialog and the results will be pass to this.
 * If this is an empty Optional the null text is used instead
 * for the button.
 *
 * Example:
 * val value = ValueSubject<Optional<LocalDateTime>>(Optional.empty())
 * value.bind(buttonView, nullText = "Select DateTime")
 */
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
        this.firstElement().subscribeBy {  element ->
            val start: LocalDateTime = element.kotlin ?: LocalDateTime.now()
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
        { _, year, month, dayOfMonth ->
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
        { _, hour, minute ->
            onResult(LocalTime.of(hour, minute))
        },
        start.hour,
        start.minute,
        false
    ).show()
}