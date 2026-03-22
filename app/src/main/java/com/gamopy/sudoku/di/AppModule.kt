package com.gamopy.sudoku.di

import com.gamopy.sudoku.repository.AuthRepository
import com.gamopy.sudoku.repository.AuthRepositoryImpl
import com.gamopy.sudoku.repository.SudokuRepo
import com.gamopy.sudoku.repository.SudokuRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

    @Provides
    fun sudokuRepository(repo:SudokuRepository) : SudokuRepo = repo

    @Provides
    fun provideFirebaseFirestore():FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    fun provideFirebaseStorage():StorageReference = Firebase.storage.reference
}