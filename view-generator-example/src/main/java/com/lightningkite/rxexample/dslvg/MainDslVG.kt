package com.lightningkite.rxexample.dslvg

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import com.airbnb.paris.extensions.*
import com.lightningkite.rxexample.R
import com.lightningkite.rx.dsl.*
import com.lightningkite.rx.viewgenerators.ActivityAccess
import com.lightningkite.rx.viewgenerators.EntryPoint
import com.lightningkite.rx.viewgenerators.ViewGenerator
class MainDslVG: ViewGenerator, EntryPoint {

    @OptIn(RxKotlinViewDsl::class)
    override fun generate(dependency: ActivityAccess): View {
        return dependency.dsl {
            columnTopStart(text {
                    text = resources.getString(R.string.app_name)
                    style {
                        backgroundRes(R.color.colorAccent)
                        matchWidth()
                        textSizeDp(20)
                        paddingDp(8)
                        layoutMarginDp(0)
                        gravity = Gravity.CENTER
                    }
                },
                frame(
                    columnCenter(
                        text {},
                        text {}
                    )
                ).apply { weight = 1f }
            )
        }
    }
}

@OptIn(RxKotlinViewDsl::class)
private class PreviewMain(context: Context, attrs: AttributeSet? = null) : VgPreview(context, attrs, MainDslVG())