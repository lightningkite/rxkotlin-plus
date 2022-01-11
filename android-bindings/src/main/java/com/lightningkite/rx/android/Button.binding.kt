    package com.lightningkite.rx.android

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.Button
import com.lightningkite.rx.kotlin
import com.lightningkite.rx.optional
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.Subject
import java.util.Optional
import java.text.DateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


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
        button: Button,
        formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
): SOURCE {
    this.subscribeBy {
        button.text = formatter.format(it)
    }.addTo(button.removed)
    button.setOnClickListener {
        this.firstElement().subscribeBy { start ->
            button.context.dateSelectorDialog(start) {
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
    button: Button,
    formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT),
    nullText: String
): SOURCE {
    this.subscribeBy {
        button.text = it.kotlin?.let { formatter.format(it) } ?: nullText
    }.addTo(button.removed)

    button.setOnClickListener {
        this.firstElement().subscribeBy { start ->
            button.context.dateSelectorDialog(start.kotlin ?: LocalDate.now()) {
                this.onNext(it.optional)
            }
        }
    }
    button.setOnLongClickListener {
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
    button: Button,
    formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
): SOURCE {
    this.subscribeBy {
        button.text = formatter.format(it)
    }.addTo(button.removed)
    button.setOnClickListener {
        this.firstElement().subscribeBy { start ->
            button.context.timeSelectorDialog(start) {
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
    button: Button,
    formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT),
    nullText: String
): SOURCE {
    this.subscribeBy {
        button.text = it.kotlin?.let { formatter.format(it) } ?: nullText
    }.addTo(button.removed)

    button.setOnClickListener {
        this.firstElement().subscribeBy { start ->
            button.context.timeSelectorDialog(start.kotlin ?: LocalTime.now()) {
                this.onNext(it.optional)
            }
        }
    }
    button.setOnLongClickListener {
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
    button: Button,
    formatter: DateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT),
): SOURCE {
    this.subscribeBy {
        button.text = formatter.format(it)
    }.addTo(button.removed)

    button.setOnClickListener {
        this.firstElement().subscribeBy { start ->
            button.context.dateSelectorDialog(start.toLocalDate()) { d ->
                button.context.timeSelectorDialog(start.toLocalTime()) { t ->
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
    button: Button,
    formatter: DateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT),
    nullText: String
): SOURCE {
    this.subscribeBy {
        button.text = it.kotlin?.let { formatter.format(it) } ?: nullText
    }.addTo(button.removed)

    button.setOnClickListener {
        this.firstElement().subscribeBy {  element ->
            val start: LocalDateTime = element.kotlin ?: LocalDateTime.now()
            button.context.dateSelectorDialog(start.toLocalDate()) { d ->
                button.context.timeSelectorDialog(start.toLocalTime()) { t ->
                    this.onNext(LocalDateTime.of(d, t).optional)
                }
            }
        }
    }
    button.setOnLongClickListener {
        this.onNext(Optional.empty())
        true
    }
    return this
}


private fun Context.dateSelectorDialog(start: LocalDate, onResult: (LocalDate) -> Unit) {
    DatePickerDialog(
        this,
        { _, year, month, dayOfMonth ->
            onResult(LocalDate.of(year, month + 1, dayOfMonth))
        },
        start.year,
        start.monthValue - 1,
        start.dayOfMonth
    ).show()
}

private fun Context.timeSelectorDialog(start: LocalTime, onResult: (LocalTime) -> Unit) {
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

