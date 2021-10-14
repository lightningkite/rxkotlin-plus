package com.lightningkite.rx.android

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.lightningkite.rx.*
import com.lightningkite.rx.android.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.Subject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

private fun no(): Nothing = throw NotImplementedError()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("observable.subscribeView(this) { text = it }", "com.lightningkite.rx.android.bind"),
    level = DeprecationLevel.ERROR
)
fun TextView.bindString(observable: Observable<String>): Unit = no()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("observable.subscribeView(this) { text = mapper(it) }", "com.lightningkite.rx.android.bind"),
    level = DeprecationLevel.ERROR
)
fun <T> TextView.bindText(observable: Observable<T>, mapper: (T)->String): Unit = no()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("this.bind(data, makeView)", "com.lightningkite.rx.android.bind"),
    level = DeprecationLevel.ERROR
)
fun <T> RecyclerView.bind(data: Observable<List<T>>, defaultValue: T, makeView: (Observable<T>) -> View): Unit = no()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("observable.subscribeView(this) { isEnabled = it }", "com.lightningkite.rx.android.bind"),
    level = DeprecationLevel.ERROR
)
fun Button.bindActive(observable: Observable<Boolean>, activeBackground: Drawable, inactiveBackground: Drawable): Unit = no()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("observable.subscribeView(this) { isEnabled = it }", "com.lightningkite.rx.android.bind"),
    level = DeprecationLevel.ERROR
)
fun Button.bindActive(
    observable: Observable<Boolean>,
    activeColorResource: ColorResource? = null,
    inactiveColorResource: ColorResource? = null
): Unit = no()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("run { this.numStars = stars; observable.toSubjectFloat().bind(this) }", "com.lightningkite.rx.android.bind", "com.lightningkite.rx.android.toSubjectFloat"),
    level = DeprecationLevel.ERROR
)
fun RatingBar.bind(stars: Int, observable: Subject<Int>): Unit = no()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("run { this.numStars = stars; observable.subscribeView(this) { rating = it.toFloat() } }", "com.lightningkite.rx.android.bind"),
    level = DeprecationLevel.ERROR
)
fun RatingBar.bind(stars: Int, observable: Observable<Int>): Unit = no()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("run { this.numStars = stars; observable.bind(this) }", "com.lightningkite.rx.android.bind"),
    level = DeprecationLevel.ERROR
)
fun RatingBar.bindFloat(stars: Int, observable: Observable<Float>): Unit = no()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("run { this.numStars = stars; observable.subscribeView(this) { rating = it } }", "com.lightningkite.rx.android.bind"),
    level = DeprecationLevel.ERROR
)
fun RatingBar.bindFloat(stars: Int, observable: Subject<Float>): Unit = no()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("observable.bind(this)", "com.lightningkite.rx.android.bind", "com.lightningkite.rx.android.isEqualTo"),
    level = DeprecationLevel.ERROR
)
fun CompoundButton.bind(observable: Subject<Boolean>): Unit = no()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("(observable isEqualTo value).bind(this)", "com.lightningkite.rx.android.bind", "com.lightningkite.rx.android.isEqualTo"),
    level = DeprecationLevel.ERROR
)
fun <T> CompoundButton.bindSelect(value: T, observable: Subject<T>): Unit = no()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("(observable isEqualTo value).bind(this)", "com.lightningkite.rx.android.bind", "com.lightningkite.rx.android.isEqualTo"),
    level = DeprecationLevel.ERROR
)
fun <T> CompoundButton.bindSelectNullable(value: T, observable: Subject<Optional<T>>): Unit = no()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("(observable isEqualToOrNull value).bind(this)", "com.lightningkite.rx.android.bind", "com.lightningkite.rx.android.isEqualToOrNull"),
    level = DeprecationLevel.ERROR
)
fun <T> CompoundButton.bindSelectInvert(value: T, observable: Subject<Optional<T>>): Unit = no()

