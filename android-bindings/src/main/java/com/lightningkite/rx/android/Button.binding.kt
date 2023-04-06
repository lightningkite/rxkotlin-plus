    package com.lightningkite.rx.android

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.Button
import com.badoo.reaktive.maybe.observeOn
import com.badoo.reaktive.maybe.subscribe
import com.badoo.reaktive.observable.firstOrComplete
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.subject.Subject
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
    this.map { formatter.format(it) }.into(button, Button::setText)
    button.setOnClickListener {
        firstOrComplete().observeOn(RequireMainThread).subscribe{ start ->
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
fun <SOURCE: Subject<LocalDate?>> SOURCE.bind(
    button: Button,
    formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT),
    nullText: String
): SOURCE {
    this.map { it.let { formatter.format(it) } ?: nullText }.into(button, Button::setText)
    button.setOnClickListener {
        firstOrComplete().observeOn(RequireMainThread).subscribe{ start ->
            button.context.dateSelectorDialog(start ?: LocalDate.now()) {
                this.onNext(it)
            }
        }
    }
    button.setOnLongClickListener {
        this.onNext(null)
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
    this.map { formatter.format(it) }.into(button, Button::setText)
    button.setOnClickListener {
        firstOrComplete().observeOn(RequireMainThread).subscribe { start ->
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
fun <SOURCE: Subject<LocalTime?>> SOURCE.bind(
    button: Button,
    formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT),
    nullText: String
): SOURCE {
    this.map { it.let { formatter.format(it) } ?: nullText }.into(button, Button::setText)
    button.setOnClickListener {
        firstOrComplete().observeOn(RequireMainThread).subscribe{ start ->
            button.context.timeSelectorDialog(start ?: LocalTime.now()) {
                this.onNext(it)
            }
        }
    }
    button.setOnLongClickListener {
        this.onNext(null)
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
    formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT),
): SOURCE {
    this.map { formatter.format(it) }.into(button, Button::setText)
    button.setOnClickListener {
        firstOrComplete().observeOn(RequireMainThread).subscribe { start ->
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
fun <SOURCE: Subject<LocalDateTime?>> SOURCE.bind(
    button: Button,
    formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT),
    nullText: String
): SOURCE {
    this.map { it.let { formatter.format(it) } ?: nullText }.into(button, Button::setText)
    button.setOnClickListener {
        firstOrComplete().observeOn(RequireMainThread).subscribe {  element ->
            val start: LocalDateTime = element ?: LocalDateTime.now()
            button.context.dateSelectorDialog(start.toLocalDate()) { d ->
                button.context.timeSelectorDialog(start.toLocalTime()) { t ->
                    this.onNext(LocalDateTime.of(d, t))
                }
            }
        }
    }
    button.setOnLongClickListener {
        this.onNext(null)
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

