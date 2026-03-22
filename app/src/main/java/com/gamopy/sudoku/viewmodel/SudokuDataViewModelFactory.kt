package com.gamopy.sudoku.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gamopy.sudoku.repository.SudokuRepository

class SudokuDataViewModelFactory(private val repository: SudokuRepository):ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SudokuDataViewModel::class.java)){
            return SudokuDataViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown View Model Class")
    }
}