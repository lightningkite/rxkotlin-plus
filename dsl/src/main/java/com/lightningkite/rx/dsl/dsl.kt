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

private val unsetSize = -3

private val View.lparams: ViewGroup.MarginLayoutParams get() = (layoutParams as? ViewGroup.MarginLayoutParams) ?: run {
    val n = when(val it = layoutParams) {
        null -> ViewGroup.MarginLayoutParams(unsetSize, unsetSize)
        is LinearLayout.LayoutParams -> ViewGroup.MarginLayoutParams(it)
        is FrameLayout.LayoutParams -> ViewGroup.MarginLayoutParams(it)
        is ViewGroup.MarginLayoutParams -> ViewGroup.MarginLayoutParams(it)
        else -> ViewGroup.MarginLayoutParams(it)
    }
    this.layoutParams = n
    n
}
private val View.llparams: LinearLayout.LayoutParams get() = (layoutParams as? LinearLayout.LayoutParams) ?: run {
    val n = when(val it = layoutParams) {
        null -> LinearLayout.LayoutParams(unsetSize, unsetSize)
        is LinearLayout.LayoutParams -> LinearLayout.LayoutParams(it)
        is FrameLayout.LayoutParams -> LinearLayout.LayoutParams(it)
        is ViewGroup.MarginLayoutParams -> LinearLayout.LayoutParams(it)
        else -> LinearLayout.LayoutParams(it)
    }
    this.layoutParams = n
    n
}
private val View.flparams: FrameLayout.LayoutParams get() = (layoutParams as? FrameLayout.LayoutParams) ?: run {
    val n = when(val it = layoutParams) {
        null -> FrameLayout.LayoutParams(unsetSize, unsetSize)
        is LinearLayout.LayoutParams -> FrameLayout.LayoutParams(it)
        is FrameLayout.LayoutParams -> FrameLayout.LayoutParams(it)
        is ViewGroup.MarginLayoutParams -> FrameLayout.LayoutParams(it)
        else -> FrameLayout.LayoutParams(it)
    }
    this.layoutParams = n
    n
}

fun <T: View> T.weight(value: Float) = this.apply { llparams.weight = value }
fun <T: View> T.width(value: Int) = this.apply { lparams.width = value.dp }
fun <T: View> T.matchWidth() = this.apply { lparams.width = ViewGroup.LayoutParams.MATCH_PARENT }
fun <T: View> T.height(value: Int) = this.apply { lparams.height = value.dp }
fun <T: View> T.matchHeight() = this.apply { lparams.height = ViewGroup.LayoutParams.MATCH_PARENT }
fun <T: View> T.frameGravity(@GravityInt gravity: Int) = this.apply { flparams.gravity = gravity }
fun <T: View> T.align(@GravityInt gravity: Int) = this.apply { llparams.gravity = gravity }
fun <T: View> T.pad(value: Int) = this.apply { setPadding(value.dp) }
fun <T: View> T.margin(value: Int) = this.apply { lparams.setMargins(value.dp) }
fun <T: View> T.hpad(value: Int) = this.apply { updatePadding(left = value.dp, right = value.dp) }
fun <T: View> T.hmargin(value: Int) = this.apply { lparams.updateMargins(left = value.dp, right = value.dp) }
fun <T: View> T.vpad(value: Int) = this.apply { updatePadding(top = value.dp, bottom = value.dp) }
fun <T: View> T.vmargin(value: Int) = this.apply { lparams.updateMargins(top = value.dp, bottom = value.dp) }

private val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()

open class ViewDsl(
    val context: Context,
    val defaultSpacing: Int = 8
) {
    fun <T: View> T.applyDefaultSpacing(): T = apply { lparams.setMargins(defaultSpacing.dp, defaultSpacing.dp, defaultSpacing.dp, defaultSpacing.dp) }
    fun <T: View> T.applyDefaultPadding(): T = apply { setPadding(defaultSpacing.dp, defaultSpacing.dp, defaultSpacing.dp, defaultSpacing.dp) }
}

fun ViewDsl.ll(orientation: Int, defaultGravity: Int, defaultWidth: Int, defaultHeight: Int, elements: Array<out View>) = LinearLayout(context).apply {
    this.orientation = orientation
    this.gravity = defaultGravity
    for(child in elements) addView(child, child.llparams.apply {
        if(width == unsetSize) width = defaultWidth // ViewGroup.LayoutParams.WRAP_CONTENT
        if(height == unsetSize) height = defaultHeight // ViewGroup.LayoutParams.WRAP_CONTENT
        if(weight != 0f) {
            if(orientation == LinearLayout.HORIZONTAL) width = 0
            else height = 0
        }
    })
}

