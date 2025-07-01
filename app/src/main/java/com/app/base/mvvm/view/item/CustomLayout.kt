package com.app.base.mvvm.view.item

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

/**
 * Class CustomLayout.
 *

 */

class CustomLayout @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    val width = measuredWidth
    setMeasuredDimension(width, width * 9 / 16)
  }
}
