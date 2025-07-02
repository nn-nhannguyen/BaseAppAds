package com.app.base.mvvm.view.dialog

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.app.base.mvvm.R
import com.app.base.mvvm.arch.extensions.setOnSingleClickListener
import com.app.base.mvvm.databinding.RequestViewAdDialogBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RequestViewAdDialog(
  private var title: String? = null,
  private var msg: String? = null
) : BaseBottomSheetDialogFragment(R.layout.request_view_ad_dialog) {

  private lateinit var binding: RequestViewAdDialogBinding
  private var requestViewAdListener: RequestViewAdListener? = null

  override fun applyBinding(viewDataBinding: ViewDataBinding) {
    binding = viewDataBinding as RequestViewAdDialogBinding
  }

  override fun onInit(view: View, fragmentArg: Bundle?, saveInstance: Bundle?) {
    initData()
  }

  private fun initData() {
    binding.apply {
      msg?.let {
        tvTitle.text = title
        tvMessage.text = msg
      }

      imgClose.setOnSingleClickListener { dismiss() }

      tvWatchAd.setOnClickListener {
        requestViewAdListener?.watchAd()
        dismiss()
      }

      tvCancel.setOnSingleClickListener {
        dismiss()
      }
    }
  }

  fun setOnMusicListener(listener: RequestViewAdListener) {
    requestViewAdListener = listener
  }

  override fun applyHeightDialog() {
    dialog?.also {
      val bottomSheet = it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
      bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
      val behavior = BottomSheetBehavior.from(bottomSheet)
      behavior.peekHeight = resources.displayMetrics.heightPixels
      view?.requestLayout()
    }
  }

  companion object {
    @JvmStatic
    fun newInstance() = RequestViewAdDialog().apply {
      arguments = Bundle().apply {}
    }
  }

  interface RequestViewAdListener {
    fun watchAd()
  }
}
