package com.example.sudokugame.sudoku

import androidx.lifecycle.MutableLiveData
import com.example.sudokugame.sudokugenerator.DifficultyLevel
import com.example.sudokugame.sudokugenerator.Generator
import com.example.sudokugame.sudokugenerator.Solver

class SudokuGame {
    var selectedCellLiveData = MutableLiveData<Pair<Int,Int>>()
    val cellsLiveData = MutableLiveData<List<Cell>>()
    val isTakingNotesLiveData = MutableLiveData<Boolean>()
    val isTimerVisible = MutableLiveData<Boolean>()
    val highlightedKeysLiveData = MutableLiveData<Set<Int>>()

    private var selectedRow = -1
    private var selectedCol = -1
    private var isTakingNotes = false

    private var sdkBoard = Generator.Builder().setLevel(DifficultyLevel.EASY).build()
    private var cellList:MutableList<Int>  = sdkBoard.sudokuList()
     private val board: Board
     private  var cc:Array<IntArray>

     companion object{
         lateinit var sudokuSolution:MutableList<Int>
         var mistakes = MutableLiveData<Int>()
         var textSize = MutableLiveData<Float>()
     }
    init {
        val cells = List(9*9){ i-> Cell(i/9,i%9, cellList[i])}
        board = Board(9,cells)
        mistakes.postValue(0)
        textSize.postValue(0F)

        val chunks = cellList.chunked(9)
        val chunkedArray: Array<List<Int>> = chunks.toTypedArray()
        cc = Array(9){IntArray(9)}
        cc[0] = chunkedArray[0].toIntArray()
        for (i in 0 until 9){
            cc[i] = chunkedArray[i].toIntArray()
        }

        if (Solver().solveSudoku(cc)) {
            sudokuSolution = Solver().returnBoard(cc)
            Solver().printBoard(cc)
        }

        for(i in 0 until cellList.size){
            if(cellList[i]!=0){
                cells[i].isStartingCell = true
                cells[i].canValueChanged = false
            }
        }
        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        cellsLiveData.postValue(board.cells)
        isTakingNotesLiveData.postValue(isTakingNotes)
    }

    fun handleInput(number: Int) {
        if (selectedRow == -1 || selectedCol == -1) return
        val cell = board.getCell(selectedRow, selectedCol)
        val cellList = board.getCellList()
        if (cell.isStartingCell) return

        if (isTakingNotes) {
            if (cell.notes.contains(number)) {
                cell.notes.remove(number)
            } else {
                cell.notes.add(number)
            }
            highlightedKeysLiveData.postValue(cell.notes)
        } else {
            cell.value = number
            if (sudokuSolution[9*selectedRow+selectedCol] != number){
                mistakes.value = mistakes.value?.plus(1)
            }
        }
        cellsLiveData.postValue(board.cells)
    }

    fun updateSelectedCell(row:Int, col:Int){
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

        if (selectedRow>=0 && selectedCol>=0){
            val curNotes = if (isTakingNotes) {
                board.getCell(selectedRow, selectedCol).notes
            } else {
                setOf()
            }
            highlightedKeysLiveData.postValue(curNotes)
        }

    }

    fun delete() {
        if(selectedRow>=0 && selectedCol>=0){
            val cell = board.getCell(selectedRow, selectedCol)
            if (isTakingNotes) {
                cell.notes.clear()
                highlightedKeysLiveData.postValue(setOf())
            } else {
                if(!cell.isStartingCell) cell.value = 0
            }
            cellsLiveData.postValue(board.cells)
        }
    }
}