package com.app.base.mvvm.view.item

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.app.base.mvvm.R

/**
 * Class CustomImageView.
 *

 */

class CustomImageView @SuppressLint("Recycle")
constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
  AppCompatImageView(context, attrs, defStyleAttr) {

  private val mIsDefaultHeight: Boolean

  @JvmOverloads
  constructor(context: Context, attrs: AttributeSet? = null) : this(context, attrs, 0)

  init {
    val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomImageView)
    mIsDefaultHeight = typedArray.getBoolean(R.styleable.CustomImageView_defaultHeight, false)
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    if (mIsDefaultHeight) {
      setMeasuredDimension(measuredHeight * 16 / 9, measuredHeight)
      return
    }
    setMeasuredDimension(measuredWidth, measuredWidth * 9 / 16)
  }
}
