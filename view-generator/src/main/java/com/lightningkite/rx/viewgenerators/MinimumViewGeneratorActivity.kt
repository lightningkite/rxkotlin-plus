package com.lightningkite.rx.viewgenerators

import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.FocusFinder
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import com.lightningkite.rx.R

/**
 * An activity that implements [ActivityAccess].
 *
 * Created by jivie on 10/12/15.
 */
abstract class MinimumViewGeneratorActivity : AccessibleActivity() {

    // main needs to be a getter that gets it's value from a static source
    // That can be froma companion object or a top level object, or any where else
    // you desire.
    abstract val main: ViewGenerator
    lateinit var view: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        view = main.generate(this)

        setContentView(view)
    }

}
