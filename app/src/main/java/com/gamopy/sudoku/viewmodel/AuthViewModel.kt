package com.gamopy.sudoku.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamopy.sudoku.data.Resource
import com.gamopy.sudoku.dto.UserProfile
import com.gamopy.sudoku.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _loginResult = MutableLiveData<Resource<FirebaseUser>?>(null)
    val loginResult: LiveData<Resource<FirebaseUser>?> = _loginResult

    private val _signupResult = MutableLiveData<Resource<FirebaseUser>?>(null)
    val signupResult: LiveData<Resource<FirebaseUser>?> = _signupResult

    private val _passResetResult = MutableLiveData<Resource<Nothing>?>(null)
    val passResetResult: LiveData<Resource<Nothing>?> = _passResetResult

    private val _fetchProfileResult = MutableLiveData<Resource<UserProfile>?>(null)
    val fetchProfileResult: LiveData<Resource<UserProfile>?> = _fetchProfileResult

    private val _updateprofileImage = MutableLiveData<Resource<Uri>?>(null)
    val updateProfileImage: LiveData<Resource<Uri>?> = _updateprofileImage

    val currentUser:FirebaseUser?
        get() = repository.currentUser




    init {
        if (repository.currentUser!=null){
            _loginResult.value = Resource.Success(repository.currentUser!!)
        }
    }
    fun login(email:String, password:String) = viewModelScope.launch {
        _loginResult.value = Resource.Loading
        val result = repository.login(email, password)
            _loginResult.value = result
    }

    fun signUp(name:String, email:String, password:String) = viewModelScope.launch {
        _signupResult.value = Resource.Loading
        val result = repository.signUp(name, email, password)
        _signupResult.value = result
    }

    fun resetPassword(email: String) = viewModelScope.launch {
        _passResetResult.value = Resource.Loading
        val result = repository.resetPassword(email)
        _passResetResult.value = result
    }

    fun fetchProfileData() = viewModelScope.launch {
        _fetchProfileResult.value = Resource.Loading
        val result = repository.fetchProfileData()
        _fetchProfileResult.value = result
    }

    /**
     * @param uri
     * Uri of Image to be uploaded in the database
     */
    fun updateProfileImage(uri:Uri) = viewModelScope.launch {
        _updateprofileImage.value = Resource.Loading
        val result = repository.updateProfileImage(uri)
        _updateprofileImage.value = result
    }

    fun logout(){
            repository.logout()
        _loginResult.value = null
        _signupResult.value = null
        
    }
}