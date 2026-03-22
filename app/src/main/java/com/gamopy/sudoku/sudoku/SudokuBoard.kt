package com.gamopy.sudoku.sudoku

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.gamopy.sudoku.R
import com.gamopy.sudoku.sharedpreferences.UserSettings
import kotlin.math.min


class SudokuBoard(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    // Constants
    private var sqrtSize = 3
    private var size = 9

    // these are set in onDraw
    private var cellSizePixels = 0F
    private var noteSizePixels = 0F

    //Initial value of selectedRow and selectedColumn
    private var selectedRow = -1
    private var selectedCol = -1

    private var listener: OnTouchListener? = null
    private var cells: List<Cell>? = null


    // Thick lines separating boxes
    private val thickLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.thick_line_paint)
        strokeWidth = 4F
    }

    //Cell animation Paint
    private val cellAnimationPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = ContextCompat.getColor(context, R.color.hint_cell_paint)
    }

    //Outer Border Lines Paint
    private val borderPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.border_paint)
        strokeWidth = 8F
    }

    // Thin lines separating cells
    private val thinLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.thin_line_paint)
        strokeWidth = 2F
    }

    //Wrong Input Cell Color
    private val wrongInputColor = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = ContextCompat.getColor(context, R.color.wrong_input_paint)

    }

    // Color of SELECTED CELL
    private val selectedCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = ContextCompat.getColor(context, R.color.selected_cell_paint)
    }

    // Paint of SELECTED CELL BOX and ROW and COLUMN
    private val conflictingCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = ContextCompat.getColor(getContext(), R.color.conflicting_cell_paint)
    }

    // Background Paint of CELL
    private val naturalCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = ContextCompat.getColor(context, R.color.natural_cell_paint)
    }

    // TextPaint
    private val textPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = ContextCompat.getColor(context, R.color.text_paint)
    }

    // Wrong Text Paint
    private val wrongTextPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = ContextCompat.getColor(context, R.color.wrong_text_paint)
    }

    // Starting Cell Text Paint
    //Starting cell is cell which are pre filled
    private val startingCellTextPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = ContextCompat.getColor(context, R.color.starting_cell_text_paint)
        // setShadowLayer(0.5F, 0.1F,0.1F, ContextCompat.getColor(context, R.color.starting_cell_text_paint))
    }

    // Pencil Notes Text Paint
    private val noteTextPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = ContextCompat.getColor(context, R.color.note_text_paint)
    }

    // Measure the view and its content to determine the measured width and the measured height
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sizePixels = min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(sizePixels - 40, sizePixels - 40)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        updateMeasurements(width)
        fillCells(canvas)
        drawLines(canvas)
        drawText(canvas)
    }

    //Setting Measurements cellSize, textSizes, pencilNotesSize, sudoku
    // Size, etc
    private fun updateMeasurements(width: Int) {
        cellSizePixels = width / size.toFloat()
        noteSizePixels = cellSizePixels / sqrtSize.toFloat()
        noteTextPaint.textSize = cellSizePixels / sqrtSize.toFloat()
        textPaint.textSize = cellSizePixels / 1.5F
        startingCellTextPaint.textSize = cellSizePixels / 1.5F
    }

    //Defines background of each cell
    private fun fillCells(canvas: Canvas) {
        cells?.forEach {
            val r = it.row
            val c = it.col


            fillCell(canvas, r, c, naturalCellPaint)
            if (r == selectedRow && c == selectedCol) {
                fillCell(canvas, r, c, selectedCellPaint)
            }

            //Checking is Region Highlight enabled in settings
            else if (context.getSharedPreferences(
                    UserSettings().PREFERENCES,
                    AppCompatActivity.MODE_PRIVATE
                ).getBoolean(
                    UserSettings().HIGHLIGHT_WRONG_INPUT,
                    UserSettings().getCustomHighlightWrongInput()
                )
            ) {
                if (r == selectedRow || c == selectedCol) {
                    fillCell(canvas, r, c, conflictingCellPaint)
                } else if (r / sqrtSize == selectedRow / sqrtSize && c / sqrtSize == selectedCol / sqrtSize) {
                    fillCell(canvas, r, c, conflictingCellPaint)
                }
            }

            //Checking is Highlight Same Number enabled in settings
            if (context.getSharedPreferences(
                    UserSettings().PREFERENCES,
                    AppCompatActivity.MODE_PRIVATE
                ).getBoolean(
                    UserSettings().HIGHLIGHT_SAME_NUMBERS,
                    UserSettings().getCustomHighlightSameNumbers()
                )
            ) {
                if (selectedRow >= 0 && selectedCol >= 0) {
                    //Highlighting cells with same values
                    if (it.value == cells!![9 * selectedRow + selectedCol].value) {
                        if (it.value != 0) {
                            fillCell(canvas, r, c, selectedCellPaint)
                        }
                    }
                }

                //If cell has wrong value
                if (it.hasWrongValue) {
                    fillCell(canvas, r, c, wrongInputColor)
                }
            }

            it.notes.forEach { note ->
                val rowInCell = (note - 1) / sqrtSize
                val colInCell = (note - 1) % sqrtSize
                val valueString = note.toString()

                //Checking is Highlight Same Number enabled in settings
                if (context.getSharedPreferences(
                        UserSettings().PREFERENCES,
                        AppCompatActivity.MODE_PRIVATE
                    ).getBoolean(
                        UserSettings().HIGHLIGHT_SAME_NUMBERS,
                        UserSettings().getCustomHighlightSameNumbers()
                    )
                ) {
                    if (selectedRow >= 0 && selectedCol >= 0) {
                        //Highlighting cell notes with same values
                        if (valueString == cells!![9 * selectedRow + selectedCol].value.toString()) {
                            if (valueString != "0") {
                                if (it.canHighlightNotes) {
                                    fillCell(canvas, r, c, rowInCell, colInCell, selectedCellPaint)
                                }
                            }
                        }
                    }
                }
            }
        }
        if (selectedRow >= 0 && selectedCol >= 0) {
            if (cells?.get(9 * selectedRow + selectedCol)?.isHint == true) {
                drawRectAnimation(canvas, selectedRow, selectedCol, cellAnimationPaint)
                cells?.get(9 * selectedRow + selectedCol)?.isHint = false
            }
        }
    }

    private fun drawRectAnimation(canvas: Canvas, r: Int, c: Int, paint: Paint) {
        canvas.drawRect(
            c * cellSizePixels,
            r * cellSizePixels,
            (c + 1) * cellSizePixels,
            (r + 1) * cellSizePixels,
            paint
        )
    }

    // Fills individual cell completely
    private fun fillCell(canvas: Canvas, r: Int, c: Int, paint: Paint) {
        canvas.drawRect(
            c * cellSizePixels,
            r * cellSizePixels,
            (c + 1) * cellSizePixels,
            (r + 1) * cellSizePixels,
            paint
        )
    }

    // Fills notes background of a cell
    private fun fillCell(canvas: Canvas, r: Int, c: Int, rIC: Int, cIC: Int, paint: Paint) {
        canvas.drawRect(
            (c * cellSizePixels) + (cIC * noteSizePixels),
            (r * cellSizePixels) + (rIC * noteSizePixels),
            ((c) * cellSizePixels) + ((cIC + 1) * noteSizePixels),
            ((r) * cellSizePixels) + ((rIC + 1) * noteSizePixels),
            paint
        )
    }

    //Draws thick and thin lines
    private fun drawLines(canvas: Canvas) {
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), borderPaint)

        for (i in 1 until size) {
            val paintToUse = when (i % sqrtSize) {
                0 -> thickLinePaint
                else -> thinLinePaint
            }

            canvas.drawLine(
                i * cellSizePixels,
                0F,
                i * cellSizePixels,
                height.toFloat(),
                paintToUse
            )
            canvas.drawLine(0F, i * cellSizePixels, width.toFloat(), i * cellSizePixels, paintToUse)
        }
    }

    //Draws text inside cell
    private fun drawText(canvas: Canvas) {
        cells?.forEach { cell ->
            val value = cell.value

            //For Empty cells
            if (value == 0) {
                // draw notes
                cell.notes.forEach { note ->
                    val rowInCell = (note - 1) / sqrtSize
                    val colInCell = (note - 1) % sqrtSize
                    val valueString = note.toString()

                    val textBounds = Rect()
                    noteTextPaint.getTextBounds(valueString, 0, valueString.length, textBounds)
                    val textWidth = noteTextPaint.measureText(valueString)
                    val textHeight = textBounds.height()
                    canvas.drawText(
                        valueString,
                        (cell.col * cellSizePixels) + (colInCell * noteSizePixels) + noteSizePixels / 2 - textWidth / 2f,
                        (cell.row * cellSizePixels) + (rowInCell * noteSizePixels) + noteSizePixels / 2 + textHeight / 2f,
                        noteTextPaint
                    )
                }
            } else {
                val row = cell.row
                val col = cell.col
                val valueString = cell.value.toString()
                val paintToUse = if (cell.isStartingCell) {
                    startingCellTextPaint
                } else if (cell.hasWrongValue) {
                    wrongTextPaint
                } else {
                    textPaint
                }
                val textBounds = Rect()
                paintToUse.getTextBounds(valueString, 0, valueString.length, textBounds)
                val textWidth = paintToUse.measureText(valueString)
                val textHeight = textBounds.height()
                paintToUse.textSize = (cellSizePixels / 1.4F)

                canvas.drawText(
                    valueString,
                    ((col * cellSizePixels) + cellSizePixels / 2 - textWidth / 2),
                    ((row * cellSizePixels) + cellSizePixels / 2 + textHeight / 2),
                    paintToUse
                )
            }
        }
    }

    private fun isInside(v: View, e: MotionEvent): Boolean {
        return !(e.x < 0 || e.y < 0 || e.x > v.measuredWidth || e.y > v.measuredHeight)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isInside(this, event)) {
            return when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    handleTouchEvent(event.x, event.y)
                    true
                }

                //On scrolling screen position will be changed
                MotionEvent.ACTION_MOVE -> {
                    handleTouchEvent(event.x, event.y)
                    true
                }

                MotionEvent.ACTION_UP -> {
                    handleTouchEvent(event.x, event.y)
                    performClick()
                    true
                }

                else -> false
            }
        }
        return false
    }

    override fun performClick(): Boolean {
        return super.performClick()

    }

    private fun handleTouchEvent(x: Float, y: Float) {
        val possibleSelectedRow = (y / cellSizePixels).toInt()
        val possibleSelectedCol = (x / cellSizePixels).toInt()
        listener?.onCellTouched(possibleSelectedRow, possibleSelectedCol)
        invalidate()
    }

    fun updateSelectedCellUI(row: Int, col: Int) {
        selectedRow = row
        selectedCol = col
        invalidate()
    }

    fun updateCells(cells: List<Cell>) {
        this.cells = cells
        invalidate()
    }

    fun registerListener(listener: OnTouchListener) {
        this.listener = listener
    }

    interface OnTouchListener {
        fun onCellTouched(row: Int, col: Int)
    }
}