package com.app.base.mvvm.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.app.base.mvvm.R

/**
 * Base dialog
 */
abstract class BaseDialogFragment : DialogFragment() {
  private var mContext: Context? = null

  abstract val layoutResource: Int

  private val dialogTheme: Int
    get() = R.style.AllDialogAppCompatAlert

  abstract fun onInit(view: View, fragmentArg: Bundle?, saveInstance: Bundle?)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NO_TITLE, dialogTheme)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(layoutResource, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    onInit(view, arguments, savedInstanceState)
  }

  override fun onResume() {
    super.onResume()
    mContext?.let {
      //  onDialogSize((ScreenUtil.getWidthScreen(it) * 0.96).toInt(), 0)
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    mContext = null
  }

  /* override fun onActivityCreated(savedInstanceState: Bundle?) {
       super.onActivityCreated(savedInstanceState)
       if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
           mContext = activity
       }
   }*/

  /* fun setResultCallback(resultCode: Int, bundle: Bundle?) {
         val targetFragment = targetFragment
         if (targetFragment != null) {
             val intent = Intent()
             if (bundle != null) {
                 intent.putExtra(ConstantUtil.ARG_BUNDLE, bundle)
             }
             targetFragment.onActivityResult(targetRequestCode, resultCode, intent)
         }
     }*/

//  private fun onDialogSize(w: Int, h: Int) {
//    val window = dialog?.window
//    window?.setLayout(w, if (h <= 0) ViewGroup.LayoutParams.WRAP_CONTENT else h)
//  }
}
