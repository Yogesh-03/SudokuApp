package com.gamopy.sudoku.sudoku

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.gamopy.sudoku.model.ActionType
import com.gamopy.sudoku.model.GameState
import com.gamopy.sudoku.model.UserAction
import com.gamopy.sudoku.sharedpreferences.UserSettings
import com.gamopy.sudoku.sudokugenerator.Constants
import com.gamopy.sudoku.sudokugenerator.DifficultyLevel
import com.gamopy.sudoku.sudokugenerator.Generator
import com.gamopy.sudoku.sudokugenerator.Solver
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale
import kotlin.random.Random


class SudokuGame(application: Application, cont: String, bundle: Bundle) {

    var selectedRow = -1
    var selectedCol = -1
    private var isTakingNotes = false
    private var sdkBoard: Generator
    private lateinit var cellList: MutableList<Int>
    private var currentCellList: MutableList<Int> = mutableListOf()
    private lateinit var cc: Array<IntArray>
    private var sudokuSolution: MutableList<Int> = mutableListOf()
    private var userActionsHistory: MutableList<UserAction> = mutableListOf()
    private val revealedCells = mutableSetOf<Pair<Int, Int>>()

    private var remainingNumber = MutableStateFlow(MutableList(9) { 9 })
    val mutableList: StateFlow<MutableList<Int>> = remainingNumber
    var difficultLevel: String? = " "

    var seconds: Int = 0
    var running: Boolean = true
    var wasRunning: Boolean = false
    lateinit var board: Board
    var time = MutableLiveData<String>()
    var hintCellLiveData = MutableLiveData<Pair<Int, Int>>()
    var selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()
    val cellsLiveData = MutableLiveData<List<Cell>>()
    val isTakingNotesLiveData = MutableLiveData<Boolean>()
    val isTimerVisible = MutableLiveData<Boolean>()
    val highlightedKeysLiveData = MutableLiveData<Set<Int>>()
    var mistakes = MutableLiveData<Int>()
    var remainingValuesCount = MutableLiveData(0)
    var theme = MutableLiveData<String>()
    var hintsRemaining = MutableLiveData(1)

