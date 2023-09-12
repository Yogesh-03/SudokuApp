package com.example.sudokugame

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(SudokuEntity::class), version = 1, exportSchema = false)
abstract class SudokuRoomDatabase : RoomDatabase(){

    abstract fun getSudokuDao(): SudokuDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: SudokuRoomDatabase? = null

        fun getDatabase(context: Context): SudokuRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SudokuRoomDatabase::class.java,
                    "word_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}