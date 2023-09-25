package com.example.sudokugame.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface SudokuDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(sudokuEntity: SudokuEntity)

    @Delete
    suspend fun delete(sudokuEntity: SudokuEntity)

    @Update
    suspend fun update(sudokuEntity: SudokuEntity)

    @Query("DELETE FROM sudoku_data")
    suspend fun deleteAll()

    @Query("SELECT * FROM sudoku_data")
    fun getAllData(): LiveData<List<SudokuEntity>>

    @Query("SELECT * FROM sudoku_data WHERE difficulty = 'Easy'")
    fun getEasyAllData(): LiveData<List<SudokuEntity>>

    @Query("SELECT * FROM sudoku_data WHERE difficulty = 'Medium'")
    fun getMediumAllData(): LiveData<List<SudokuEntity>>

    @Query("SELECT * FROM sudoku_data WHERE difficulty = 'Hard'")
    fun getHardAllData(): LiveData<List<SudokuEntity>>

    @Query("SELECT * FROM sudoku_data WHERE difficulty = 'Expert'")
    fun getExpertAllData(): LiveData<List<SudokuEntity>>

    //Easy
    @Query("SELECT COUNT(*) FROM sudoku_data WHERE difficulty = 'Easy' AND is_completed = 1")
     fun getEasyTotalWins():LiveData<Int>

    @Query("SELECT COUNT(*) FROM sudoku_data WHERE difficulty = 'Easy'")
    fun getEasyGamesPlayed():LiveData<Int>

    @Query("SELECT COUNT(*) FROM sudoku_data WHERE difficulty = 'Easy' AND mistakes = 0")
    fun getEasyPerfectWins():LiveData<Int>

    //Medium
    @Query("SELECT COUNT(*) FROM sudoku_data WHERE difficulty = 'Medium' AND is_completed = 1")
    fun getMediumTotalWins():LiveData<Int>

    @Query("SELECT COUNT(*) FROM sudoku_data WHERE difficulty = 'Medium'")
    fun getMediumGamesPlayed():LiveData<Int>

    @Query("SELECT COUNT(*) FROM sudoku_data WHERE difficulty = 'Medium' AND mistakes = 0")
    fun getMediumPerfectWins():LiveData<Int>


    //Hard
    @Query("SELECT COUNT(*) FROM sudoku_data WHERE difficulty = 'Hard' AND is_completed = 1")
    fun getHardTotalWins():LiveData<Int>

    @Query("SELECT COUNT(*) FROM sudoku_data WHERE difficulty = 'Hard'")
    fun getHardGamesPlayed():LiveData<Int>

    @Query("SELECT COUNT(*) FROM sudoku_data WHERE difficulty = 'Hard' AND mistakes = 0")
    fun getHardPerfectWins():LiveData<Int>


    //Expert
    @Query("SELECT COUNT(*) FROM sudoku_data WHERE difficulty = 'Expert' AND is_completed = 1")
    fun getExpertTotalWins():LiveData<Int>

    @Query("SELECT COUNT(*) FROM sudoku_data WHERE difficulty = 'Expert'")
    fun getExpertGamesPlayed():LiveData<Int>

    @Query("SELECT COUNT(*) FROM sudoku_data WHERE difficulty = 'Expert' AND mistakes = 0")
    fun getExpertPerfectWins():LiveData<Int>

}
