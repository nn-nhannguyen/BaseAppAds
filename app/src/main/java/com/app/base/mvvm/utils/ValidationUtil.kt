package com.gh.app.ui.util

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.widget.TextView
import java.util.regex.Pattern

object ValidationUtil {

  /**
   * Method is used for set Font for textView
   * @param context  .
   * @param textView .
   */
  fun setFonts(context: Context, textView: TextView?) {
    val tf = Typeface.createFromAsset(
      context.assets,
      "fonts/Tolyer_Regular_no.3.ttf"
    )
    if (textView != null) {
      textView.typeface = tf
    }
  }

  fun isUserName(userName: String): Boolean {
    return !TextUtils.isEmpty(userName) && userName.length > 2
  }

  /**
   * Method is used for checking valid email id format.
   * @param email .
   * @return boolean true for valid false for invalid
   */
  fun isEmailValid(email: String): Boolean {
    val expression = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$"
    val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
    val matcher = pattern.matcher(email)
    return matcher.matches()
  }

  fun isNameInvalid(name: String): Boolean {
    // val expression = "[a-zA-Z.? ]*"
    val expression = "^[\\p{L} .'-]+$"
    val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
    val matcher = pattern.matcher(name)
    return matcher.matches()
  }

  fun isPassword(password: String?): Boolean {
    return password != null && password.length >= 6
  }

  fun isDisplayName(name: String): Boolean {
    return !TextUtils.isEmpty(name)
  }
}
