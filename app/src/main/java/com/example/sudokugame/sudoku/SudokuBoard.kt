package com.example.sudokugame.sudoku

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.sudokugame.R
import com.example.sudokugame.sharedpreferences.ThemeAndFont
import com.example.sudokugame.sharedpreferences.UserSettings
import kotlin.math.min


class SudokuBoard(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {


    // Constants
    private var sqrtSize = 3
    private var size = 9

    // these are set in onDraw
    private var cellSizePixels = 0F
    private var noteSizePixels = 0F

    private var selectedRow = -1
    private var selectedCol = -1

    private var listener: OnTouchListener? = null
    private var cells: List<Cell>? = null


    // Thick lines separating boxes
    private val thickLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 4F
    }

    private val borderPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 8F
    }

    // Thin lines separating cells
    private val thinLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.GRAY
        strokeWidth = 2F
    }

    //Wrong Input Cell Color
    private val wrongInputColor = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#ffcccb")

    }

    // Color of selected Cell
    private val selectedCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#ADD8E6")
    }

    // Paint of selected Cell BOX and row and column
    private val conflictingCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = ContextCompat.getColor(getContext(), R.color.light_gray)
    }

    // Background Paint of Cell
    private val naturalCellPain = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.WHITE
    }

    // TextPaint
    private val textPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLUE
    }

    // Wrong Text Paint
    private val wrongTextPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.RED
    }

    // Starting Cell Text Paint
    private val startingCellTextPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK
    }

    // Pencil Notes Text Paint
    private val noteTextPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.GRAY
    }

    // Starting Cell Paint
    private val startingCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#acacac")
    }

    // Measure the view and its content to determine the measured width and the measured height
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sizePixels = min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(sizePixels - 40, sizePixels - 40)
    }

    override fun onDraw(canvas: Canvas) {
        //super.onDraw(canvas)
        updateMeasurements(width)
        fillCells(canvas)
        drawLines(canvas)
        drawText(canvas)
    }

    //Setting Measurements cellSize, textSizes, pencilNotesSize, sudokuBoardSize, etc
    private fun updateMeasurements(width: Int) {
        cellSizePixels = width / size.toFloat()
        noteSizePixels = cellSizePixels / sqrtSize.toFloat()
        noteTextPaint.textSize = cellSizePixels / sqrtSize.toFloat()
        textPaint.textSize = cellSizePixels / 1.5F
        startingCellTextPaint.textSize = cellSizePixels / 1.5F
    }


    private fun fillCells(canvas: Canvas) {
        if (selectedRow >= 0 && selectedCol >= 0) {
            val cellValue = cells!![9 * selectedRow + selectedCol].value
            if (cellValue != 0) {
                if (cellValue != SudokuGame.sudokuSolution[9 * selectedRow + selectedCol]) {
                    cells!![9 * selectedRow + selectedCol].hasWrongValue = true
                    fillCell(canvas, selectedRow, selectedCol, wrongInputColor)
                } else {
                    cells!![9 * selectedRow + selectedCol].hasWrongValue = false
                }
            }
        }

        cells?.forEach {
            val r = it.row
            val c = it.col
            fillCell(canvas, r, c, naturalCellPain)
            if (r == selectedRow && c == selectedCol) {
                fillCell(canvas, r, c, selectedCellPaint)
            } else if (r == selectedRow || c == selectedCol) {
                fillCell(canvas, r, c, conflictingCellPaint)
            } else if (r / sqrtSize == selectedRow / sqrtSize && c / sqrtSize == selectedCol / sqrtSize) {
                fillCell(canvas, r, c, conflictingCellPaint)
            }

            if (context.getSharedPreferences(
                    UserSettings().PREFERENCES,
                    AppCompatActivity.MODE_PRIVATE
                ).getBoolean(
                    UserSettings().HIGHLIGHT_SAME_NUMBERS,
                    UserSettings().getCustomHighlightSameNumbers()
                )
            ) {
                if (selectedRow >= 0 && selectedCol >= 0) {
                    if (it.value == cells!![9 * selectedRow + selectedCol].value) {
                        if (it.value != 0) {
                            fillCell(canvas, r, c, selectedCellPaint)
                        }
                    }
                }
                if (it.hasWrongValue) {
                    fillCell(canvas, r, c, wrongInputColor)
                }
            }

            it.notes.forEach { note ->
                val rowInCell = (note - 1) / sqrtSize
                val colInCell = (note - 1) % sqrtSize
                val valueString = note.toString()
                Log.d("ValueString", valueString)
                if (context.getSharedPreferences(
                        UserSettings().PREFERENCES,
                        AppCompatActivity.MODE_PRIVATE
                    ).getBoolean(
                        UserSettings().HIGHLIGHT_SAME_NUMBERS,
                        UserSettings().getCustomHighlightSameNumbers()
                    )
                ) {
                    if (selectedRow >= 0 && selectedCol >= 0) {
                        if (valueString == cells!![9 * selectedRow + selectedCol].value.toString()) {
                            if (valueString != "0") {
                                fillCell(canvas, r, c, rowInCell, colInCell, selectedCellPaint)
                            }
                        }
                    }
                }
            }
        }
    }

    // Fills cell completely
    private fun fillCell(canvas: Canvas, r: Int, c: Int, paint: Paint) {
        canvas.drawRect(
            c * cellSizePixels,
            r * cellSizePixels,
            (c + 1) * cellSizePixels,
            (r + 1) * cellSizePixels,
            paint
        )
    }

    // Fills notes of a cell
    private fun fillCell(canvas: Canvas, r: Int, c: Int, rIC: Int, cIC: Int, paint: Paint) {
        canvas.drawRect(
            (c * cellSizePixels) + (cIC * noteSizePixels),
            (r * cellSizePixels) + (rIC * noteSizePixels),
            ((c) * cellSizePixels) + ((cIC + 1) * noteSizePixels),
            ((r) * cellSizePixels) + ((rIC + 1) * noteSizePixels),
            paint
        )
    }

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

            canvas.drawLine(
                0F,
                i * cellSizePixels,
                width.toFloat(),
                i * cellSizePixels,
                paintToUse
            )
        }
    }

    private fun drawText(canvas: Canvas) {
        cells?.forEach { cell ->
            val value = cell.value

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
                //val textWidth = 12F
                val textHeight = textBounds.height()
                //val textHeight = 12F
                paintToUse.textSize =
                    (cellSizePixels / updateFontSize(ThemeAndFont().getCurrentTextSize())) + SudokuGame.textSize.value!!

//                if (SudokuGame.textSize.value != 0F){
//                    paintToUse.textSize = 100F
//                }

                canvas.drawText(
                    valueString, ((col * cellSizePixels) + cellSizePixels / 2 - textWidth / 2),
                    ((row * cellSizePixels) + cellSizePixels / 2 + textHeight / 2), paintToUse
                )
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                handleTouchEvent(event.x, event.y)
                true
            }

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

    override fun performClick(): Boolean {
        return super.performClick()
    }

    private fun handleTouchEvent(x: Float, y: Float) {
//        selectedRow= (y/cellSizePixels).toInt()
//        selectedCol = (x/cellSizePixels).toInt()
        val possibleSelectedRow = (y / cellSizePixels).toInt()
        val possibleSelectedCol = (x / cellSizePixels).toInt()
        //invalidate()
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

    private fun updateFontSize(fontSize: Float): Float {
        return fontSize
    }

    interface OnTouchListener {
        fun onCellTouched(row: Int, col: Int)
    }
}