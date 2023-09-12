package com.example.sudokugame

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time
import java.util.Date

@Entity(tableName = "sudoku_data")
data class SudokuEntity(@PrimaryKey(autoGenerate = true) var id:Int = 0,
                        @ColumnInfo(name = "date") val date:Date,
                        @ColumnInfo(name = "time_taken") val timeTaken:Time?,
                        @ColumnInfo(name = "hints_used") val hintsUsed:Int?,
                        @ColumnInfo(name = "mistakes") val mistakes:Int?,
                        @ColumnInfo(name = "difficulty") val difficulty:String,
                        @ColumnInfo(name = "score") val score:Int?,
                        @ColumnInfo(name = "stars") val stars:Int?,
                        @ColumnInfo(name = "perfect_win") val perfectWins:Int?){
}