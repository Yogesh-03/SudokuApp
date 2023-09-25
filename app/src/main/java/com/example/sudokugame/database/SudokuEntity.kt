package com.example.sudokugame.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sudoku_data")
data class SudokuEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "difficulty") val difficulty: String,
    @ColumnInfo(name = "time_taken") val timeTaken: String?,
    @ColumnInfo(name = "mistakes") val mistakes: Int?,
    @ColumnInfo(name = "is_completed") var isCompleted: Boolean?,
    @ColumnInfo(name = "hints_used") val hintsUsed: Int?,
    @ColumnInfo(name = "date") val date: String?
) {
}