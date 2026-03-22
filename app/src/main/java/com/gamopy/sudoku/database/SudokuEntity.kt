package com.gamopy.sudoku.database

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
)

@Entity(tableName = "sudoku_easy_data")
data class SudokuEasyData(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "time_taken") val timeTaken: String?,
    @ColumnInfo(name = "mistakes") val mistakes: Int?,
    @ColumnInfo(name = "is_completed") var isCompleted: Boolean?,
    @ColumnInfo(name = "time") val time: Int?
)

@Entity(tableName = "sudoku_medium_data")
data class SudokuMediumData(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "time_taken") val timeTaken: String?,
    @ColumnInfo(name = "mistakes") val mistakes: Int?,
    @ColumnInfo(name = "is_completed") var isCompleted: Boolean?,
    @ColumnInfo(name = "time") val time: Int?
)

@Entity(tableName = "sudoku_hard_data")
data class SudokuHardData(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "time_taken") val timeTaken: String?,
    @ColumnInfo(name = "mistakes") val mistakes: Int?,
    @ColumnInfo(name = "is_completed") var isCompleted: Boolean?,
    @ColumnInfo(name = "time") val time: Int?
)

@Entity(tableName = "sudoku_Expert_data")
data class SudokuExpertData(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "time_taken") val timeTaken: String?,
    @ColumnInfo(name = "mistakes") val mistakes: Int?,
    @ColumnInfo(name = "is_completed") var isCompleted: Boolean?,
    @ColumnInfo(name = "time") val time: Int?
)