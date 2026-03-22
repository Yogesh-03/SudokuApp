package com.gamopy.sudoku.model

enum class ActionType( // User performs a Undo action ()
    private val displayName: String
) {
    FILL("Fill"),  // User fills a cell with a number
    CLEAR("Clear"),  // User clears a filled cell
    PENCIL_MARK("Pencil Mark"), // User adds a pencil mark to a cell
    PENCIL_CLEAR("Pencil Clear"),// User clears a pencil mark
    PENCIL_CLEAR_ALL("Pencil Clear All"); // User clears a pencil mark using eraser

    override fun toString(): String {
        return displayName
    }
}