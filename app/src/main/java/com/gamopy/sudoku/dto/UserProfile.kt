package com.gamopy.sudoku.dto

data class UserProfile(
    val name:String,
    val email:String,
    val user:String,
    val profileImage:String?
) {
    constructor() : this("", "", "", null)
}

