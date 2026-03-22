package com.gamopy.sudoku.sudokugenerator


class Constants {
    internal  val GRID_SIZE = 9
    internal  val GRID_SIZE_SQUARE_ROOT = 3
    internal  val MIN_DIGIT_VALUE = 1
    internal  val MAX_DIGIT_VALUE = 9
    internal  val MIN_DIGIT_INDEX = 0
    internal  val MAX_DIGIT_INDEX = 8
    internal  val BOX_SIZE = 3

    companion object {
        const val TAG = "cameraX"
        const val FILE_NAME_FORMAT = "yy-MM-dd-HH-mm-ss-SSS"
        const val REQUEST_CODE_PERMISSION = 123
        val REQUIRED_PERMISSION = arrayOf(android.Manifest.permission.CAMERA)
    }

}