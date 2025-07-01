package com.app.base.mvvm.view.dialog

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import com.app.base.mvvm.R
import com.app.base.mvvm.databinding.DialogBaseLayoutBinding

/**
 * alert dialog
 */
class AlertDialog(context: Context, private val mBuilder: Builder) :
  BaseDialog(context),
  View.OnClickListener {
  private lateinit var binding: DialogBaseLayoutBinding

  /**
   * Define an interface for this dialog
   */
  interface OnClickListener {
    fun onClick(v: View)
  }

  /**
   * Interface dismiss
   */
  interface OnDismissListener {
    fun onDismiss()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // setContentView(R.layout.dialog_base_layout)
    binding = DialogBaseLayoutBinding.inflate(layoutInflater)
    setContentView(binding.root)

    setAttributes()
    setCancelable(mBuilder.mCancelable)
    setCanceledOnTouchOutside(mBuilder.mCanceledOnTouchOutside)

    if (mBuilder.mTitle == null) {
      binding.tvTitle.visibility = View.GONE
    } else {
      binding.tvTitle.visibility = View.VISIBLE
      binding.tvTitle.text = mBuilder.mTitle
    }
    if (mBuilder.mMessage == null) {
      binding.tvMessage.visibility = View.GONE
    } else {
      binding.tvMessage.visibility = View.VISIBLE
      binding.tvMessage.text = mBuilder.mMessage
    }

    binding.btnPositive.text =
      if (mBuilder.mPositiveButtonText == null) {
        context.getString(android.R.string.ok)
      } else {
        mBuilder.mPositiveButtonText
      }

    if (mBuilder.mNegativeButtonText == null) {
      binding.btnNegative.visibility = View.GONE
      binding.viewBtnDivider.visibility = View.GONE
    } else {
      binding.viewBtnDivider.visibility = View.VISIBLE
      binding.btnNegative.visibility = View.VISIBLE
      binding.btnNegative.text = mBuilder.mNegativeButtonText
    }

    binding.btnPositive.setOnClickListener(this)
    binding.btnNegative.setOnClickListener(this)

    this.setOnDismissListener {
      mBuilder.mOnDismissListener?.onDismiss()
    }
  }

  override fun onClick(v: View) {
    when (v.id) {
      R.id.btnPositive -> {
        mBuilder.mPositiveButtonListener?.onClick(v)
        dismiss()
      }

      R.id.btnNegative -> {
        mBuilder.mNegativeButtonListener?.onClick(v)
        dismiss()
      }
    }
  }

  /**
   * Define a builder for this dialog
   */
  class Builder(private val mContext: Context) {
    var mTitle: String? = null
    var mMessage: String? = null
    var mPositiveButtonText: String? = null
    var mNegativeButtonText: String? = null
    var mPositiveButtonListener: OnClickListener? = null
    var mNegativeButtonListener: OnClickListener? = null
    var mOnDismissListener: OnDismissListener? = null
    var mCancelable = true // default is false
    var mCanceledOnTouchOutside = false // default is false

    fun setTitle(title: String): Builder {
      mTitle = title
      return this
    }

    fun setTitle(resId: Int): Builder {
      return setTitle(mContext.getString(resId))
    }

    fun setMessage(msg: String): Builder {
      mMessage = msg
      return this
    }

    fun setMessage(resId: Int): Builder {
      return setMessage(mContext.getString(resId))
    }

    fun setPositiveButtonText(text: String, l: OnClickListener?): Builder {
      mPositiveButtonText = text
      mPositiveButtonListener = l
      return this
    }

    fun setPositiveButtonText(resId: Int, l: OnClickListener?): Builder {
      return setPositiveButtonText(mContext.getString(resId), l)
    }

    fun setNegativeButtonText(text: String, l: OnClickListener?): Builder {
      mNegativeButtonText = text
      mNegativeButtonListener = l
      return this
    }

    fun setNegativeButtonText(resId: Int, l: OnClickListener?): Builder {
      return setNegativeButtonText(mContext.getString(resId), l)
    }

    fun setPositiveButtonListener(l: OnClickListener?): Builder {
      mPositiveButtonListener = l
      return this
    }

    fun setOnDismissListener(l: OnDismissListener?): Builder {
      mOnDismissListener = l
      return this
    }

    fun setCancelable(flag: Boolean): Builder {
      mCancelable = flag
      return this
    }

    fun setCanceledOnTouchOutside(flag: Boolean): Builder {
      mCanceledOnTouchOutside = flag
      return this
    }

    fun create(): AlertDialog {
      return AlertDialog(mContext, this)
    }

    fun show(): AlertDialog {
      val dialog = create()
      if (!(mContext as Activity).isFinishing) {
        dialog.show()
      }
      return dialog
    }
  }
}
