package com.gamopy.sudoku.repository

import android.net.Uri
import com.gamopy.sudoku.data.Resource
import com.gamopy.sudoku.dto.UserProfile
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    val currentUser:FirebaseUser?
    suspend fun login(email:String, password:String) : Resource<FirebaseUser>
    suspend fun signUp(name:String, email:String, password: String): Resource<FirebaseUser>
    suspend fun resetPassword(email: String) : Resource<Nothing>

    suspend fun fetchProfileData() : Resource<UserProfile>

    suspend fun updateProfileImage(uri:Uri) : Resource<Uri>
    fun logout()
}