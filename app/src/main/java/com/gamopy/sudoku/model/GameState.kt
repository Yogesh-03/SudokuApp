package com.gamopy.sudoku.model

import com.gamopy.sudoku.sudoku.Cell

data class GameState(
    val sudokuList: List<Cell>,
    val elapsedTime: Int,
    val userActionHistory: MutableList<UserAction>,
    val mistakes: Int,
    val hintsRemaining:Int,
    val seconds:Int,
    val running:Boolean,
    val wasRunning:Boolean,
    val difficultyLevel:String?
)