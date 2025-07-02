package com.app.base.mvvm.arch.extensions

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.app.base.mvvm.R
import com.app.base.mvvm.utils.ShareUtils

fun Activity.showKeyboard() {
  currentFocus?.let {
    val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    manager.showSoftInput(it, 0)
  }
}

fun Activity.hideKeyboard() {
  currentFocus?.let { view ->
    val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    manager.hideSoftInputFromWindow(view.windowToken, 0)
  }
}

fun Activity.showToast(message: String) {
  Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun FragmentActivity.showDialogFragment(fragment: DialogFragment) {
  fragment.show(supportFragmentManager, DialogFragment::class.java.simpleName)
}

fun reOpenApp(context: Context) {
  try {
    val pm = context.packageManager
    val launchIntent = pm.getLaunchIntentForPackage(context.packageName)
    // launchIntent!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    context.startActivity(launchIntent)
  } catch (e: Exception) {
    e.printStackTrace()
    Toast.makeText(context, "Can't open app", Toast.LENGTH_SHORT).show()
  }
}

fun Activity.feedBackApp() {
  ShareUtils.sendMail(
    this,
    getString(R.string.text_feedback) +
      " " + getString(R.string.app_name),
    getString(R.string.text_feedback)
  )
}
