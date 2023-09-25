package com.example.sudokugame.repository

import com.example.sudokugame.database.SudokuDao
import com.example.sudokugame.database.SudokuEntity

class SudokuRepository(private val dao: SudokuDao) {
    val easyAllData = dao.getEasyAllData()
    val mediumAllData = dao.getMediumAllData()
    val hardAllData = dao.getHardAllData()
    val expertAllData = dao.getExpertAllData()

    val easyTotalWins = dao.getEasyTotalWins()
    val easyGamesPlayed = dao.getEasyGamesPlayed()
    val easyPerfectWins = dao.getEasyPerfectWins()

    val mediumTotalWins = dao.getMediumTotalWins()
    val mediumGamesPlayed = dao.getMediumGamesPlayed()
    val mediumPerfectWins = dao.getMediumPerfectWins()

    val hardTotalWins = dao.getHardTotalWins()
    val hardGamesPlayed = dao.getHardGamesPlayed()
    val hardPerfectWins = dao.getHardPerfectWins()

    val expertTotalWins = dao.getExpertTotalWins()
    val expertGamesPlayed = dao.getExpertGamesPlayed()
    val expertPerfectWins = dao.getExpertPerfectWins()

    suspend fun insert(sudokuData: SudokuEntity) {
        dao.insert(sudokuData)
    }

    suspend fun update(sudokuData: SudokuEntity) {
        dao.update(sudokuData)
    }

    suspend fun delete(sudokuData: SudokuEntity) {
        dao.delete(sudokuData)
    }

    suspend fun deleteAll() {
        dao.deleteAll()
    }
}