fun ViewDsl.htl(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.TOP or Gravity.LEFT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
fun ViewDsl.hts(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.TOP or Gravity.START, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
fun ViewDsl.htc(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.TOP or Gravity.CENTER_HORIZONTAL, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
fun ViewDsl.htr(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.TOP or Gravity.RIGHT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
fun ViewDsl.hte(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.TOP or Gravity.END, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)

fun ViewDsl.hcl(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.CENTER_VERTICAL or Gravity.LEFT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
fun ViewDsl.hcs(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.CENTER_VERTICAL or Gravity.START, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
fun ViewDsl.hcc(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
fun ViewDsl.hc(vararg elements: View) = hcc(*elements)
fun ViewDsl.hcr(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.CENTER_VERTICAL or Gravity.RIGHT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
fun ViewDsl.hce(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.CENTER_VERTICAL or Gravity.END, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)

fun ViewDsl.hbl(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.LEFT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
fun ViewDsl.hbs(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.START, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
fun ViewDsl.hbc(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
fun ViewDsl.hbr(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.RIGHT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
fun ViewDsl.hbe(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.END, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)

fun ViewDsl.hfl(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.LEFT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, elements)
fun ViewDsl.hfs(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.START, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, elements)
fun ViewDsl.hfc(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, elements)
fun ViewDsl.hfr(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.RIGHT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, elements)
fun ViewDsl.hfe(vararg elements: View) = ll(LinearLayout.HORIZONTAL, Gravity.BOTTOM or Gravity.END, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, elements)

fun ViewDsl.vtl(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.TOP or Gravity.LEFT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
fun ViewDsl.vts(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.TOP or Gravity.START, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
fun ViewDsl.vtc(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.TOP or Gravity.CENTER_HORIZONTAL, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
fun ViewDsl.vtr(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.TOP or Gravity.RIGHT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
fun ViewDsl.vte(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.TOP or Gravity.END, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
fun ViewDsl.vtf(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.TOP or Gravity.END, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)

fun ViewDsl.vcl(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.CENTER_VERTICAL or Gravity.LEFT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
fun ViewDsl.vcs(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.CENTER_VERTICAL or Gravity.START, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
fun ViewDsl.vcc(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
fun ViewDsl.vc(vararg elements: View) = vcc(*elements)
fun ViewDsl.vcr(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.CENTER_VERTICAL or Gravity.RIGHT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
fun ViewDsl.vce(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.CENTER_VERTICAL or Gravity.END, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
fun ViewDsl.vcf(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.CENTER_VERTICAL or Gravity.END, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)

fun ViewDsl.vbl(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.BOTTOM or Gravity.LEFT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
fun ViewDsl.vbs(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.BOTTOM or Gravity.START, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
fun ViewDsl.vbc(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
fun ViewDsl.vbr(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.BOTTOM or Gravity.RIGHT, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
fun ViewDsl.vbe(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.BOTTOM or Gravity.END, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)
fun ViewDsl.vbf(vararg elements: View) = ll(LinearLayout.VERTICAL, Gravity.BOTTOM or Gravity.END, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, elements)

fun ViewDsl.f(vararg elements: View) = FrameLayout(context).apply {
    for(child in elements) addView(child, child.flparams.apply {
        if(width == unsetSize) width = ViewGroup.LayoutParams.MATCH_PARENT
        if(height == unsetSize) height = ViewGroup.LayoutParams.MATCH_PARENT
    })
}
fun ViewDsl.scroll(view: View) = ScrollView(context).apply {
    addView(view, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
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

//fun <T: TextView> T.text(text: String) = apply { setText(text) }
//fun <T: TextView> T.text(@StringRes text: StringResource) = apply { setText(text) }
//fun <T: TextView> T.text(text: ViewString) = apply { setText(text) }
//@JvmName("textString") fun <T: TextView> T.text(text: Observable<String>) = apply { text.into(this, TextView::setText) }
//@JvmName("textResource") fun <T: TextView> T.text(text: Observable<StringResource>) = apply { text.into(this, TextView::setText) }
//@JvmName("textViewString") fun <T: TextView> T.text(text: Observable<ViewString>) = apply { text.into(this, TextView::setText) }


fun ActivityAccess.dsl(make: ViewDsl.()->View): View = ViewDsl(context).let(make)