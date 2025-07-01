package com.app.base.mvvm.view.item

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Class Class DividerItemDecoration Item view line below item of list.
 *

 */
class DividerItemDecoration : RecyclerView.ItemDecoration {
  private var mDivider: Drawable? = null
  private var mShowFirstDivider = false
  private var mShowLastDivider = false

  /**
   * Constructor of class.
   *
   * @param context Value context.
   * @param attrs   AttributeSet.
   */
  private constructor(context: Context, attrs: AttributeSet) {
    val a = context.obtainStyledAttributes(attrs, intArrayOf(android.R.attr.listDivider))
    mDivider = a.getDrawable(0)
    a.recycle()
  }

  /**
   * @param context          Value context.
   * @param attrs            AttributeSet.
   * @param showFirstDivider IsShow.
   * @param showLastDivider  IsShow.
   */
  constructor(
    context: Context,
    attrs: AttributeSet,
    showFirstDivider: Boolean,
    showLastDivider: Boolean
  ) : this(context, attrs) {

    mShowFirstDivider = showFirstDivider
    mShowLastDivider = showLastDivider
  }

  /**
   * @param divider Value Drawable.
   */
  constructor(divider: Drawable) {
    mDivider = divider
  }

  constructor(divider: Drawable, showFirstDivider: Boolean, showLastDivider: Boolean) : this(
    divider
  ) {

    mShowFirstDivider = showFirstDivider
    mShowLastDivider = showLastDivider
  }

  override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
    super.getItemOffsets(outRect, view, parent, state)

    mDivider?.apply {
      if (parent.getChildLayoutPosition(view) < 1) {
        return
      }

      if (getOrientation(parent) == LinearLayoutManager.VERTICAL) {
        outRect.top = intrinsicHeight
      } else {
        outRect.left = intrinsicWidth
      }
    }
  }

  override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    if (mDivider == null) {
      super.onDrawOver(c, parent, state)
      return
    }

    mDivider?.apply {
      // Initialization needed to avoid compiler warning.
      var left = 0
      var right = 0
      var top = 0
      var bottom = 0
      val size: Int
      val orientation = getOrientation(parent)
      val childCount = parent.childCount

      if (orientation == LinearLayoutManager.VERTICAL) {
        size = intrinsicHeight
        left = parent.paddingLeft
        right = parent.width - parent.paddingRight
      } else { // Horizontal.
        size = intrinsicWidth
        top = parent.paddingTop
        bottom = parent.height - parent.paddingBottom
      }

      for (i in (if (mShowFirstDivider) 0 else 1) until childCount) {
        val child = parent.getChildAt(i)
        val params = child.layoutParams as RecyclerView.LayoutParams

        if (orientation == LinearLayoutManager.VERTICAL) {
          if (isReverseLayout(parent)) {
            bottom = child.bottom - params.bottomMargin
            top = bottom - size
          } else {
            top = child.top - params.topMargin
            bottom = top + size
          }
        } else { // Horizontal.
          if (isReverseLayout(parent)) {
            right = child.right - params.rightMargin
            left = right - size
          } else {
            left = child.left - params.leftMargin
            right = left + size
          }
        }

        setBounds(left, top, right, bottom)
        draw(c)
      }

      // Show last divider.
      if (mShowLastDivider && childCount > 0) {
        val child = parent.getChildAt(childCount - 1)
        val params = child.layoutParams as RecyclerView.LayoutParams

        if (orientation == LinearLayoutManager.VERTICAL) {
          top = child.bottom + params.bottomMargin
          bottom = top + size
        } else { // Horizontal.
          left = child.right + params.rightMargin
          right = left + size
        }

        setBounds(left, top, right, bottom)
        draw(c)
      }
    }
  }

  /**
   * @param parent View parent.
   * @return Value int.
   */
  private fun getOrientation(parent: RecyclerView): Int {
    if (parent.layoutManager is LinearLayoutManager) {
      val layoutManager = parent.layoutManager as LinearLayoutManager?
      return layoutManager?.orientation ?: LinearLayoutManager.VERTICAL
    } else {
      throw IllegalStateException("DividerItemDecoration can only be used with a LinearLayoutManager.")
    }
  }

  /**
   * @param parent View parent.
   * @return If layout is reversed or not.
   */
  private fun isReverseLayout(parent: RecyclerView): Boolean {
    if (parent.layoutManager is LinearLayoutManager) {
      val layoutManager = parent.layoutManager as LinearLayoutManager?
      return layoutManager?.reverseLayout ?: false
    } else {
      throw IllegalStateException("DividerItemDecoration can only be used with a LinearLayoutManager.")
    }
  }
}
