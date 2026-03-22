package com.gamopy.sudoku.model

data class UserAction(
    val row: Int,
    val col: Int,
    val value: Int,
    val hasWrongValue: Boolean,
    val canValueChanged: Boolean,
    val actionType: ActionType,
    val list: MutableList<Int?>?
)