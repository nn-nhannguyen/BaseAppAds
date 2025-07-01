package com.app.base.mvvm.arch.extensions

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.regex.Pattern

fun View.setOnSingleClickListener(delayMillis: Int = 1000, listener: () -> Unit) {
  var pressedTime = 0L
  setOnClickListener {
    if (System.currentTimeMillis() - pressedTime < delayMillis) return@setOnClickListener
    pressedTime = System.currentTimeMillis()
    listener()
  }
}

fun BottomNavigationView.disableTooltip() {
  val content: View = getChildAt(0)
  if (content is ViewGroup) {
    content.forEach {
      it.setOnLongClickListener {
        return@setOnLongClickListener true
      }
      // disable vibration also
      it.isHapticFeedbackEnabled = false
    }
  }
}

fun TextView.setSocialView(resLayout: Int) {
  this.setCompoundDrawablesWithIntrinsicBounds(resLayout, 0, 0, 0)
  this.compoundDrawablePadding = 8
}

fun View.beInvisibleIf(beInvisible: Boolean) = if (beInvisible) beInvisible() else beVisible()

fun View.beVisibleIf(beVisible: Boolean) = if (beVisible) beVisible() else beGone()

fun View.beGoneIf(beGone: Boolean) = beVisibleIf(!beGone)

fun View.beInvisible() {
  visibility = View.INVISIBLE
}

fun View.beVisible() {
  visibility = View.VISIBLE
}

fun View.beGone() {
  visibility = View.GONE
}

fun View.beDisable() {
  isEnabled = false
}

fun View.beEnable() {
  isEnabled = true
}

fun View.fadeAnimation() {
  alpha = 0f
  animate()
    .alpha(1f)
    .setDuration(700)
    .start()
}

fun View.fadeAnimationAfterLoading() {
  alpha = 0.3f
  animate()
    .alpha(1f)
    .setDuration(700)
    .start()
}

fun TextView.toSpan(colorSpan: Int, textFull: String, textNeedToSpan: String) {
  val p = Pattern.compile(textNeedToSpan)
  val matcher = p.matcher(textFull)
  val spannable = SpannableStringBuilder(textFull)

  while (matcher.find()) {
    spannable.setSpan(
      ForegroundColorSpan(colorSpan),
      matcher.start(),
      matcher.end(),
      Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
  }
  text = spannable
}

fun RecyclerView.setVertically() {
  layoutManager = LinearLayoutManager(context)
}

fun RecyclerView.setHorizontally() {
  layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
}

fun RecyclerView.disableAnimation() {
  (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
}

fun ImageView.setImageViewTintColor(color: Int) {
  setColorFilter(ContextCompat.getColor(context, color))
}
