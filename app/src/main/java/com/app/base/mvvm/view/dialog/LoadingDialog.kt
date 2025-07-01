package com.app.base.mvvm.view.dialog

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.app.base.mvvm.R

/**
 * LoadingDialog
 */
class LoadingDialog(context: Context) : BaseDialog(context) {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.dialog_progress)
    setAttributes()
    setCancelable(false)
    setCanceledOnTouchOutside(false)

    val pro = findViewById<ProgressBar>(R.id.progressBar)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      pro.indeterminateDrawable.colorFilter =
        BlendModeColorFilter(
          ContextCompat.getColor(context, R.color.colorAccent),
          BlendMode.SRC_ATOP
        )
    } else {
      pro.indeterminateDrawable.setColorFilter(
        ContextCompat.getColor(
          context,
          R.color.colorAccent
        ),
        PorterDuff.Mode.MULTIPLY
      )
    }
  }
}
