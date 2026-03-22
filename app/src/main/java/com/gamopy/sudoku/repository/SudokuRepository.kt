package com.gamopy.sudoku.repository

import com.gamopy.sudoku.data.Resource
import com.gamopy.sudoku.database.EasySudokuDao
import com.gamopy.sudoku.database.ExpertSudokuDao
import com.gamopy.sudoku.database.HardSudokuDao
import com.gamopy.sudoku.database.MediumSudokuDao
import com.gamopy.sudoku.database.SudokuDao
import com.gamopy.sudoku.database.SudokuEasyData
import com.gamopy.sudoku.database.SudokuEntity
import com.gamopy.sudoku.database.SudokuExpertData
import com.gamopy.sudoku.database.SudokuHardData
import com.gamopy.sudoku.database.SudokuMediumData
import com.gamopy.sudoku.firebase.awaitSuccess
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import java.lang.Exception
import javax.inject.Inject

class SudokuRepository(
    private val dao: SudokuDao,
    private val easyDao: EasySudokuDao,
    private val mediumDao: MediumSudokuDao,
    private val hardDao: HardSudokuDao,
    private val expertDao: ExpertSudokuDao
) : SudokuRepo {
    val easyAllData = dao.getEasyAllData()
    val mediumAllData = dao.getMediumAllData()
    val hardAllData = dao.getHardAllData()
    val expertAllData = dao.getExpertAllData()

    val easyTotalWins = easyDao.getEasyTotalWins()
    val easyGamesPlayed = easyDao.getEasyGamesPlayed()
    val easyPerfectWins = easyDao.getEasyPerfectWins()
    val easyBestTime = easyDao.getEasyBestTime()

    val mediumTotalWins = mediumDao.getMediumTotalWins()
    val mediumGamesPlayed = mediumDao.getMediumGamesPlayed()
    val mediumPerfectWins = mediumDao.getMediumPerfectWins()
    val mediumBestTime = mediumDao.getMediumBestTime()

    val hardTotalWins = hardDao.getHardTotalWins()
    val hardGamesPlayed = hardDao.getHardGamesPlayed()
    val hardPerfectWins = hardDao.getHardPerfectWins()
    val hardBestTime = hardDao.getHardBestTime()

    val expertTotalWins = expertDao.getExpertTotalWins()
    val expertGamesPlayed = expertDao.getExpertGamesPlayed()
    val expertPerfectWins = expertDao.getExpertPerfectWins()
    val expertBestTime = expertDao.getExpertBestTime()

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var firebaseFirestore: FirebaseFirestore

    @Inject
    lateinit var storageReference: StorageReference


    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser


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

    suspend fun easyInsert(sudokuEasyData: SudokuEasyData) {
        easyDao.insert(sudokuEasyData)
    }

    suspend fun mediumInsert(sudokuMediumData: SudokuMediumData) {
        mediumDao.insert(sudokuMediumData)
    }

    suspend fun hardInsert(sudokuHardData: SudokuHardData) {
        hardDao.insert(sudokuHardData)
    }

    suspend fun expertInsert(sudokuExpertData: SudokuExpertData) {
        expertDao.insert(sudokuExpertData)
    }

    /**
     * Updates score (Brain Points) on firebase firestore after successful completion of the game
     */
    override suspend fun updateFirebaseBrainPoints(incrementedScore: Int): Resource<Nothing> {
        return try {
            val data = hashMapOf(
                "score" to incrementedScore
            )

            val result =
                firebaseFirestore.collection("Scores").document(currentUser!!.uid).set(data)
                    .awaitSuccess()
            Resource.Empty
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }
}