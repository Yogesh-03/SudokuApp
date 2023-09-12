package com.example.sudokugame

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface SudokuDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(sudokuEntity: SudokuEntity)

    @Delete
    suspend fun delete(sudokuEntity: SudokuEntity)
}
