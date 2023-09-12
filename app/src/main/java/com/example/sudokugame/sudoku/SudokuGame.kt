package com.example.sudokugame.sudoku

import android.content.SharedPreferences
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.example.sudokugame.sharedpreferences.UserSettings
import com.example.sudokugame.sudokugenerator.DifficultyLevel
import com.example.sudokugame.sudokugenerator.Generator
import com.example.sudokugame.sudokugenerator.Solver

class SudokuGame {
    var selectedCellLiveData = MutableLiveData<Pair<Int,Int>>()
    val cellsLiveData = MutableLiveData<List<Cell>>()
    val isTakingNotesLiveData = MutableLiveData<Boolean>()
    val highlightedKeysLiveData = MutableLiveData<Set<Int>>()
    val currentDifficultyLevel = MutableLiveData<String>()




    private var selectedRow = -1;
    private var selectedCol = -1;
    private var isTakingNotes = false
    var cellList:MutableList<Int> = Generator.Builder().setLevel(DifficultyLevel.EASY).build().sudokuList()

     val board: Board
    init {
        val cells = List(9*9){ i-> Cell(i/9,i%9, cellList[i])}
        board = Board(9,cells)
        for(i in 0 until cellList.size){
            if(cellList[i]!=0){
                Log.d("index", i.toString())
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

        val curNotes = if (isTakingNotes) {
            board.getCell(selectedRow, selectedCol).notes
        } else {
            setOf<Int>()
        }
        highlightedKeysLiveData.postValue(curNotes)
    }

    fun delete() {
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