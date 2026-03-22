package com.gamopy.sudoku.viewmodel

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.gamopy.sudoku.sudoku.SudokuGame

class PlaySudoku(application: Application, cont:String, bundle: Bundle): ViewModel(){
    val sudokuGame = SudokuGame(application, cont, bundle)
}