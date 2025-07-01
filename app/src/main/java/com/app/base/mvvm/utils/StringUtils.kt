package com.app.base.mvvm.utils

import android.text.Html
import android.text.Spanned
import com.app.base.mvvm.App

object StringUtils {
  fun getString(resId: Int, vararg formatArgs: Any): String {
    return App.instance.getString(resId, *formatArgs)
  }

  fun addString(firstString: String, secondString: String): String {
    val compare = firstString.compareTo(secondString)
    if (compare <= 0) {
      return firstString + secondString
    }
    return secondString + firstString
  }

  fun getEmojiByUnicode(unicode: Int): String {
    return String(Character.toChars(unicode))
  }

  fun getSpannedText(text: String): Spanned? {
    return Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
  }
}
