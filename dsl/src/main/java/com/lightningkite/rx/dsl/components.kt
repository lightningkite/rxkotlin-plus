package com.lightningkite.rx.dsl

import android.opengl.GLSurfaceView
import android.view.SurfaceView
import android.view.TextureView
import android.view.View
import android.webkit.WebView
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.lightningkite.rx.viewgenerators.ActivityAccess
import com.lightningkite.rx.viewgenerators.SwapView

@RxKotlinViewDsl inline fun ViewDsl.glSurface(setup: GLSurfaceView.() -> Unit = {}) = GLSurfaceView(context).apply { marginDefault() }.apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.surface(setup: SurfaceView.()->Unit = {}) = SurfaceView(context).apply { marginDefault() }.apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.texture(setup: TextureView.()->Unit = {}) = TextureView(context).apply { marginDefault() }.apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.view(setup: View.()->Unit = {}) = View(context).apply { marginDefault() }.apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.web(setup: WebView.()->Unit = {}) = WebView(context).apply { marginDefault() }.apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.autoCompleteText(setup: AutoCompleteTextView.()->Unit = {}) = AutoCompleteTextView(context).apply { marginDefault() }.apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.button(setup: Button.()->Unit = {}) = Button(context).apply { marginDefault() }.apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.checkBox(setup: CheckBox.()->Unit = {}) = CheckBox(context).apply { marginDefault() }.apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.checkedText(setup: CheckedTextView.()->Unit = {}) = CheckedTextView(context).apply { marginDefault() }.apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.editText(setup: EditText.()->Unit = {}) = EditText(context).apply { marginDefault() }.apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.imageButton(setup: ImageButton.()->Unit = {}) = ImageButton(context).apply { marginDefault() }.apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.image(setup: ImageView.()->Unit = {}) = ImageView(context).apply { marginDefault() }.apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.progressBar(setup: ProgressBar.()->Unit = {}) = ProgressBar(context).apply { marginDefault() }.apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.radioButton(setup: RadioButton.()->Unit = {}) = RadioButton(context).apply { marginDefault() }.apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.ratingBar(setup: RatingBar.()->Unit = {}) = RatingBar(context).apply { marginDefault() }.apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.seekBar(setup: SeekBar.()->Unit = {}) = SeekBar(context).apply { marginDefault() }.apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.space(spacingMultiplier: Float = 1f, setup: Space.()->Unit = {}) = Space(context).apply { width((spacingMultiplier * defaultSpacing * 4).toInt()); height((spacingMultiplier * defaultSpacing * 4).toInt()) }.apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.spinner(setup: Spinner.()->Unit = {}) = Spinner(context).apply { marginDefault() }.apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.switch(setup: Switch.()->Unit = {}) = Switch(context).apply { marginDefault() }.apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.text(setup: TextView.()->Unit = {}) = TextView(context).apply { marginDefault() }.apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.toggleButton(setup: ToggleButton.()->Unit = {}) = ToggleButton(context).apply { marginDefault() }.apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.video(setup: VideoView.()->Unit = {}) = VideoView(context).apply { marginDefault() }.apply(setup)

@RxKotlinViewDsl inline fun ViewDsl.recycler(setup: RecyclerView.()->Unit = {}) = RecyclerView(context).apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.pager(setup: ViewPager2.()->Unit = {}) = ViewPager2(context).apply(setup)

@RxKotlinViewDsl inline fun ViewDsl.swap(setup: SwapView.()->Unit = {}) = SwapView(context).apply(setup)

@RxKotlinViewDsl fun ActivityAccess.dsl(make: ViewDsl.()->View): View = ViewDsl(context).let(make)

/*
Ideas for prebuilt components

divider
TODO add more
 */