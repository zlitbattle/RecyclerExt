package com.zl.recyclerviewext.decoration

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.core.view.children
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zl.recyclerviewext.orientation

class GridSpacingItemDecoration(
    @ColorInt
    val color: Int,
    val spanCount: Int,
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int,
    val includeEdge: Boolean = false,
) : RecyclerView.ItemDecoration() {
    val mPaint = Paint().apply {
        setColor(this@GridSpacingItemDecoration.color)
        style = Paint.Style.FILL
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        for (child in parent.children) {
            val curPosition = parent.getChildAdapterPosition(child)
            val isFirst = isFirstRow(curPosition, parent)
            val isLast = isLastRow(curPosition, parent)

            when (parent.orientation) {
                LinearLayout.HORIZONTAL -> {
                    val farTop = isFirstInRow(curPosition, parent)
                    val farBottom = isLastInRow(curPosition, parent)
                    if (includeEdge || !isFirst) {
                        drawLeft(c, child)
                    }
                    if (includeEdge || !isLast) {
                        drawRight(c, child)
                    }
                    if (includeEdge || !farTop) {
                        drawTop(c, child)
                    }
                    if (includeEdge || !farBottom) {
                        drawBottom(c, child)
                    }
                }

                LinearLayout.VERTICAL -> {
                    val farLeft = isFirstInRow(curPosition, parent)
                    val farRight = isLastInRow(curPosition, parent)
                    if (includeEdge || !isFirst) {
                        drawTop(c, child)
                    }
                    if (includeEdge || !isLast) {
                        drawBottom(c, child)
                    }
                    if (includeEdge || !farLeft) {
                        drawLeft(c, child)
                    }
                    if (includeEdge || !farRight) {
                        drawRight(c, child)
                    }
                }
            }
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val curPosition = parent.getChildAdapterPosition(view)
        val isFirst = isFirstRow(curPosition, parent)
        val isLast = isLastRow(curPosition, parent)

        when (parent.orientation) {
            LinearLayout.HORIZONTAL -> {
                val farTop = isFirstInRow(curPosition, parent)
                val farBottom = isLastInRow(curPosition, parent)
                val leftSpace = if (isFirst && !includeEdge) 0 else left
                val rightSpace = if (isLast && !includeEdge) 0 else right
                val topSpace = if (includeEdge || !farTop) top else 0
                val bottomSpace = if (includeEdge || !farBottom) bottom else 0
                outRect.set(leftSpace, topSpace, rightSpace, bottomSpace)
            }

            LinearLayout.VERTICAL -> {
                val farLeft = isFirstInRow(curPosition, parent)
                val farRight = isLastInRow(curPosition, parent)
                val leftSpace = if (includeEdge || !farLeft) left else 0
                val rightSpace = if (includeEdge || !farRight) right else 0
                val topSpace = if (isFirst && !includeEdge) 0 else top
                val bottomSpace = if (isLast && !includeEdge) 0 else bottom
                outRect.set(leftSpace, topSpace, rightSpace, bottomSpace)
            }
        }
    }

    private fun drawTop(canvas: Canvas, child: View) {
        val topRect = Rect(child.left, child.top - top, child.right, child.top)
        canvas.drawRect(topRect, mPaint)
    }

    private fun drawBottom(canvas: Canvas, child: View) {
        val topRect = Rect(child.left, child.bottom, child.right, child.bottom + bottom)
        canvas.drawRect(topRect, mPaint)
    }

    private fun drawLeft(canvas: Canvas, child: View) {
        val topRect = Rect(child.left - left, child.top, child.left, child.bottom)
        canvas.drawRect(topRect, mPaint)
    }

    private fun drawRight(canvas: Canvas, child: View) {
        val topRect = Rect(child.right, child.top, child.right + right, child.bottom)
        canvas.drawRect(topRect, mPaint)
    }

    fun isLastRow(position: Int, parent: RecyclerView): Boolean {
        val layoutManager = parent.layoutManager as GridLayoutManager
        val itemCount = parent.adapter?.itemCount ?: return false
        val spanSizeLookup = layoutManager.spanSizeLookup

        var spanSum = 0
        var lastRowFirstPos = 0

        for (i in 0 until itemCount) {
            val spanSize = spanSizeLookup.getSpanSize(i)
            if (spanSum + spanSize > spanCount) {
                spanSum = 0
            }
            if (spanSum == 0) {
                lastRowFirstPos = i
            }
            spanSum += spanSize
        }
        return position >= lastRowFirstPos
    }

    fun isFirstRow(position: Int, parent: RecyclerView): Boolean {
        val layoutManager = parent.layoutManager as? GridLayoutManager ?: return false
        val spanCount = layoutManager.spanCount
        val itemCount = parent.adapter?.itemCount ?: return false
        val spanSizeLookup = layoutManager.spanSizeLookup

        var spanSum = 0
        for (i in 0 until itemCount) {
            spanSum += spanSizeLookup.getSpanSize(i)
            if (spanSum > spanCount) {
                // 已经到第二行
                return position < i
            }
        }
        // 如果 item 数量本来就不足一行，那么所有的都是第一行
        return true
    }

    fun isFirstInRow(position: Int, parent: RecyclerView): Boolean {
        val layoutManager = parent.layoutManager as? GridLayoutManager ?: return false
        val spanSizeLookup = layoutManager.spanSizeLookup
        val spanIndex = spanSizeLookup.getSpanIndex(position, layoutManager.spanCount)
        return spanIndex == 0
    }

    fun isLastInRow(position: Int, parent: RecyclerView): Boolean {
        val layoutManager = parent.layoutManager as? GridLayoutManager ?: return false
        val spanSizeLookup = layoutManager.spanSizeLookup
        val spanSize = spanSizeLookup.getSpanSize(position)
        val spanIndex = spanSizeLookup.getSpanIndex(position, layoutManager.spanCount)
        return spanIndex + spanSize == layoutManager.spanCount
    }

}