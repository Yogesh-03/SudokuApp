package com.gamopy.sudoku.viewmodel

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PlaySudokuViewModelFactory(private val application: Application, private val cont:String, private val bundle: Bundle): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaySudoku::class.java)){
            return PlaySudoku(application, cont, bundle) as T
        }
        throw IllegalArgumentException("Unknown View Model Class")
    }
}