    init {
        mistakes.postValue(0)
        hintsRemaining.postValue(1)

        sdkBoard = if (bundle.getString("easy") == "Easy") {
            Generator.Builder().setLevel(DifficultyLevel.EASY).build()
        } else if (bundle.getString("medium") == "Medium") {
            Generator.Builder().setLevel(DifficultyLevel.MEDIUM).build()
        } else if (bundle.getString("hard") == "Hard") {
            Generator.Builder().setLevel(DifficultyLevel.HARD).build()
        } else if (bundle.getString("expert") == "Expert") {
            Generator.Builder().setLevel(DifficultyLevel.EXPERT).build()
        } else {
            Generator.Builder().setLevel(DifficultyLevel.EASY).build()
        }



        if (application.getSharedPreferences(UserSettings().PREFERENCES, Context.MODE_PRIVATE)
                .getBoolean(UserSettings().IS_CURRENT_GAME, false) && cont == "continue"
        ) {
            val gameState = loadGameState(application)
            val list = gameState.sudokuList
            board = Board(9, list)
            val continueCellList: MutableList<Int> = MutableList(81) { 0 }
            for (i in list.indices) {
                continueCellList[i] = list[i].value
            }
            cellList = continueCellList
            val solutionList: MutableList<Int> = Gson().fromJson(
                application.getSharedPreferences(
                    UserSettings().PREFERENCES,
                    Context.MODE_PRIVATE
                ).getString(UserSettings().CURRENT_SUDOKU_SOLUTION_LIST, null),
                object : TypeToken<MutableList<Int>>() {}.type
            )
            sudokuSolution = solutionList
            userActionsHistory = gameState.userActionHistory
            mistakes.postValue(gameState.mistakes)
            hintsRemaining.postValue(gameState.hintsRemaining)
            seconds = gameState.seconds
            difficultLevel = gameState.difficultyLevel
        } else {
            if (bundle.getString("scanned") != "true") {
                cellList = sdkBoard.sudokuList()
                val cells: List<Cell> = List(9 * 9) { i -> Cell(i / 9, i % 9, cellList[i]) }
                board = Board(9, cells)
                hintsRemaining.postValue(1)
                val chunks = cellList.chunked(9)
                val chunkedArray: Array<List<Int>> = chunks.toTypedArray()
                cc = Array(9) { IntArray(9) }
                cc[0] = chunkedArray[0].toIntArray()
                for (i in 0 until 9) cc[i] = chunkedArray[i].toIntArray()

                if (cont != "continue") {
                    if (Solver().solveSudoku(cc)) {
                        sudokuSolution = Solver().returnBoard(cc)
                        Solver().printBoard(cc)
                    }
                }

                for (i in 0 until cellList.size) {
                    if (cellList[i] != 0) {
                        cells[i].isStartingCell = true
                        cells[i].canValueChanged = false
                    }
                }
            } else {
                val resultArrayList = bundle.getIntegerArrayList("list")

                cellList = resultArrayList!!
                val cells: List<Cell> = List(9 * 9) { i -> Cell(i / 9, i % 9, cellList[i]) }
                board = Board(9, cells)
                hintsRemaining.postValue(1)
                val chunks = cellList.chunked(9)
                val chunkedArray: Array<List<Int>> = chunks.toTypedArray()
                cc = Array(9) { IntArray(9) }
                cc[0] = chunkedArray[0].toIntArray()
                for (i in 0 until 9) cc[i] = chunkedArray[i].toIntArray()

                if (cont != "continue") {
                    if (Solver().solveSudoku(cc)) {
                        sudokuSolution = Solver().returnBoard(cc)
                        Solver().printBoard(cc)
                    }
                }

                for (i in 0 until cellList.size) {
                    if (cellList[i] != 0) {
                        cells[i].isStartingCell = true
                        cells[i].canValueChanged = false
                    }
                }

            }
        }

        for (i in cellList) {
            if (i != 0) remainingNumber.value[i - 1] = remainingNumber.value[i - 1].minus(1)
            else remainingValuesCount.value = remainingValuesCount.value?.plus(1)
        }



        currentCellList = cellList
        val convertedInt = Gson().toJson(cellList)
        application.getSharedPreferences(UserSettings().PREFERENCES, Context.MODE_PRIVATE)
            .edit()
            .putString(UserSettings().CURRENT_SUDOKU_LIST, convertedInt).apply()

        val convertedSolution = Gson().toJson(sudokuSolution)
        application.getSharedPreferences(UserSettings().PREFERENCES, Context.MODE_PRIVATE)
            .edit()
            .putString(UserSettings().CURRENT_SUDOKU_SOLUTION_LIST, convertedSolution).apply()



        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        cellsLiveData.postValue(board.cells)
        isTakingNotesLiveData.postValue(isTakingNotes)

    }


    /**
     * Increased value by 1 of remaining number count list
     * @param value
     */
    private fun incrementValue(value: Int) {
        val currentList = remainingNumber.value.toMutableList()
        currentList[value - 1]++
        remainingNumber.value = currentList
    }

    /**
     * Decreased value by 1 of remaining number count list
     * @param value
     */
    private fun decrementValue(value: Int) {
        val currentList = remainingNumber.value.toMutableList()
        currentList[value - 1]--
        remainingNumber.value = currentList
    }

    private fun decrementValue(data: MutableLiveData<Int>, value: Int) {
        data.value = data.value?.minus(value)
    }

    private fun incrementValue(data: MutableLiveData<Int>, value: Int) {
        data.value = data.value?.plus(value)
    }

    /**
     * @param string
     */
    fun changeTheme(string: String) {
        theme.postValue(string)
    }

    private fun addUserAction(
        row: Int,
        col: Int,
        value: Int,
        hasWrongValue: Boolean,
        canValueChanged: Boolean,
        actionType: ActionType,
        list: MutableList<Int?>?
    ) {
        val userAction =
            UserAction(row, col, value, hasWrongValue, canValueChanged, actionType, list)
        userActionsHistory.add(userAction)
        val lastAction: UserAction = userActionsHistory[userActionsHistory.size - 1]
//        Log.d(
//            "Last Action: ",
//            "{Row: ${lastAction.row}, Column: ${lastAction.col}, Value: ${lastAction.value}, " +
//                    "Can Value Changes: ${lastAction.canValueChanged}," +
//                    " Has Wrong Value: ${lastAction.hasWrongValue}, Action Type: ${lastAction.actionType}, " +
//                    " Size: ${userActionsHistory.size}"
//        )
    }

