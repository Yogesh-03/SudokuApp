package com.gamopy.sudoku.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.gamopy.sudoku.R

class SelectionView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var selectionRect = Rect(100, 100, 500, 500)  // Default rectangle
    private val density = resources.displayMetrics.density
    private val rectPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.blue)
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    private val cornerCirclePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.black)
        style = Paint.Style.FILL
    }

    private val cornerCircleRadius = 10 * density

    private var resizingEdge: ResizingEdge? = null
    private val touchTolerance = (40 * density).toInt() // Tolerance for detecting edge touches

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Draw the rectangle
        canvas.drawRect(selectionRect, rectPaint)
        // Draw circles at the corners
        drawCornerCircles(canvas)
    }

    private fun drawCornerCircles(canvas: Canvas) {
        val corners = arrayOf(
            selectionRect.left to selectionRect.top,
            selectionRect.right to selectionRect.top,
            selectionRect.left to selectionRect.bottom,
            selectionRect.right to selectionRect.bottom
        )
        corners.forEach { (x, y) ->
            canvas.drawCircle(x.toFloat(), y.toFloat(), cornerCircleRadius, cornerCirclePaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                resizingEdge = getTouchedEdge(event.x.toInt(), event.y.toInt())
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                resizingEdge?.let {
                    resizeRect(it, event.x.toInt(), event.y.toInt())
                    invalidate()
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                resizingEdge = null
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun getTouchedEdge(x: Int, y: Int): ResizingEdge? {
        return when {
            isNear(x, selectionRect.left) && isNear(y, selectionRect.top) -> ResizingEdge.TOP_LEFT
            isNear(x, selectionRect.right) && isNear(y, selectionRect.top) -> ResizingEdge.TOP_RIGHT
            isNear(x, selectionRect.left) && isNear(y, selectionRect.bottom) -> ResizingEdge.BOTTOM_LEFT
            isNear(x, selectionRect.right) && isNear(y, selectionRect.bottom) -> ResizingEdge.BOTTOM_RIGHT
            isNear(x, selectionRect.left) -> ResizingEdge.LEFT
            isNear(x, selectionRect.right) -> ResizingEdge.RIGHT
            isNear(y, selectionRect.top) -> ResizingEdge.TOP
            isNear(y, selectionRect.bottom) -> ResizingEdge.BOTTOM
            else -> null
        }
    }

    private fun isNear(touch: Int, edge: Int): Boolean {
        return kotlin.math.abs(touch - edge) <= touchTolerance
    }

    private fun resizeRect(edge: ResizingEdge, x: Int, y: Int) {
        when (edge) {
            ResizingEdge.TOP_LEFT -> {
                selectionRect.left = x.coerceIn(0, selectionRect.right - 1)
                selectionRect.top = y.coerceIn(0, selectionRect.bottom - 1)
            }
            ResizingEdge.TOP_RIGHT -> {
                selectionRect.right = x.coerceIn(selectionRect.left + 1, width)
                selectionRect.top = y.coerceIn(0, selectionRect.bottom - 1)
            }
            ResizingEdge.BOTTOM_LEFT -> {
                selectionRect.left = x.coerceIn(0, selectionRect.right - 1)
                selectionRect.bottom = y.coerceIn(selectionRect.top + 1, height)
            }
            ResizingEdge.BOTTOM_RIGHT -> {
                selectionRect.right = x.coerceIn(selectionRect.left + 1, width)
                selectionRect.bottom = y.coerceIn(selectionRect.top + 1, height)
            }
            ResizingEdge.LEFT -> selectionRect.left = x.coerceIn(0, selectionRect.right - 1)
            ResizingEdge.RIGHT -> selectionRect.right = x.coerceIn(selectionRect.left + 1, width)
            ResizingEdge.TOP -> selectionRect.top = y.coerceIn(0, selectionRect.bottom - 1)
            ResizingEdge.BOTTOM -> selectionRect.bottom = y.coerceIn(selectionRect.top + 1, height)
        }
    }

    fun getSelectionRect(): Rect {
        return selectionRect
    }

    enum class ResizingEdge {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, LEFT, RIGHT, TOP, BOTTOM
    }
}
