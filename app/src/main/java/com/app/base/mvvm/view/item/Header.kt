package com.app.base.mvvm.view.item

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.app.base.mvvm.R
import com.app.base.mvvm.databinding.LayoutHeaderBinding

/**
 * Class Header.
 *

 */
class Header @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

  private var mOnItemClickListener: OnItemClickListener? = null

  private lateinit var viewBinding: LayoutHeaderBinding

  init {
    initLayout(context, attrs)
  }

  private fun initLayout(context: Context, attributeSet: AttributeSet?) {
    // val inflate = LayoutInflater.from(context).inflate(R.layout.layout_header, this, false)
    // addView(inflate)
    viewBinding = LayoutHeaderBinding.inflate(LayoutInflater.from(context), this, true)
    updateView(context, attributeSet)
  }

  /**
   * Update data for view
   */
  @SuppressLint("Recycle")
  private fun updateView(context: Context, attributeSet: AttributeSet?) {
    val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.Header)

    val showBack = typedArray.getBoolean(R.styleable.Header_showBack, false)
    val showTitle = typedArray.getBoolean(R.styleable.Header_showTextTitle, false)
    val showTextRight = typedArray.getBoolean(R.styleable.Header_showTextRight, false)
    val showTextLeft = typedArray.getBoolean(R.styleable.Header_showTextLeft, false)

    val title = typedArray.getString(R.styleable.Header_text_title)
    val textRight = typedArray.getString(R.styleable.Header_text_right)

    viewBinding.apply {
      imgBack.visibility = if (showBack) View.VISIBLE else View.GONE
      tvTitle.visibility = if (showTitle) View.VISIBLE else View.GONE
      tvRight.visibility = if (showTextRight) View.VISIBLE else View.GONE
      tvLeft.visibility = if (showTextLeft) View.VISIBLE else View.GONE

      if (showTitle && !TextUtils.isEmpty(title)) {
        tvTitle.text = title
      }
      if (showTextRight && !TextUtils.isEmpty(textRight)) {
        tvRight.text = textRight
      }
    }

    initClick()
  }

  private fun initClick() {
    viewBinding.apply {
      imgBack.setOnClickListener {
        mOnItemClickListener?.onBack()
      }
      tvLeft.setOnClickListener {
        mOnItemClickListener?.onTextLeftClick()
      }

      tvRight.setOnClickListener {
        mOnItemClickListener?.onTextRightClick()
      }

      imgNext.setOnClickListener {
        mOnItemClickListener?.onNext()
      }
    }
  }

  /**
   * show or hide
   *
   * @param visible .
   */
  fun showBackButton(visible: Int) {
    viewBinding.imgBack.visibility = visible
  }

  /**
   * Set Title text
   *
   * @param text to set
   */
  fun setTextTitle(text: String) {
    viewBinding.tvTitle.text = text
  }

  /**
   * Set TextRight text
   *
   * @param text to set
   */
  fun setTextRight(text: String) {
    viewBinding.tvRight.text = text
  }

  /**
   * Set TextLeft text
   *
   * @param text to set
   */
  fun setTextLeft(text: String) {
    viewBinding.tvLeft.text = text
  }

  /**
   * @param onClickListener .
   * @return listener
   */
  fun setOnItemClickListener(onClickListener: OnItemClickListener): Header {
    mOnItemClickListener = onClickListener
    return this
  }

  fun setTextRightDrawable(drawableRes: Int) {
    viewBinding.tvRight.setCompoundDrawablesWithIntrinsicBounds(
      ContextCompat.getDrawable(context, drawableRes),
      null,
      null,
      null
    )
  }

  fun setTextLeftDrawable(drawableRes: Int) {
    viewBinding.tvLeft.setCompoundDrawablesWithIntrinsicBounds(
      ContextCompat.getDrawable(context, drawableRes),
      null,
      null,
      null
    )
  }

  /**
   * Simple click listener
   */
  abstract class SimpleHeaderClickListener : OnItemClickListener {

    override fun onBack() {}

    override fun onTextRightClick() {
    }

    override fun onNext() {
    }

    override fun onTextLeftClick() {
    }
  }

  /**
   * onClickListener
   */
  interface OnItemClickListener {
    fun onBack()
    fun onNext()
    fun onTextRightClick()
    fun onTextLeftClick()
  }
}
