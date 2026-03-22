package com.gamopy.sudoku.repository

import com.gamopy.sudoku.data.Resource
import com.google.firebase.auth.FirebaseUser

interface SudokuRepo {
    val currentUser: FirebaseUser?

    suspend fun updateFirebaseBrainPoints(incrementedScore: Int): Resource<Nothing>
}