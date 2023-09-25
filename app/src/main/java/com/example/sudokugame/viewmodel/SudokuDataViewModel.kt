package com.example.sudokugame.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sudokugame.database.SudokuEntity
import com.example.sudokugame.repository.SudokuRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SudokuDataViewModel(private val repository: SudokuRepository) : ViewModel() {
    val easyAllData = repository.easyAllData
    val mediumAllData = repository.mediumAllData
    val hardAllData = repository.hardAllData
    val expertAllData = repository.expertAllData

    val easyTotalWins = repository.easyTotalWins
    val easyGamesPlayed = repository.easyGamesPlayed
    val easyPerfectWins = repository.easyPerfectWins

    val mediumTotalWins = repository.mediumTotalWins
    val mediumGamesPlayed = repository.mediumGamesPlayed
    val mediumPerfectWins = repository.mediumPerfectWins

    val hardTotalWins = repository.hardTotalWins
    val hardGamesPlayed = repository.hardGamesPlayed
    val hardPerfectWins = repository.hardPerfectWins

    val expertTotalWins = repository.expertTotalWins
    val expertGamesPlayed = repository.expertGamesPlayed
    val expertPerfectWins = repository.expertPerfectWins

    private val inputDifficulty = MutableLiveData<String>()
    private val inputTime = MutableLiveData<String>()
    private val inputMistakes = MutableLiveData<Int>()
    private val inputIsCompleted = MutableLiveData<Boolean>()
    private val inputHintsUsed = MutableLiveData<Int>()
    private val date = MutableLiveData<String>()


    fun save() {
        insert(
            SudokuEntity(
                0,
                inputDifficulty.value!!,
                inputTime.value,
                inputMistakes.value,
                inputIsCompleted.value,
                inputHintsUsed.value,
                date.value
            )
        )
    }

    private fun insert(sudokuEntity: SudokuEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(sudokuEntity)
    }

    fun update(sudokuEntity: SudokuEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(sudokuEntity)
    }

    fun delete(sudokuEntity: SudokuEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(sudokuEntity)
    }

    fun setInputDifficulty(inputDifficulty: String) {
        this.inputDifficulty.value = inputDifficulty
    }

    fun setInputTime(inputTime: String) {
        this.inputTime.value = inputTime
    }

    fun setInputMistakes(inputMistakes: Int) {
        this.inputMistakes.value = inputMistakes
    }

    fun setInputIsCompleted(inputIsCompleted: Boolean) {
        this.inputIsCompleted.value = inputIsCompleted
    }

    fun setInputHintsUsed(inputHintsUsed: Int) {
        this.inputHintsUsed.value = inputHintsUsed
    }

    fun setDate(date:String){
        this.date.value = date
    }
}