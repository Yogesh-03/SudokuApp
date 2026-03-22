package com.gamopy.sudoku.sudoku

/**
 * Defines properties of cell
 *
 * @property row                -> Row index of cell
 * @property col                -> Column index of cell
 * @property value              -> value inside the cell
 * @property isStartingCell     -> cells with prefilled values
 * @property canValueChanged    -> can we change value of cell or not
 * @property hasWrongValue      -> cells having wrong input values
 * @property notes              -> set of notes(pencil notes)  inside a cell
 */
class Cell(
    val row: Int,
    val col: Int,
    var value: Int,
    var isStartingCell: Boolean = false,
    var canValueChanged: Boolean = true,
    var hasWrongValue:Boolean = false,
    var notes: MutableSet<Int> = mutableSetOf(),
    var canHighlightNotes:Boolean = true,
    var isHint:Boolean = false
)