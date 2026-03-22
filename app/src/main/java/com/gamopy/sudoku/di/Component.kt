package com.gamopy.sudoku.di

import com.gamopy.sudoku.repository.SudokuRepository
import dagger.Component

@Component(modules =  [AppModule::class])
interface Component {
    fun inject(repository: SudokuRepository)
}