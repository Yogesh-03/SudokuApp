package com.example.sudokugame.sudoku

class Cell(
    val row: Int,
    val col: Int,
    var value: Int,
    var isStartingCell: Boolean = false,
    var canValueChanged: Boolean = true,
    var notes: MutableSet<Int> = mutableSetOf<Int>()
)