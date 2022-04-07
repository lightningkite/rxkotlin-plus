package com.lightningkite.rx.dsl

import android.content.Context
import android.content.res.Resources
import android.opengl.GLSurfaceView
import android.view.*
import android.webkit.WebView
import android.widget.*
import androidx.annotation.GravityInt
import androidx.annotation.StringRes
import androidx.core.view.*
import com.lightningkite.rx.android.into
import com.lightningkite.rx.android.onClick
import com.lightningkite.rx.android.resources.StringResource
import com.lightningkite.rx.android.resources.ViewString
import com.lightningkite.rx.android.resources.setText
import com.lightningkite.rx.viewgenerators.ActivityAccess
import io.reactivex.rxjava3.core.Observable

private val View.lparams: ViewGroup.MarginLayoutParams get() = (layoutParams as? ViewGroup.MarginLayoutParams) ?: run {
    println("Porting the layout params to ViewGroup.MarginLayoutParams from ${layoutParams?.let { it::class.simpleName }}")
    val n = when(val it = layoutParams) {
        null -> ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
        is LinearLayout.LayoutParams -> ViewGroup.MarginLayoutParams(it)
        is FrameLayout.LayoutParams -> ViewGroup.MarginLayoutParams(it)
        is ViewGroup.MarginLayoutParams -> ViewGroup.MarginLayoutParams(it)
        else -> ViewGroup.MarginLayoutParams(it)
    }
    this.layoutParams = n
    n
}
private val View.llparams: LinearLayout.LayoutParams get() = (layoutParams as? LinearLayout.LayoutParams) ?: run {
    println("Porting the layout params to LinearLayout.LayoutParams from ${layoutParams?.let { it::class.simpleName }}")
    val n = when(val it = layoutParams) {
        null -> LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        is LinearLayout.LayoutParams -> LinearLayout.LayoutParams(it)
        is FrameLayout.LayoutParams -> LinearLayout.LayoutParams(it)
        is ViewGroup.MarginLayoutParams -> LinearLayout.LayoutParams(it)
        else -> LinearLayout.LayoutParams(it)
    }
    this.layoutParams = n
    n
}
private val View.flparams: FrameLayout.LayoutParams get() = (layoutParams as? FrameLayout.LayoutParams) ?: run {
    println("Porting the layout params to FrameLayout.LayoutParams from ${layoutParams?.let { it::class.simpleName }}")
    val n = when(val it = layoutParams) {
        null -> FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        is LinearLayout.LayoutParams -> FrameLayout.LayoutParams(it)
        is FrameLayout.LayoutParams -> FrameLayout.LayoutParams(it)
        is ViewGroup.MarginLayoutParams -> FrameLayout.LayoutParams(it)
        else -> FrameLayout.LayoutParams(it)
    }
    this.layoutParams = n
    n
}
fun View.weight(value: Float) = this.apply { llparams.weight = value }
fun View.width(value: Int) = this.apply { lparams.width = value.dp }
fun View.matchWidth() = this.apply { lparams.width = ViewGroup.LayoutParams.MATCH_PARENT }
fun View.height(value: Int) = this.apply { lparams.height = value.dp }
fun View.matchHeight() = this.apply { lparams.height = ViewGroup.LayoutParams.MATCH_PARENT }
fun View.frameGravity(@GravityInt gravity: Int) = this.apply { flparams.gravity = gravity }
fun View.align(@GravityInt gravity: Int) = this.apply { llparams.gravity = gravity }
fun View.pad(value: Int) = this.apply { setPadding(value.dp) }
fun View.margin(value: Int) = this.apply { lparams.setMargins(value.dp) }
fun View.hpad(value: Int) = this.apply { updatePadding(left = value.dp, right = value.dp) }
fun View.hmargin(value: Int) = this.apply { lparams.updateMargins(left = value.dp, right = value.dp) }
fun View.vpad(value: Int) = this.apply { updatePadding(top = value.dp, bottom = value.dp) }
fun View.vmargin(value: Int) = this.apply { lparams.updateMargins(top = value.dp, bottom = value.dp) }

private val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()