    /**
     * Remove pencil note on Undo
     * @param row   Row of cell
     * @param col   Column of cell
     * @param note  Note to be removed
     */
    private fun removePencilNote(row: Int, col: Int, note: Int) {
        val cell = board.getCell(row, col)
        cell.notes.remove(note)
        highlightedKeysLiveData.postValue(cell.notes) // Update the UI to reflect the removed note
    }

    private fun addAllPencilNotes(row: Int, col: Int, list: MutableList<Int?>?) {
        val cell = board.getCell(row, col)
        if (list != null) for (i in list) if (i != null) cell.notes.add(i)
    }

    private fun addPencilNote(row: Int, col: Int, note: Int) {
        val cell = board.getCell(row, col)
        cell.notes.add(note)
        highlightedKeysLiveData.postValue(cell.notes) // Update the UI to reflect the removed note
    }

    fun undo() {
        // Undo the last action
        if (userActionsHistory.isNotEmpty()) {
            // value at last index
            val lastAction: UserAction = userActionsHistory.removeAt(userActionsHistory.size - 1)
//            Log.d(
//                "Last Action Removed: ",
//                "{Row: ${lastAction.row}, Column: ${lastAction.col}, Value: ${lastAction.value}, " +
//                        "Can Value Changes: ${lastAction.canValueChanged}, " +
//                        "Has Wrong Value: ${lastAction.hasWrongValue}, Action Type: ${lastAction.actionType}, " +
//                        "Size: ${userActionsHistory.size}, Value: ${lastAction.value}}"
//            )

            val row: Int = lastAction.row
            val col: Int = lastAction.col
            val cell = board.getCell(row, col)

            when (lastAction.actionType) {
                ActionType.FILL -> {
                    // Reverse a fill action (e.g., clear the cell)
                    if (!cell.hasWrongValue) {
                        remainingValuesCount.value = remainingValuesCount.value?.plus(1)
                        incrementValue(cell.value)
                    }
                    //resetCell(cell)
                    cell.value = lastAction.value
                    cell.hasWrongValue = lastAction.hasWrongValue
                    cell.canValueChanged = lastAction.canValueChanged
                    updateSelectedCell(cell.row, cell.col)
                }

                ActionType.CLEAR -> {
                    // Reverse a clear action (e.g., restore the previous value)
                    cell.value = lastAction.value
                    cell.hasWrongValue = lastAction.hasWrongValue
                    updateSelectedCell(cell.row, cell.col)
                }

                ActionType.PENCIL_MARK -> {
                    // Handle undoing a pencil mark action (if applicable)
                    // You may need to implement pencil mark functionality separately
                    removePencilNote(row, col, lastAction.value)
                    updateSelectedCell(cell.row, cell.col)
                }

                ActionType.PENCIL_CLEAR -> {
                    addPencilNote(row, col, lastAction.value)
                    updateSelectedCell(cell.row, cell.col)
                }

                ActionType.PENCIL_CLEAR_ALL -> {
                    addAllPencilNotes(row, col, lastAction.list)
                    updateSelectedCell(cell.row, cell.col)
                }
            }

            if (cell.value == 0) cell.canHighlightNotes = true

            cellsLiveData.postValue(board.cells)
            highlightedKeysLiveData.postValue(cell.notes)
        }
    }

