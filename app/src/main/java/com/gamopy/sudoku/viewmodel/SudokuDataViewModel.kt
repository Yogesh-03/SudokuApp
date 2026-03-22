package com.gamopy.sudoku.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamopy.sudoku.data.Resource
import com.gamopy.sudoku.database.SudokuEasyData
import com.gamopy.sudoku.database.SudokuEntity
import com.gamopy.sudoku.database.SudokuExpertData
import com.gamopy.sudoku.database.SudokuHardData
import com.gamopy.sudoku.database.SudokuMediumData
import com.gamopy.sudoku.repository.SudokuRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SudokuDataViewModel(private val repository: SudokuRepository) : ViewModel() {

    private val _scoreUpdateResult = MutableLiveData<Resource<Nothing>?>(null)
    val scoreUpdateResult: LiveData<Resource<Nothing>?> = _scoreUpdateResult

    val easyAllData = repository.easyAllData
    val mediumAllData = repository.mediumAllData
    val hardAllData = repository.hardAllData
    val expertAllData = repository.expertAllData

    val easyTotalWins = repository.easyTotalWins
    val easyGamesPlayed = repository.easyGamesPlayed
    val easyPerfectWins = repository.easyPerfectWins
    val easyBestTime = repository.easyBestTime

    val mediumTotalWins = repository.mediumTotalWins
    val mediumGamesPlayed = repository.mediumGamesPlayed
    val mediumPerfectWins = repository.mediumPerfectWins
    val mediumBestTime = repository.mediumBestTime

    val hardTotalWins = repository.hardTotalWins
    val hardGamesPlayed = repository.hardGamesPlayed
    val hardPerfectWins = repository.hardPerfectWins
    val hardBestTime = repository.hardBestTime

    val expertTotalWins = repository.expertTotalWins
    val expertGamesPlayed = repository.expertGamesPlayed
    val expertPerfectWins = repository.expertPerfectWins
    val expertBestTime = repository.expertBestTime

    private val inputDifficulty = MutableLiveData<String>()
    private val inputTime = MutableLiveData<String>()
    private val inputMistakes = MutableLiveData<Int>()
    private val inputIsCompleted = MutableLiveData<Boolean>()
    private val inputHintsUsed = MutableLiveData<Int>()
    private val date = MutableLiveData<String>()
    private val time = MutableLiveData<Int>()


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

    fun saveEasy() {
        insertEasy(
            SudokuEasyData(
                0,
                inputTime.value,
                inputMistakes.value,
                inputIsCompleted.value,
                time.value
            )
        )
    }

    fun saveMedium() {
        insertMedium(
            SudokuMediumData(
                0,
                inputTime.value,
                inputMistakes.value,
                inputIsCompleted.value,
                time.value
            )
        )
    }

    fun saveHard() {
        insertHard(
            SudokuHardData(
                0,
                inputTime.value,
                inputMistakes.value,
                inputIsCompleted.value,
                time.value
            )
        )
    }

    fun saveExpert() {
        insertExpert(
            SudokuExpertData(
                0,
                inputTime.value,
                inputMistakes.value,
                inputIsCompleted.value,
                time.value
            )
        )
    }

    private fun insertExpert(sudokuExpertData: SudokuExpertData) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.expertInsert(sudokuExpertData)
        }

    private fun insertHard(sudokuHardData: SudokuHardData) = viewModelScope.launch(Dispatchers.IO) {
        repository.hardInsert(sudokuHardData)
    }

    private fun insertMedium(sudokuMediumData: SudokuMediumData) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.mediumInsert(sudokuMediumData)
        }

    private fun insertEasy(sudokuEasyData: SudokuEasyData) = viewModelScope.launch(Dispatchers.IO) {
        repository.easyInsert(sudokuEasyData)
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

    fun setDate(date: String) {
        this.date.value = date
    }

    fun setTime(time: Int) {
        this.time.value = time
    }

    fun updateFirebaseBrainPoints(incrementedScore: Int) = viewModelScope.launch {
        _scoreUpdateResult.value = Resource.Loading
        val result = repository.updateFirebaseBrainPoints(incrementedScore)
        _scoreUpdateResult.value = result
    }
}