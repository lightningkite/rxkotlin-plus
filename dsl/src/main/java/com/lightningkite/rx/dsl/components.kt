package com.lightningkite.rx.dsl

import android.opengl.GLSurfaceView
import android.view.SurfaceView
import android.view.TextureView
import android.view.View
import android.webkit.WebView
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.lightningkite.rx.viewgenerators.ActivityAccess

@RxKotlinViewDsl inline fun ViewDsl.glSurface(setup: GLSurfaceView.() -> Unit = {}) = GLSurfaceView(context).applyDefaultSpacing().apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.viewFlipper(setup: ViewFlipper.() -> Unit = {}) = ViewFlipper(context).applyDefaultSpacing().apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.surface(setup: SurfaceView.()->Unit = {}) = SurfaceView(context).applyDefaultSpacing().apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.texture(setup: TextureView.()->Unit = {}) = TextureView(context).applyDefaultSpacing().apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.view(setup: View.()->Unit = {}) = View(context).applyDefaultSpacing().apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.web(setup: WebView.()->Unit = {}) = WebView(context).applyDefaultSpacing().apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.autoCompleteText(setup: AutoCompleteTextView.()->Unit = {}) = AutoCompleteTextView(context).applyDefaultSpacing().apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.button(setup: Button.()->Unit = {}) = Button(context).applyDefaultSpacing().apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.checkBox(setup: CheckBox.()->Unit = {}) = CheckBox(context).applyDefaultSpacing().apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.checkedText(setup: CheckedTextView.()->Unit = {}) = CheckedTextView(context).applyDefaultSpacing().apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.editText(setup: EditText.()->Unit = {}) = EditText(context).applyDefaultSpacing().apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.imageButton(setup: ImageButton.()->Unit = {}) = ImageButton(context).applyDefaultSpacing().apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.image(setup: ImageView.()->Unit = {}) = ImageView(context).applyDefaultSpacing().apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.progressBar(setup: ProgressBar.()->Unit = {}) = ProgressBar(context).applyDefaultSpacing().apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.radioButton(setup: RadioButton.()->Unit = {}) = RadioButton(context).applyDefaultSpacing().apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.ratingBar(setup: RatingBar.()->Unit = {}) = RatingBar(context).applyDefaultSpacing().apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.seekBar(setup: SeekBar.()->Unit = {}) = SeekBar(context).applyDefaultSpacing().apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.space(spacingMultiplier: Float = 1f, setup: Space.()->Unit = {}) = Space(context).width((spacingMultiplier * defaultSpacing * 4).toInt()).height((spacingMultiplier * defaultSpacing * 4).toInt()).apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.spinner(setup: Spinner.()->Unit = {}) = Spinner(context).applyDefaultSpacing().apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.switch(setup: Switch.()->Unit = {}) = Switch(context).applyDefaultSpacing().apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.text(setup: TextView.()->Unit = {}) = TextView(context).applyDefaultSpacing().apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.toggleButton(setup: ToggleButton.()->Unit = {}) = ToggleButton(context).applyDefaultSpacing().apply(setup)
@RxKotlinViewDsl inline fun ViewDsl.video(setup: VideoView.()->Unit = {}) = VideoView(context).applyDefaultSpacing().apply(setup)

@RxKotlinViewDsl inline fun ViewDsl.recyclerView(setup: RecyclerView.()->Unit = {}) = RecyclerView(context).applyDefaultSpacing().apply(setup)

@RxKotlinViewDsl fun ActivityAccess.dsl(make: ViewDsl.()->View): View = ViewDsl(context).let(make)

/*
Ideas for prebuilt components

divider
TODO add more
 */