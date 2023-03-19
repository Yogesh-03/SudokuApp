package com.example.sudokugame.viewmodel

import androidx.lifecycle.ViewModel
import com.example.sudokugame.sudoku.SudokuGame

class PlaySudoku: ViewModel() {
    val sudokuGame = SudokuGame()
}