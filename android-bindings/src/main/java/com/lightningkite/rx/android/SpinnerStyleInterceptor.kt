package com.lightningkite.rx.android

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.graphics.fonts.FontStyle
import android.os.Build
import android.util.AttributeSet
import android.util.SparseIntArray
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.core.util.set
import io.github.inflationx.viewpump.InflateResult
import io.github.inflationx.viewpump.Interceptor

object SpinnerStyleInterceptor : Interceptor {

    private val getter = AttributeGetter(
        listOfNotNull(
            android.R.attr.textSize,
            android.R.attr.fontFamily,
            android.R.attr.typeface,
            android.R.attr.textStyle,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) android.R.attr.textFontWeight else null,
            android.R.attr.textColor,
            android.R.attr.letterSpacing,
            android.R.attr.lineSpacingMultiplier,
            R.attr.entryTextSize,
            R.attr.entryFontFamily,
            R.attr.entryTypeface,
            R.attr.entryTextStyle,
            R.attr.entryTextFontWeight,
            R.attr.entryTextColor,
            R.attr.entryPaddingLeft,
            R.attr.entryPaddingStart,
            R.attr.entryPaddingRight,
            R.attr.entryPaddingEnd,
            R.attr.entryPaddingTop,
            R.attr.entryPaddingBottom,
            R.attr.entryPaddingHorizontal,
            R.attr.entryPaddingVertical,
            R.attr.entryPadding,
            R.attr.entryLetterSpacing,
            R.attr.entryLineSpacingMultiplier,
        ).toIntArray()
    )

    @SuppressLint("ResourceType")
    override fun intercept(chain: Interceptor.Chain): InflateResult {
        val result = chain.proceed(chain.request())
        result.view?.let { it as? Spinner }?.let { v ->
            val t = getter.get(result.context, result.attrs)
            v.spinnerTextStyle = SpinnerTextStyle(
                textSize = t.getDimension(R.attr.entryTextSize, t.getDimension(android.R.attr.textSize, 14f * result.context.resources.displayMetrics.scaledDensity)),
                textColor = t.getColor(R.attr.entryTextColor, t.getColor(android.R.attr.textColor, 0xFF000000.toInt())),
                paddingLeft = t.getDimension(R.attr.entryPaddingLeft, t.getDimension(R.attr.entryPaddingStart, t.getDimension(R.attr.entryPaddingHorizontal, t.getDimension(R.attr.entryPadding, 8f * result.context.resources.displayMetrics.density)))).toInt(),
                paddingTop = t.getDimension(R.attr.entryPaddingTop, t.getDimension(R.attr.entryPaddingVertical, t.getDimension(R.attr.entryPadding, 8f * result.context.resources.displayMetrics.density))).toInt(),
                paddingRight = t.getDimension(R.attr.entryPaddingRight, t.getDimension(R.attr.entryPaddingEnd, t.getDimension(R.attr.entryPaddingHorizontal, t.getDimension(R.attr.entryPadding, 8f * result.context.resources.displayMetrics.density)))).toInt(),
                paddingBottom = t.getDimension(R.attr.entryPaddingBottom, t.getDimension(R.attr.entryPaddingVertical, t.getDimension(R.attr.entryPadding, 8f * result.context.resources.displayMetrics.density))).toInt(),
                typeface = setTypefaceFromAttrs(
                    familyName = t.getString(R.attr.entryFontFamily) ?: t.getString(android.R.attr.fontFamily),
                    typefaceIndex = t.getInt(R.attr.entryTypeface, t.getInt(android.R.attr.typeface, DEFAULT_TYPEFACE)),
                    style = t.getInt(R.attr.entryTextStyle, t.getInt(android.R.attr.textStyle, 0)),
                    weight = t.getInt(R.attr.entryTextFontWeight, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) t.getInt(android.R.attr.textFontWeight, -1) else -1)
                ),
                letterSpacing = t.getFloat(R.attr.entryLetterSpacing, t.getFloat(android.R.attr.letterSpacing, -1f)).takeUnless { it == -1f },
                lineSpacingMultiplier = t.getFloat(R.attr.entryLineSpacingMultiplier, t.getFloat(android.R.attr.lineSpacingMultiplier, -1f)).takeUnless { it == -1f },
            )
            t.recycle()
        }
        return result
    }

    private const val DEFAULT_TYPEFACE = -1
    private const val SANS = 1
    private const val SERIF = 2
    private const val MONOSPACE = 3

    private fun setTypefaceFromAttrs(
        familyName: String?,
        typefaceIndex: Int,
        style: Int,
        weight: Int
    ): Typeface? {
        return if (familyName != null) {
            val normalTypeface = Typeface.create(familyName, Typeface.NORMAL)
            resolveStyleAndSetTypeface(normalTypeface, style, weight)
        } else when (typefaceIndex) {
            SANS -> resolveStyleAndSetTypeface(Typeface.SANS_SERIF, style, weight)
            SERIF -> resolveStyleAndSetTypeface(Typeface.SERIF, style, weight)
            MONOSPACE -> resolveStyleAndSetTypeface(Typeface.MONOSPACE, style, weight)
            DEFAULT_TYPEFACE -> resolveStyleAndSetTypeface(null, style, weight)
            else -> resolveStyleAndSetTypeface(null, style, weight)
        }
    }

    private fun resolveStyleAndSetTypeface(
        typeface: Typeface? = null,
        style: Int,
        weight: Int
    ): Typeface? {
        if (weight >= 0) {
            val italic = style and Typeface.ITALIC != 0
            return (Typeface.create(typeface, weight, italic))
        } else {
            return typeface
        }
    }

    private class NRT(val typedArray: TypedArray, val mapping: SparseIntArray) {
        fun getInt(key: Int, default: Int): Int = typedArray.getInt(mapping[key], default)
        fun getDimension(key: Int, default: Float): Float = typedArray.getDimension(mapping[key], default)
        fun getColor(key: Int, default: Int): Int = typedArray.getColor(mapping[key], default)
        fun getString(key: Int): String? = typedArray.getString(mapping[key])
        fun getFloat(key: Int, default: Float): Float = typedArray.getFloat(mapping[key], default)
        fun recycle() = typedArray.recycle()
    }
    private class AttributeGetter(keys: IntArray) {
        val sortedKeys = keys.sorted().toIntArray()
        val map = SparseIntArray(sortedKeys.size)
        init {
            sortedKeys.forEachIndexed { index, it ->
                map[it] = index
            }
        }
        fun get(context: Context, attrs: AttributeSet?): NRT {
            val t = context.theme.obtainStyledAttributes(attrs, sortedKeys, 0, 0)
            return NRT(t, map)
        }
    }
}

