package com.app.base.mvvm.view.item

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Class GridSpacingItemDecoration.
 *

 */
class GridSpacingItemDecoration private constructor(builder: Builder) :
  RecyclerView.ItemDecoration() {

  private val includeEdge: Boolean
  private var horizontalSpacing: Int = 0
  private var verticalSpacing: Int = 0
  private var gridLayoutManager: GridLayoutManager? = null

  init {
    includeEdge = builder.includeEdge
    val spacing = builder.spacing
    if (spacing != 0) {
      horizontalSpacing = spacing
      verticalSpacing = spacing
    } else {
      horizontalSpacing = builder.horizontalSpacing
      verticalSpacing = builder.verticalSpacing
    }
  }

  override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
    gridLayoutManager = gridLayoutManager ?: parent.layoutManager as GridLayoutManager

    gridLayoutManager?.apply {
      val spanCount = spanCount
      val position = parent.getChildAdapterPosition(view)
      val spanSize = spanSizeLookup.getSpanSize(position)
      val column = spanSizeLookup.getSpanIndex(position, spanCount)
      val totalChildCount = parent.adapter?.itemCount ?: 0
      val isLastRow = if (spanSize == 1) {
        position + spanCount - column > totalChildCount - 1
      } else {
        position - column / spanSize > totalChildCount - 1
      }
      val isFirstRow =
        spanSizeLookup.getSpanGroupIndex(position, spanCount) == 0

      if (includeEdge) {
        outRect.left = horizontalSpacing - column * horizontalSpacing / spanCount
        outRect.right = (column + spanSize) * horizontalSpacing / spanCount
        outRect.top = verticalSpacing
        outRect.bottom = if (isLastRow) verticalSpacing else 0
      } else {
        outRect.left = column * horizontalSpacing / spanCount
        outRect.right =
          horizontalSpacing - (column + spanSize) * horizontalSpacing / spanCount
        outRect.top = if (isFirstRow) 0 else verticalSpacing
      }
    }
  }

  class Builder() {
    var includeEdge: Boolean = false
    var spacing = 0
    var verticalSpacing: Int = 0
    var horizontalSpacing: Int = 0

    fun includeEdge(includeEdge: Boolean): Builder {
      this.includeEdge = includeEdge
      return this
    }

    fun spacing(spacing: Int): Builder {
      this.spacing = spacing
      return this
    }

    fun verticalSpacing(verticalSpacing: Int): Builder {
      this.verticalSpacing = verticalSpacing
      return this
    }

    fun horizontalSpacing(horizontalSpacing: Int): Builder {
      this.horizontalSpacing = horizontalSpacing
      return this
    }

    fun build(): GridSpacingItemDecoration {
      return GridSpacingItemDecoration(this)
    }
  }

  companion object {
    fun newBuilder(): Builder {
      return Builder()
    }
  }
}