@Deprecated(
    "Dead, do not use.  There's a different version of bindMulti you can use."
)
fun RecyclerView.bindMulti(data: Observable<List<Any>>, typeHandlerSetup: (Any) -> Unit): Unit = no()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("bindMulti", "com.lightningkite.rx.android.bindMulti"),
    level = DeprecationLevel.ERROR
)
fun <T> RecyclerView.bindMulti(
    data: Observable<List<T>>,
    defaultValue: T,
    determineType: (T) -> Int,
    makeView: (Int, Observable<T>) -> View
): Unit = no()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("this.bind(data, makeView)", "com.lightningkite.rx.android.bind"),
    level = DeprecationLevel.ERROR
)
fun <T> LinearLayout.bind(data: Observable<List<T>>, defaultValue: T, makeView: (Observable<T>) -> View): Unit = no()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("this.bind(data, makeView)", "com.lightningkite.rx.android.bind"),
    level = DeprecationLevel.ERROR
)
fun <T> LinearLayout.bindHorizontal(
    data: Observable<List<T>>,
    defaultValue: T,
    makeView: (Observable<T>) -> View
): Unit = no()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("observable.subscribeView(this) { setText(it) }", "com.lightningkite.rx.android.bind"),
    level = DeprecationLevel.ERROR
)
fun TextView.bindStringRes(observable: Observable<StringResource>): Unit = no()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("bindObservable", "com.lightningkite.rx.android.bindObservable"),
    level = DeprecationLevel.ERROR
)
fun <T> Spinner.bindString(
    options: Observable<List<T>>,
    selected: Subject<T>,
    toString: (T) -> Observable<String>
): Unit = no()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("run { this.max = endInclusive - start; (observable + start).bind(this) }", "com.lightningkite.rx.android.bind", "com.lightningkite.rx.plus"),
    level = DeprecationLevel.ERROR
)
fun SeekBar.bind(start: Int, endInclusive: Int, observable: Subject<Int>): Unit = no()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("observable.bind(this)", "com.lightningkite.rx.android.bind"),
    level = DeprecationLevel.ERROR
)
fun EditText.bindString(observable: Subject<String>): Unit = no()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("observable.toSubjectString().bind(this)", "com.lightningkite.rx.android.bind", "com.lightningkite.rx.toSubjectString"),
    level = DeprecationLevel.ERROR
)
fun EditText.bindInteger(observable: Subject<Int>): Unit = no()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("observable.toSubjectString().bind(this)", "com.lightningkite.rx.android.bind", "com.lightningkite.rx.toSubjectString"),
    level = DeprecationLevel.ERROR
)
fun EditText.bindDouble(observable: Subject<Double>): Unit = no()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("observable.toSubjectString().bind(this)", "com.lightningkite.rx.android.bind", "com.lightningkite.rx.toSubjectString"),
    level = DeprecationLevel.ERROR
)
fun EditText.bindIntegerNullable(observable: Subject<Optional<Int>>): Unit = no()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("observable.toSubjectString().bind(this)", "com.lightningkite.rx.android.bind", "com.lightningkite.rx.toSubjectString"),
    level = DeprecationLevel.ERROR
)
fun EditText.bindDoubleNullable(observable: Subject<Optional<Double>>): Unit = no()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("this.bind(options, toString, onItemSelected)", "com.lightningkite.rx.android.bind"),
    level = DeprecationLevel.ERROR
)
fun <T> AutoCompleteTextView.bindList(
    options: Observable<List<T>>,
    toString: (T) -> String,
    onItemSelected: (T) -> Unit
): Unit = no()

@Deprecated(
    "Use bindDate directly from RxKotlin Properties",
    replaceWith = ReplaceWith("this.bindDate(date)", "com.lightningkite.rx.android.bindDate"),
    level = DeprecationLevel.ERROR
)
fun Button.bind(date: Subject<LocalDate>): Unit = no()

@Deprecated(
    "Use bindTime directly from RxKotlin Properties",
    replaceWith = ReplaceWith("bindTime(date)", "com.lightningkite.rx.android.bindTime"),
    level = DeprecationLevel.ERROR
)
fun Button.bind(date: Subject<LocalTime>, minuteInterval: Int = 1): Unit = no()

@Deprecated(
    "Use bindTime directly from RxKotlin Properties",
    replaceWith = ReplaceWith("bindDateTime(date)", "com.lightningkite.rx.android.bindTime"),
    level = DeprecationLevel.ERROR
)
@JvmName("bindLocalDateTime")
fun Button.bind(date: Subject<LocalDateTime>, minuteInterval: Int = 1): Unit = no()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("run { this.max = 10000; observable.subscribeView(this) { progress = (it * 10000).toInt() } }", "com.lightningkite.rx.android.bind"),
    level = DeprecationLevel.ERROR
)
fun ProgressBar.bindFloat(observable: Observable<Float>): Unit = no()

@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("observable.subscribeView(this) { progress = it }", "com.lightningkite.rx.android.bind"),
    level = DeprecationLevel.ERROR
)
fun ProgressBar.bindInt(observable: Observable<Int>): Unit = no()


@Deprecated(
    "Use directly from RxKotlin Properties",
    replaceWith = ReplaceWith("observable.subscribeView(this) { progress = it.toInt() }", "com.lightningkite.rx.android.bind"),
    level = DeprecationLevel.ERROR
)
fun ProgressBar.bindLong(observable: Observable<Long>): Unit = no()


