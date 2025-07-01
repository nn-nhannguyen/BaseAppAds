package com.app.base.mvvm.view.item

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * Class CustomViewPager.
 *

 */

class CustomViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {
  private var enabledViewPager: Boolean = false

  init {
    this.enabledViewPager = true
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    return this.enabledViewPager && super.onTouchEvent(event)
  }

  override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
    return this.enabledViewPager && super.onInterceptTouchEvent(event)
  }

  fun setPagingEnabled(enabled: Boolean) {
    this.enabledViewPager = enabled
  }
}
