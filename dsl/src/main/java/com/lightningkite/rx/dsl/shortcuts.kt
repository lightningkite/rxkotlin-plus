package com.lightningkite.rx.dsl

import android.view.Gravity
import android.view.inputmethod.EditorInfo

object InputTypes {
    const val email = EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
    const val name = EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME or EditorInfo.TYPE_TEXT_FLAG_CAP_WORDS
    const val paragraph = EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES or EditorInfo.TYPE_TEXT_VARIATION_LONG_MESSAGE
    const val sentence = EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES or EditorInfo.TYPE_TEXT_VARIATION_SHORT_MESSAGE
    const val naturalNumber = EditorInfo.TYPE_CLASS_NUMBER
    const val integer = EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_FLAG_SIGNED
    const val number = EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_FLAG_SIGNED or EditorInfo.TYPE_NUMBER_FLAG_DECIMAL
    const val password = EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
    const val pin = EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD
}

object Grav {
    const val topLeft = Gravity.TOP or Gravity.LEFT
    const val topStart = Gravity.TOP or Gravity.START
    const val topCenter = Gravity.TOP or Gravity.CENTER_HORIZONTAL
    const val topRight = Gravity.TOP or Gravity.RIGHT
    const val topEnd = Gravity.TOP or Gravity.END
    const val centerLeft = Gravity.CENTER_VERTICAL or Gravity.LEFT
    const val centerStart = Gravity.CENTER_VERTICAL or Gravity.START
    const val center = Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL
    const val centerRight = Gravity.CENTER_VERTICAL or Gravity.RIGHT
    const val centerEnd = Gravity.CENTER_VERTICAL or Gravity.END
    const val bottomLeft = Gravity.BOTTOM or Gravity.LEFT
    const val bottomStart = Gravity.BOTTOM or Gravity.START
    const val bottomCenter = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
    const val bottomRight = Gravity.BOTTOM or Gravity.RIGHT
    const val bottomEnd = Gravity.BOTTOM or Gravity.END
}