    fun handleInput(number: Int) {
        if (selectedRow == -1 || selectedCol == -1) return
        val cell = board.getCell(selectedRow, selectedCol)
        val cells = board.getCellList()
        if (cell.isStartingCell) return

        if (isTakingNotes) {
            if (cell.value == 0) {
                if (cell.notes.contains(number)) {
                    addUserAction(
                        selectedRow,
                        selectedCol,
                        number,
                        cell.hasWrongValue,
                        cell.canValueChanged,
                        ActionType.PENCIL_CLEAR,
                        null
                    )
                    cell.notes.remove(number)
                } else {
                    cells.forEach {
                        val r = it.row
                        val c = it.col
                        if (!it.canValueChanged) {
                            if (r == selectedRow || c == selectedCol) {
                                if (it.value != number) cell.notes.add(number)
                                else {
                                    cell.notes.remove(number)
                                    return
                                }
                            }
                            if (r / 3 == selectedRow / 3 && c / 3 == selectedCol / 3) {
                                if (it.value != number) cell.notes.add(number)
                                else {
                                    cell.notes.remove(number)
                                    return
                                }

                            }
                        }
                    }
                    addUserAction(
                        selectedRow,
                        selectedCol,
                        number,
                        cell.hasWrongValue,
                        cell.canValueChanged,
                        ActionType.PENCIL_MARK,
                        null
                    )
                }
            }
            highlightedKeysLiveData.postValue(cell.notes)
        } else {
            if (cell.canValueChanged) {
                //Cell having wrong value
                if (cell.value == number) {
                    addUserAction(
                        selectedRow,
                        selectedCol,
                        number,
                        true,
                        true,
                        ActionType.CLEAR,
                        null
                    )
                    cell.hasWrongValue = false
                    cell.value = 0
                    cellsLiveData.postValue(board.cells)
                    return
                }

                if (sudokuSolution[9 * selectedRow + selectedCol] != number) {
                    mistakes.value = mistakes.value?.plus(1)
                    addUserAction(
                        selectedRow,
                        selectedCol,
                        cell.value,
                        cell.hasWrongValue,
                        cell.canValueChanged,
                        ActionType.FILL,
                        null
                    )
                    cell.apply {
                        canValueChanged = true
                        hasWrongValue = true
                        value = number
                        //notes.clear()
                        canHighlightNotes = false
                    }
                } else {
                    decrementValue(number)
                    addUserAction(
                        selectedRow,
                        selectedCol,
                        cell.value,
                        cell.hasWrongValue,
                        cell.canValueChanged,
                        ActionType.FILL,
                        null
                    )
                    remainingValuesCount.value = remainingValuesCount.value?.minus(1)
                    cell.canValueChanged = false
                    cell.hasWrongValue = false
                    cells.forEach {
                        val r = it.row
                        val c = it.col
                        //Removing unwanted notes
                        if (r == selectedRow || c == selectedCol) if (it.notes.contains(number)) it.notes.remove(
                            number
                        )

                        if (r / 3 == selectedRow / 3 && c / 3 == selectedCol / 3) if (it.notes.contains(
                                number
                            )
                        ) it.notes.remove(number)

                    }
                    cell.value = number
                    //cell.notes.clear()
                    cell.canHighlightNotes = false
                }
            }
        }
        if (cell.value == 0) cell.canHighlightNotes = true
        cellsLiveData.postValue(board.cells)
    }

    suspend fun autoSolverSudoku() {
        val cells = board.getCellList()

        for (cell in cells) {
            if (cell.canValueChanged) {
                val index = cell.row * Constants().GRID_SIZE + cell.col
                cell.value = sudokuSolution[index]

                cellsLiveData.postValue(cells)
                //Log.d("CELLVALUE", cell.value.toString())

                delay(100) // ⏱ delay between cells (milliseconds)
                decrementValue(remainingValuesCount, 1)
                //remainingValuesCount.value = remainingValuesCount.value?.minus(1)
            }
        }
    }


    /**
     * @param row
     * @param col
     */
    fun updateSelectedCell(row: Int, col: Int) {
        val cell = board.getCell(row, col)
        if (!cell.canValueChanged || !cell.isStartingCell) {
            selectedRow = row
            selectedCol = col
            selectedCellLiveData.postValue(Pair(row, col))

            if (isTakingNotes) highlightedKeysLiveData.postValue(cell.notes)
        }
    }


    fun changeNoteTakingState() {
        if (selectedRow == -1 || selectedCol == -1) return
        isTakingNotes = !isTakingNotes
        isTakingNotesLiveData.postValue(isTakingNotes)
        val cell = board.getCell(selectedRow, selectedCol)
        if (selectedRow >= 0 && selectedCol >= 0) {
            if (cell.canValueChanged) {
                val curNotes = if (isTakingNotes) board.getCell(selectedRow, selectedCol).notes
                else setOf()

                highlightedKeysLiveData.postValue(curNotes)
            }
        }
    }