open class ViewDsl(
    val context: Context,
    val defaultSpacing: Int = 8
) {
    fun <T: View> T.applyDefaultSpacing(): T = apply { lparams.setMargins(defaultSpacing.dp, defaultSpacing.dp, defaultSpacing.dp, defaultSpacing.dp) }
    fun <T: View> T.applyDefaultPadding(): T = apply { setPadding(defaultSpacing.dp, defaultSpacing.dp, defaultSpacing.dp, defaultSpacing.dp) }
}

fun ViewDsl.ll(orientation: Int, defaultGravity: Int, elements: Array<out View>) = LinearLayout(context).apply {
    this.orientation = orientation
    for(child in elements) addView(child, child.llparams.apply {
        println("Adding child $child with lparams ${this.width} x ${this.height} w $weight")
        if(gravity == -1) gravity = defaultGravity
        if(weight != 0f) {
            if(orientation == LinearLayout.HORIZONTAL) width = 0
            else height = 0
        }
    })
}
fun ViewDsl.ht(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.TOP, elements)
fun ViewDsl.hc(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.CENTER, elements)
fun ViewDsl.hb(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM, elements)
fun ViewDsl.vl(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.LEFT, elements)
fun ViewDsl.vs(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.START, elements)
fun ViewDsl.vc(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.CENTER, elements)
fun ViewDsl.vr(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.RIGHT, elements)
fun ViewDsl.ve(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.END, elements)
fun ViewDsl.f(vararg elements: View) = FrameLayout(context).apply {
    for(child in elements) addView(child, child.flparams)
}
fun ViewDsl.scroll(view: View) = ScrollView(context).apply {
    addView(view, view.flparams)
}

fun ViewDsl.gLSurface() = GLSurfaceView(context).applyDefaultSpacing()
fun ViewDsl.viewFlipper() = ViewFlipper(context).applyDefaultSpacing()

