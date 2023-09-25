package com.example.sudokugame.sudoku

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.MutableLiveData
import com.example.sudokugame.R
import com.example.sudokugame.sudokugenerator.DifficultyLevel
import com.example.sudokugame.sudokugenerator.Generator
import com.example.sudokugame.sudokugenerator.Solver

class SudokuGame() {
    var selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()
    val cellsLiveData = MutableLiveData<List<Cell>>()
    val isTakingNotesLiveData = MutableLiveData<Boolean>()
    val isTimerVisible = MutableLiveData<Boolean>()
    val highlightedKeysLiveData = MutableLiveData<Set<Int>>()
    var mistakes = MutableLiveData<Int>()
    var remainingValuesCount = MutableLiveData(0)
    var availableHints = MutableLiveData(1)
    var theme = MutableLiveData<String>()

    private var selectedRow = -1
    private var selectedCol = -1
    private var isTakingNotes = false
    private var sdkBoard = Generator.Builder().setLevel(DifficultyLevel.EASY).build()
    private var cellList: MutableList<Int> = sdkBoard.sudokuList()
    private val board: Board
    private var cc: Array<IntArray>
    private lateinit var sudokuSolution: MutableList<Int>
    private var remainingNumber = MutableList(9) { 9 }


    init {
        val cells = List(9 * 9) { i -> Cell(i / 9, i % 9, cellList[i]) }
        board = Board(9, cells)
        mistakes.postValue(0)

        val chunks = cellList.chunked(9)
        val chunkedArray: Array<List<Int>> = chunks.toTypedArray()
        cc = Array(9) { IntArray(9) }
        cc[0] = chunkedArray[0].toIntArray()
        for (i in 0 until 9) {
            cc[i] = chunkedArray[i].toIntArray()
        }

        if (Solver().solveSudoku(cc)) {
            sudokuSolution = Solver().returnBoard(cc)
            Solver().printBoard(cc)
        }
        for (i in cellList) {
            if (i != 0) {
                remainingNumber[i - 1]--
            } else {
                remainingValuesCount.value = remainingValuesCount.value?.plus(1)
            }
        }

        for (i in 0 until cellList.size) {
            if (cellList[i] != 0) {
                cells[i].isStartingCell = true
                cells[i].canValueChanged = false
            }
        }
        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        cellsLiveData.postValue(board.cells)
        isTakingNotesLiveData.postValue(isTakingNotes)
    }


    fun changeTheme(string: String){
        theme.postValue(string)
    }
    fun handleInput(number: Int) {
        if (selectedRow == -1 || selectedCol == -1) return
        val cell = board.getCell(selectedRow, selectedCol)
        val cells = board.getCellList()
        if (cell.isStartingCell) return

        if (isTakingNotes) {
            if (cell.notes.contains(number)) {
                cell.notes.remove(number)
            } else {
                cells.forEach {
                    val r = it.row
                    val c = it.col
                    if (!it.canValueChanged){
                        if (r == selectedRow || c == selectedCol){
                            if (it.value!=number){
                                cell.notes.add(number)
                            } else {
                                cell.notes.remove(number)
                                return
                            }
                        }
                        if (r / 3 == selectedRow / 3 && c / 3 == selectedCol / 3){
                            if (it.value!=number){
                                cell.notes.add(number)
                            } else {
                                cell.notes.remove(number)
                                return
                            }

                        }
                    }
                }
            }
            highlightedKeysLiveData.postValue(cell.notes)
        } else {
            if (cell.canValueChanged) {
                cell.value = number
                cell.notes.clear()
                if (sudokuSolution[9 * selectedRow + selectedCol] != number) {
                    mistakes.value = mistakes.value?.plus(1)
                    cell.canValueChanged = true
                    cell.hasWrongValue = true
                } else {
                    remainingNumber[number - 1]--
                    remainingValuesCount.value = remainingValuesCount.value?.minus(1)
                    cell.canValueChanged = false
                    cell.hasWrongValue = false
                    cells.forEach {
                        val r = it.row
                        val c = it.col
                        if (r == selectedRow || c == selectedCol){
                           if (it.notes.contains(number)) it.notes.remove(number)
                            if (r / 3 == selectedRow / 3 && c / 3 == selectedCol / 3){
                                if (it.notes.contains(number)) it.notes.remove(number)
                            }
                        }
                    }
                }
            }

        }
        cellsLiveData.postValue(board.cells)
    }


    fun updateSelectedCell(row: Int, col: Int) {
        val cell = board.getCell(row, col)
        if (!cell.canValueChanged || !cell.isStartingCell) {
            selectedRow = row
            selectedCol = col
            selectedCellLiveData.postValue(Pair(row, col))

            if (isTakingNotes) {
                highlightedKeysLiveData.postValue(cell.notes)
            }
        }
    }


    fun changeNoteTakingState() {
        isTakingNotes = !isTakingNotes
        isTakingNotesLiveData.postValue(isTakingNotes)
        val cell = board.getCell(selectedRow, selectedCol)
        if (selectedRow >= 0 && selectedCol >= 0) {
            if (cell.canValueChanged) {
                val curNotes = if (isTakingNotes) {
                    board.getCell(selectedRow, selectedCol).notes
                } else {
                    setOf()
                }
                highlightedKeysLiveData.postValue(curNotes)
            }
        }
    }

    fun delete() {
        if (selectedRow >= 0 && selectedCol >= 0) {
            val cell = board.getCell(selectedRow, selectedCol)
            if (!cell.canValueChanged) return
            else if (isTakingNotes) {
                cell.notes.clear()
                highlightedKeysLiveData.postValue(setOf())
            } else {
                if (!cell.isStartingCell) cell.value = 0
            }
            cellsLiveData.postValue(board.cells)
        }
    }

    fun exitGameMistakes() {
        remainingNumber.replaceAll { 9 }
    }

    fun gameCompleted() {

    }

    fun getRemainingNumberCount():MutableList<Int>{
        return remainingNumber
    }
}