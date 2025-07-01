package com.app.base.mvvm.view.item

import android.annotation.SuppressLint
import android.content.Context
import android.text.InputType
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.app.base.mvvm.R
import com.app.base.mvvm.arch.extensions.beGone
import com.app.base.mvvm.arch.extensions.beVisible
import com.app.base.mvvm.databinding.ItemInputBinding

/**
 * Class InputItem.
 *

 */
class InputItem @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

  private lateinit var binding: ItemInputBinding

  private var mIsShowEye = false
  private var mIsPassWord = false

  init {
    initLayout(context, attrs)
  }

  private fun initLayout(context: Context, attributeSet: AttributeSet?) {
    // val inflate = LayoutInflater.from(context).inflate(R.layout.item_input, this)
    // addView(inflate)
    binding = ItemInputBinding.inflate(LayoutInflater.from(context), this, true)
    updateView(context, attributeSet)
    initListener()
  }

  /**
   * Update data for view
   */
  @SuppressLint("Recycle")
  private fun updateView(context: Context, attributeSet: AttributeSet?) {
    val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.InputItem)
    val showEditText = typedArray.getBoolean(R.styleable.InputItem_showEditText, true)

    mIsPassWord = typedArray.getBoolean(R.styleable.InputItem_isPassword, false)

    val isTextView = typedArray.getBoolean(R.styleable.InputItem_isTextView, false)
    val isLabelWrapContent =
      typedArray.getBoolean(R.styleable.InputItem_isLabelWrapContent, false)

    mIsShowEye = typedArray.getBoolean(R.styleable.InputItem_showEye, false)

    val label = typedArray.getString(R.styleable.InputItem_text_label)
    val textHint = typedArray.getString(R.styleable.InputItem_text_input_hint)
    val textInput = typedArray.getString(R.styleable.InputItem_text_input)

    binding.tvLabel.visibility = View.VISIBLE
    if (!TextUtils.isEmpty(label)) {
      binding.tvLabel.text = label
    }

    binding.edtInput.apply {
      if (showEditText) beVisible() else beGone()

      if (isLabelWrapContent) {
        setLabelWrapContent()
      }

      if (showEditText && !TextUtils.isEmpty(textHint)) {
        hint = textHint
      }

      if (isTextView) {
        inputType = InputType.TYPE_NULL
        return
      }

      inputType = if (mIsPassWord) {
        InputType.TYPE_TEXT_VARIATION_PASSWORD
      } else {
        InputType.TYPE_CLASS_TEXT
      }

      if (mIsPassWord) {
        transformationMethod = PasswordTransformationMethod.getInstance()
      }

      if (!TextUtils.isEmpty(textInput)) {
        setText(textInput)
      }
    }
  }

  private fun initListener() {
    binding.apply {
      imgEye.setOnClickListener {
        imgEye.isSelected = !imgEye.isSelected
        if (imgEye.isSelected) {
          edtInput.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
          edtInput.transformationMethod = PasswordTransformationMethod.getInstance()
        }
        edtInput.setSelection(edtInput.text.length)
      }

      edtInput.setOnFocusChangeListener { _, b ->
        if (mIsShowEye) {
          imgEye.visibility = if (b) View.VISIBLE else View.GONE
          if (!b) {
            edtInput.transformationMethod = PasswordTransformationMethod.getInstance()
            imgEye.isSelected = false
          } else {
            edtInput.inputType =
              if (mIsPassWord) InputType.TYPE_TEXT_VARIATION_PASSWORD else InputType.TYPE_CLASS_TEXT
          }
        }
      }
    }
  }

  /**
   * Change width of label
   */
  private fun setLabelWrapContent() {
    val layoutParams = RelativeLayout.LayoutParams(
      RelativeLayout.LayoutParams.WRAP_CONTENT,
      RelativeLayout.LayoutParams.MATCH_PARENT
    )
    layoutParams.setMargins(
      0,
      0,
      resources.getDimension(R.dimen.input_label_margin_right_small).toInt(),
      0
    )
    binding.tvLabel.layoutParams = layoutParams
  }

  /**
   * Change width of label
   */
  fun setLabelWidth(widthInput: Int) {
    var width = widthInput
    if (width == 0) {
      width = resources.getDimension(R.dimen.input_label_width_small).toInt()
    }
    val layoutParams =
      RelativeLayout.LayoutParams(width, RelativeLayout.LayoutParams.MATCH_PARENT)
    layoutParams.setMargins(
      0,
      0,
      resources.getDimension(R.dimen.input_label_margin_right_small).toInt(),
      0
    )
    binding.tvLabel.layoutParams = layoutParams
  }

  fun enableInput(enable: Boolean) {
    binding.edtInput.isEnabled = enable
  }

  fun setInputText(text: String) {
    binding.edtInput.setText(text)
  }

  fun edtInput(): EditText = binding.edtInput
}