fun ViewDsl.surface(style: Int = 0) = SurfaceView(if(style == 0) context else ContextThemeWrapper(context, style), null, 0).applyDefaultSpacing()
fun ViewDsl.texture(style: Int = 0) = TextureView(if(style == 0) context else ContextThemeWrapper(context, style), null, 0).applyDefaultSpacing()
fun ViewDsl.view(style: Int = 0) = View(if(style == 0) context else ContextThemeWrapper(context, style), null, 0).applyDefaultSpacing()
fun ViewDsl.web(style: Int = 0) = WebView(if(style == 0) context else ContextThemeWrapper(context, style), null, 0).applyDefaultSpacing()
fun ViewDsl.adapterViewFlipper(style: Int = 0) = AdapterViewFlipper(if(style == 0) context else ContextThemeWrapper(context, style), null, 0).applyDefaultSpacing()
fun ViewDsl.autoCompleteText(style: Int = 0) = AutoCompleteTextView(if(style == 0) context else ContextThemeWrapper(context, style), null, 0).applyDefaultSpacing()
fun ViewDsl.button(style: Int = 0) = Button(if(style == 0) context else ContextThemeWrapper(context, style), null, 0).applyDefaultSpacing()
fun ViewDsl.calendar(style: Int = 0) = CalendarView(if(style == 0) context else ContextThemeWrapper(context, style), null, 0).applyDefaultSpacing()
fun ViewDsl.checkBox(style: Int = 0) = CheckBox(if(style == 0) context else ContextThemeWrapper(context, style), null, 0).applyDefaultSpacing()
fun ViewDsl.checkedText(style: Int = 0) = CheckedTextView(if(style == 0) context else ContextThemeWrapper(context, style), null, 0).applyDefaultSpacing()
fun ViewDsl.chronometer(style: Int = 0) = Chronometer(if(style == 0) context else ContextThemeWrapper(context, style), null, 0).applyDefaultSpacing()
fun ViewDsl.datePicker(style: Int = 0) = DatePicker(if(style == 0) context else ContextThemeWrapper(context, style), null, 0).applyDefaultSpacing()
fun ViewDsl.editText(style: Int = 0) = EditText(if(style == 0) context else ContextThemeWrapper(context, style), null, 0).applyDefaultSpacing()
fun ViewDsl.expandableListView(style: Int = 0) = ExpandableListView(if(style == 0) context else ContextThemeWrapper(context, style), null, 0).applyDefaultSpacing()
fun ViewDsl.imageButton(style: Int = 0) = ImageButton(if(style == 0) context else ContextThemeWrapper(context, style), null, 0).applyDefaultSpacing()
fun ViewDsl.image(style: Int = 0) = ImageView(if(style == 0) context else ContextThemeWrapper(context, style), null, 0).applyDefaultSpacing()
fun ViewDsl.listView(style: Int = 0) = ListView(if(style == 0) context else ContextThemeWrapper(context, style), null, 0).applyDefaultSpacing()
fun ViewDsl.multiAutoCompleteText(style: Int = 0) = MultiAutoCompleteTextView(if(style == 0) context else ContextThemeWrapper(context, style), null, 0).applyDefaultSpacing()
fun ViewDsl.numberPicker(style: Int = 0) = NumberPicker(if(style == 0) context else ContextThemeWrapper(context, style), null, 0).applyDefaultSpacing()
fun ViewDsl.progressBar(style: Int = 0) = ProgressBar(if(style == 0) context else ContextThemeWrapper(context, style), null, 0).applyDefaultSpacing()
fun ViewDsl.quickContactBadge(style: Int = 0) = QuickContactBadge(if(style == 0) context else ContextThemeWrapper(context, style), null, 0).applyDefaultSpacing()
fun ViewDsl.radioButton(style: Int = 0) = RadioButton(if(style == 0) context else ContextThemeWrapper(context, style), null, 0).applyDefaultSpacing()
fun ViewDsl.ratingBar(style: Int = 0) = RatingBar(if(style == 0) context else ContextThemeWrapper(context, style), null, 0).applyDefaultSpacing()
fun ViewDsl.search(style: Int = 0) = SearchView(if(style == 0) context else ContextThemeWrapper(context, style), null, 0).applyDefaultSpacing()
fun ViewDsl.seekBar(style: Int = 0) = SeekBar(if(style == 0) context else ContextThemeWrapper(context, style), null, 0).applyDefaultSpacing()
fun ViewDsl.space(style: Int = 0) = Space(if(style == 0) context else ContextThemeWrapper(context, style), null, 0).applyDefaultSpacing()
fun ViewDsl.spinner(style: Int = 0) = Spinner(if(style == 0) context else ContextThemeWrapper(context, style), null, 0).applyDefaultSpacing()
fun ViewDsl.stack(style: Int = 0) = StackView(if(style == 0) context else ContextThemeWrapper(context, style), null, 0).applyDefaultSpacing()
fun ViewDsl.switch(style: Int = 0) = Switch(if(style == 0) context else ContextThemeWrapper(context, style), null, 0).applyDefaultSpacing()
fun ViewDsl.text(style: Int = 0) = TextView(if(style == 0) context else ContextThemeWrapper(context, style), null, 0).applyDefaultSpacing()
fun ViewDsl.timePicker(style: Int = 0) = TimePicker(if(style == 0) context else ContextThemeWrapper(context, style), null, 0).applyDefaultSpacing()
fun ViewDsl.toggleButton(style: Int = 0) = ToggleButton(if(style == 0) context else ContextThemeWrapper(context, style), null, 0).applyDefaultSpacing()
fun ViewDsl.video(style: Int = 0) = VideoView(if(style == 0) context else ContextThemeWrapper(context, style), null, 0).applyDefaultSpacing()

fun <T: TextView> T.text(text: String) = apply { setText(text) }
fun <T: TextView> T.text(@StringRes text: StringResource) = apply { setText(text) }
fun <T: TextView> T.text(text: ViewString) = apply { setText(text) }
@JvmName("textString") fun <T: TextView> T.text(text: Observable<String>) = apply { text.into(this, TextView::setText) }
@JvmName("textResource") fun <T: TextView> T.text(text: Observable<StringResource>) = apply { text.into(this, TextView::setText) }
@JvmName("textViewString") fun <T: TextView> T.text(text: Observable<ViewString>) = apply { text.into(this, TextView::setText) }


fun ActivityAccess.dsl(make: ViewDsl.()->View): View = ViewDsl(context).let(make)