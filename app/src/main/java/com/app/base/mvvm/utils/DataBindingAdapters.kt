package com.app.base.mvvm.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.app.base.mvvm.R
import com.bumptech.glide.Glide

/**
 * DataBinding Adapters
 */
object DataBindingAdapters {

  @JvmStatic
  @BindingAdapter("app:nextIcon")
  fun setImageUri(view: ImageView, drawable: Drawable?) {
    if (drawable == null) {
      view.setImageResource(R.drawable.ic_done)
    } else {
      view.setImageDrawable(drawable)
    }
  }

  @JvmStatic
  @BindingAdapter("app:loadAvatar")
  fun loadAvatar(imageView: ImageView, url: String?) {
    Glide
      .with(imageView.context)
      // .with(AppApplication.instance.baseContext)
      .load(url)
      .placeholder(circularProgress(imageView.context))
      // .error(ContextCompat.getDrawable(imageView.context, R.drawable.ic_avatar))
      .into(imageView)
  }

  private fun circularProgress(context: Context): Drawable {
    return CircularProgressDrawable(context).apply {
      strokeWidth = 5f
      centerRadius = 30f
      clearColorFilter()
      setColorSchemeColors(ContextCompat.getColor(context, R.color.orange))
      start()
    }
  }

  @JvmStatic
  @BindingAdapter("app:maxHeight")
  fun maxHeightEditText(view: EditText, height: Float) {
    view.maxHeight = height.toInt()
  }

  @JvmStatic
  @BindingAdapter("app:emojiUnicode")
  fun emojiUnicodeTextView(view: TextView, unicode: Int) {
    view.text = StringUtils.getEmojiByUnicode(unicode)
  }
}