    // Deletes the value of selected cell
    fun delete() {
        if (selectedRow >= 0 && selectedCol >= 0) {
            val cell = board.getCell(selectedRow, selectedCol)
            if (!cell.canValueChanged) return
            else if (isTakingNotes) highlightedKeysLiveData.postValue(setOf())
            else {
                if (!cell.isStartingCell) {
                    if (cell.value != 0) {
                        addUserAction(
                            selectedRow,
                            selectedCol,
                            cell.value,
                            true,
                            true,
                            ActionType.CLEAR,
                            null
                        )
                        cell.hasWrongValue = false
                        cell.value = 0
                        cell.canHighlightNotes = true
                    }
                }
            }
            if (cell.notes.size > 0) {
                val list = mutableListOf<Int?>()
                for (i in cell.notes) {
                    list.add(i)
                }
                addUserAction(
                    selectedRow,
                    selectedCol,
                    cell.value,
                    true,
                    true,
                    ActionType.PENCIL_CLEAR_ALL,
                    list
                )
            }

            if (cell.value == 0) cell.notes.clear()

            cellsLiveData.postValue(board.cells)
        }
    }

    fun exitGameMistakes() {
        remainingNumber.value.replaceAll { 9 }
    }

    /**
     * @param jsonString
     * converts json to Gson
     */
    private fun deserializeGameState(jsonString: String): GameState {
        return Gson().fromJson(jsonString, GameState::class.java)
    }

    /**
     * @param application
     * Loads the complete saved game state from the shared preferences
     * Used to continue the saved game
     */
    private fun loadGameState(application: Application): GameState {
        val sharedPreferences = application.getSharedPreferences("SudokuGame", Context.MODE_PRIVATE)
        val gameStateString = sharedPreferences.getString("gameState", null)
        return deserializeGameState(gameStateString!!)
    }

    fun getUserActionHistory(): MutableList<UserAction> {
        return userActionsHistory
    }

    private fun generateRandomRowAndColumn(): Pair<Int, Int> {
        val row = Random(System.nanoTime()).nextInt(0, 9)
        val col = Random(System.nanoTime()).nextInt(0, 9)
        return Pair(row, col)
    }

    fun handleHints() {
        if (hintsRemaining.value!! > 0) {
            val rowAndCol = generateRandomRowAndColumn()
            val cell = board.getCell(rowAndCol.first, rowAndCol.second)

            if (!revealedCells.contains(
                    Pair(
                        rowAndCol.first,
                        rowAndCol.second
                    )
                ) && cell.value == 0
            ) {
                selectedRow = rowAndCol.first
                selectedCol = rowAndCol.second
                val number = sudokuSolution[9 * rowAndCol.first + rowAndCol.second]
//                Log.d(
//                    "Hint: ",
//                    "Row: ${rowAndCol.first}, Col: ${rowAndCol.second}, Number: $number, cell: ${cell.value}"
//                )
                revealedCells.add(Pair(rowAndCol.first, rowAndCol.second))
                hintsRemaining.value = hintsRemaining.value!!.minus(1)
                cell.isHint = true
                handleInput(number)
                hintCellLiveData.postValue(Pair(rowAndCol.first, rowAndCol.second))
                return
            } else handleHints()

        }
    }

    fun runTimer() {
        val handler = Handler(Looper.getMainLooper())

        handler.post(object : Runnable {
            override fun run() {
                val minutes = (seconds.rem(3600)).div(60)
                val secs = seconds.rem(60)

                time.postValue(String.format(Locale.getDefault(), "%02d:%02d", minutes, secs))

                if (running) seconds++

                handler.postDelayed(this, 1000)
            }
        })
    }

    fun pauseTimer() {
        running = false
    }

    /**
     * It resumes the ongoing game timer
     */
    fun resumeTimer() {
        running = true
    }

    fun timerOnPause() {
        wasRunning = running
        running = false
    }

    fun timerOnResume() {
        if (wasRunning) running = true
    }
}