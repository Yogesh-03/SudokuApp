package com.gamopy.sudoku.database

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


    //Easy
    @Query("SELECT * FROM sudoku_data WHERE difficulty = 'Easy'")
    fun getEasyAllData(): LiveData<List<SudokuEntity>>

    @Query("DELETE FROM sudoku_data WHERE difficulty = 'Easy' ")
    fun deleteAllFromEasy()


    //Medium
    @Query("SELECT * FROM sudoku_data WHERE difficulty = 'Medium'")
    fun getMediumAllData(): LiveData<List<SudokuEntity>>


    //Hard
    @Query("SELECT * FROM sudoku_data WHERE difficulty = 'Hard'")
    fun getHardAllData(): LiveData<List<SudokuEntity>>


    //Expert
    @Query("SELECT * FROM sudoku_data WHERE difficulty = 'Expert'")
    fun getExpertAllData(): LiveData<List<SudokuEntity>>

}

@Dao
interface EasySudokuDao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(sudokuEasyData: SudokuEasyData)

    @Query("SELECT COUNT(*) FROM sudoku_easy_data WHERE is_completed = 1")
    fun getEasyTotalWins():LiveData<Int>

    @Query("SELECT COUNT(*) FROM sudoku_easy_data ")
    fun getEasyGamesPlayed():LiveData<Int>

    @Query("SELECT COUNT(*) FROM sudoku_easy_data WHERE  mistakes = 0")
    fun getEasyPerfectWins():LiveData<Int>

    @Query("SELECT MIN(time) FROM sudoku_easy_data WHERE is_completed=1")
    fun getEasyBestTime():LiveData<Int>

    @Query("SELECT COUNT(*) FROM sudoku_easy_data")
     fun getAllData():LiveData<Int>
}

@Dao
interface MediumSudokuDao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(sudokuMediumData: SudokuMediumData)

    @Query("SELECT COUNT(*) FROM sudoku_medium_data WHERE  is_completed = 1")
    fun getMediumTotalWins():LiveData<Int>

    @Query("SELECT COUNT(*) FROM sudoku_medium_data")
    fun getMediumGamesPlayed():LiveData<Int>

    @Query("SELECT COUNT(*) FROM sudoku_medium_data WHERE mistakes = 0")
    fun getMediumPerfectWins():LiveData<Int>

    @Query("SELECT MIN(time) FROM sudoku_medium_data WHERE is_completed=1")
    fun getMediumBestTime():LiveData<Int>

    @Query("SELECT COUNT(*) FROM sudoku_medium_data")
     fun getAllData():LiveData<Int>
}

@Dao
interface HardSudokuDao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(sudokuHardData: SudokuHardData)

    @Query("SELECT COUNT(*) FROM sudoku_hard_data WHERE  is_completed =1")
    fun getHardTotalWins():LiveData<Int>

    @Query("SELECT COUNT(*) FROM sudoku_hard_data")
    fun getHardGamesPlayed():LiveData<Int>

    @Query("SELECT COUNT(*) FROM sudoku_hard_data WHERE  mistakes = 0")
    fun getHardPerfectWins():LiveData<Int>

    @Query("SELECT MIN(time) FROM sudoku_hard_data WHERE is_completed =1 ")
    fun getHardBestTime():LiveData<Int>

    @Query("SELECT COUNT(*) FROM sudoku_hard_data")
     fun getAllData():LiveData<Int>
}

@Dao
interface ExpertSudokuDao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(sudokuExpertData: SudokuExpertData)

    @Query("SELECT COUNT(*) FROM sudoku_Expert_data WHERE  is_completed = 1")
    fun getExpertTotalWins():LiveData<Int>

    @Query("SELECT COUNT(*) FROM sudoku_Expert_data ")
    fun getExpertGamesPlayed():LiveData<Int>

    @Query("SELECT COUNT(*) FROM sudoku_Expert_data WHERE  mistakes = 0")
    fun getExpertPerfectWins():LiveData<Int>

    @Query("SELECT MIN(time) FROM sudoku_Expert_data WHERE is_completed=1")
    fun getExpertBestTime():LiveData<Int>

    @Query("SELECT COUNT(*) FROM sudoku_expert_data")
     fun getAllData():LiveData<Int>
}
