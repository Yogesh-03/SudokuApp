package com.example.sudokugame.sudokugenerator

import kotlin.random.Random
private lateinit var sudokuList:List<IntArray>
class Generator private constructor(difficultyLevel: DifficultyLevel){
    private val grid = Array(Constants().GRID_SIZE) { IntArray(Constants().GRID_SIZE) {0} }
    private val level: DifficultyLevel = difficultyLevel ?: DifficultyLevel.EASY

    init {
        fillGrid()
    }

    fun printGrid() {
        sudokuList = grid.toList()
        for (i in 0 until Constants().GRID_SIZE) {
            for (j in 0 until Constants().GRID_SIZE) {
                print(grid[i][j])
            }
            //println()
        }
        //println()
    }

    fun sudokuList():MutableList<Int>{
         val cells:MutableList<Int> = mutableListOf()
        for (i in 0 until Constants().GRID_SIZE) {
            for (j in 0 until Constants().GRID_SIZE) {
                cells.add(grid[i][j])
            }
        }
        return cells
    }

    private fun fillGrid() {
        fillDiagonalBoxes()
        fillRemaining(0, Constants().GRID_SIZE_SQUARE_ROOT)
        removeDigits()
    }

    private fun fillDiagonalBoxes() {
        for (i in 0 until Constants().GRID_SIZE step Constants().GRID_SIZE_SQUARE_ROOT) {
            fillBox(i, i)
        }
    }

    private fun fillBox(row: Int, column: Int) {
        var generatedDigit: Int

        for (i in 0 until Constants().GRID_SIZE_SQUARE_ROOT) {
            for (j in 0 until Constants().GRID_SIZE_SQUARE_ROOT) {
                do {
                    generatedDigit = generateRandomInt(Constants().MIN_DIGIT_VALUE, Constants().MAX_DIGIT_VALUE)
                } while (!isUnusedInBox(row, column, generatedDigit))

                grid[row + i][column + j] = generatedDigit
            }
        }
    }

    private fun generateRandomInt(min: Int, max: Int) = Random.nextInt(min, max + 1)

    private fun isUnusedInBox(rowStart: Int, columnStart: Int, digit: Int) : Boolean {
        for (i in 0 until Constants().GRID_SIZE_SQUARE_ROOT) {
            for (j in 0 until Constants().GRID_SIZE_SQUARE_ROOT) {
                if (grid[rowStart + i][columnStart + j] == digit) {
                    return false
                }
            }
        }
        return true
    }

    private fun fillRemaining(i: Int, j: Int) : Boolean {
        var i = i
        var j = j

        if (j >= Constants().GRID_SIZE && i < Constants().GRID_SIZE - 1) {
            i += 1
            j = 0
        }
        if (i >= Constants().GRID_SIZE && j >= Constants().GRID_SIZE) {
            return true
        }
        if (i < Constants().GRID_SIZE_SQUARE_ROOT) {
            if (j < Constants().GRID_SIZE_SQUARE_ROOT) {
                j = Constants().GRID_SIZE_SQUARE_ROOT
            }
        } else if (i < Constants().GRID_SIZE - Constants().GRID_SIZE_SQUARE_ROOT) {
            if (j == (i / Constants().GRID_SIZE_SQUARE_ROOT) * Constants().GRID_SIZE_SQUARE_ROOT) {
                j += Constants().GRID_SIZE_SQUARE_ROOT
            }
        } else {
            if (j == Constants().GRID_SIZE - Constants().GRID_SIZE_SQUARE_ROOT) {
                i += 1
                j = 0
                if (i >= Constants().GRID_SIZE) {
                    return true
                }
            }
        }

        for (digit in 1..Constants().MAX_DIGIT_VALUE) {
            if (isSafeToPutIn(i, j, digit)) {
                grid[i][j] = digit
                if (fillRemaining(i, j + 1)) {
                    return true
                }
                grid[i][j] = 0
            }
        }
        return false
    }

    private fun isSafeToPutIn(row: Int, column: Int, digit: Int) =
        isUnusedInBox(findBoxStart(row), findBoxStart(column), digit)
                && isUnusedInRow(row, digit)
                && isUnusedInColumn(column, digit)

    private fun findBoxStart(index: Int) = index - index % Constants().GRID_SIZE_SQUARE_ROOT

    private fun isUnusedInRow(row: Int, digit: Int) : Boolean {
        for (i in 0 until Constants().GRID_SIZE) {
            if (grid[row][i] == digit) {
                return false
            }
        }
        return true
    }

    private fun isUnusedInColumn(column: Int, digit: Int) : Boolean {
        for (i in 0 until Constants().GRID_SIZE) {
            if (grid[i][column] == digit) {
                return false
            }
        }
        return true
    }

    private fun removeDigits() {
        var digitsToRemove = Constants().GRID_SIZE * Constants().GRID_SIZE - level.numberOfProvidedDitits

        while (digitsToRemove > 0) {
            val randomRow = generateRandomInt(Constants().MIN_DIGIT_INDEX, Constants().MAX_DIGIT_INDEX)
            val randomColumn = generateRandomInt(Constants().MIN_DIGIT_INDEX, Constants().MAX_DIGIT_INDEX)

            if (grid[randomRow][randomColumn] != 0) {
                val digitToRemove = grid[randomRow][randomColumn]
                grid[randomRow][randomColumn] = 0
                if (!Solver().solvable(grid)) {
                    grid[randomRow][randomColumn] = digitToRemove
                } else {
                    digitsToRemove --
                }
            }
        }
    }

    class Builder {
        private lateinit var level: DifficultyLevel

        fun setLevel(level: DifficultyLevel) : Builder {
            this.level = level
            return this
        }

        fun build() : Generator {
            return Generator(this.level)
        }
    }
}