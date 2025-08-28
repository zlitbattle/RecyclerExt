package com.zl.recyclerviewext.decoration

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.zl.recyclerviewext.orientation

class LinearLayoutManagerSpacingItemDecoration(
    @ColorInt
    val color: Int,
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int,
    val includeEdge: Boolean = false
) : RecyclerView.ItemDecoration() {
    val mPaint = Paint().apply {
        setColor(this@LinearLayoutManagerSpacingItemDecoration.color)
        style = Paint.Style.FILL
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val maxPosition = (parent.adapter?.itemCount ?: 0) - 1
        for (child in parent.children) {
            val curPosition = parent.getChildAdapterPosition(child)
            val isFirst = curPosition == 0
            val isLast = curPosition == maxPosition

            when (parent.orientation) {
                LinearLayout.HORIZONTAL -> {
                    if (includeEdge || !isFirst) {
                        drawLeft(c, child)
                    }
                    if (includeEdge || !isLast) {
                        drawRight(c, child)
                    }
                    if (includeEdge) {
                        drawTop(c, child)
                        drawBottom(c, child)
                    }
                }

                LinearLayout.VERTICAL -> {
                    if (includeEdge || !isFirst) {
                        drawTop(c, child)
                    }
                    if (includeEdge || !isLast) {
                        drawBottom(c, child)
                    }
                    if (includeEdge) {
                        drawLeft(c, child)
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
        val maxPosition = (parent.adapter?.itemCount ?: 0) - 1
        val isFirst = curPosition == 0
        val isLast = curPosition == maxPosition

        when (parent.orientation) {
            LinearLayout.HORIZONTAL -> {
                val leftSpace = if (isFirst && !includeEdge) 0 else left
                val rightSpace = if (isLast && !includeEdge) 0 else right
                val topSpace = if (includeEdge) top else 0
                val bottomSpace = if (includeEdge) bottom else 0
                outRect.set(leftSpace, topSpace, rightSpace, bottomSpace)
            }

            LinearLayout.VERTICAL -> {
                val leftSpace = if (includeEdge) left else 0
                val rightSpace = if (includeEdge) right else 0
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
}