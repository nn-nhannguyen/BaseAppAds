package com.app.base.mvvm.view

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Class RecyclerTouchListener.
 *
 */

class RecyclerTouchListener(
  context: Context,
  recycleView: RecyclerView,
  private val clickListener: ClickListener?
) : RecyclerView.OnItemTouchListener {

  private var gestureDetector: GestureDetector? = null

  abstract class OnClickListener : ClickListener {
    override fun onClick(view: View, position: Int) {}

    override fun onLongClick(view: View?, position: Int) {}
  }

  interface ClickListener {
    fun onClick(view: View, position: Int)

    fun onLongClick(view: View?, position: Int)
  }

  init {
    gestureDetector =
      GestureDetector(
        context,
        object : GestureDetector.SimpleOnGestureListener() {
          override fun onSingleTapUp(e: MotionEvent): Boolean {
            return true
          }

          override fun onLongPress(e: MotionEvent) {
            recycleView.findChildViewUnder(e.x, e.y)?.apply {
              clickListener?.onLongClick(
                this,
                recycleView.getChildAdapterPosition(this)
              )
            }
          }
        }
      )
  }

  override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
    val child = rv.findChildViewUnder(e.x, e.y)
    child?.apply {
      if (gestureDetector?.onTouchEvent(e) == true) {
        clickListener?.onClick(this, rv.getChildAdapterPosition(this))
      }
    }

    return false
  }

  override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
  }

  override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
  }
}
