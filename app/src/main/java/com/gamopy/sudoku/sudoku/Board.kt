package com.gamopy.sudoku.sudoku

class Board(private val size: Int, val cells: List<Cell>) {
    fun getCell(row: Int, col: Int) = cells[row * size + col]
    fun getCellList() = cells
}