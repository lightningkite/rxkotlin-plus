package com.lightningkite.rx.viewgenerators

import android.content.Context
import android.os.Bundle
import android.view.View
import io.github.inflationx.viewpump.ViewPump
import io.github.inflationx.viewpump.ViewPumpContextWrapper

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
